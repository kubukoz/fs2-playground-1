package com.quiquedeveloper.fs2
import cats.effect.IO
import fs2.async.mutable.Queue
import fs2.{Scheduler, Stream}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object AMain extends App {
  val producer: Stream[IO, Int] =
    Scheduler[IO](5).flatMap(
      scheduler =>
        scheduler
          .awakeEvery[IO](1 second)
          .flatMap(_ =>
            Stream.eval(IO {
              Random.nextInt
            })))

  val evenQueue: Stream[IO, Queue[IO, Int]] =
    Stream.eval(fs2.async.boundedQueue[IO, Int](10))

  val oddQueue: Stream[IO, Queue[IO, Int]] =
    Stream.eval(fs2.async.boundedQueue[IO, Int](10))

  class PrintEven(input: Stream[IO, Queue[IO, Int]]) {
    val start = for {
      inputQ <- input
      element <- inputQ.dequeue
      _ <- Stream.eval(IO { println(s"im even: $element") })
    } yield ()
  }

  class PrintOdd(input: Stream[IO, Queue[IO, Int]]) {
    val start = for {
      inputQ <- input
      element <- inputQ.dequeue
      _ <- Stream.eval(IO { println(s"im even: $element") })
    } yield ()
  }

  val producerStream = for {
    evenQueueQ <- evenQueue
    oddQueueQ <- oddQueue
    element <- producer
    _ <- Stream.eval {
      if (element % 2 == 0) evenQueueQ.enqueue1(element)
      else oddQueueQ.enqueue1(element)
    } concurrently new PrintEven(evenQueue).start.drain concurrently new PrintOdd(
      oddQueue).start.drain
  } yield ()

  producerStream.compile.drain.unsafeRunSync()
}
