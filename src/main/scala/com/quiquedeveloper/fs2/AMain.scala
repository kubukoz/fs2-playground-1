package com.quiquedeveloper.fs2

import cats.effect.IO
import fs2.async.mutable.Queue
import fs2.{Scheduler, Stream, StreamApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

object AMain extends StreamApp[IO] {
  val producer: Stream[IO, Int] =
    Scheduler[IO](5).flatMap(
      scheduler =>
        scheduler
          .awakeEvery[IO](1 second)
          .evalMap(_ =>
            IO {
              Random.nextInt
            }))

  val evenQueue: Stream[IO, Queue[IO, Int]] =
    Stream.eval(fs2.async.boundedQueue[IO, Int](10))

  val oddQueue: Stream[IO, Queue[IO, Int]] =
    Stream.eval(fs2.async.boundedQueue[IO, Int](10))

  class PrintEven(inputQ: Queue[IO, Int]) {
    val start = for {
      element <- inputQ.dequeue
      _ <- Stream.eval(IO { println(s"im even: $element") })
    } yield ()
  }

  class PrintOdd(inputQ: Queue[IO, Int]) {
    val start = for {
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
    } concurrently new PrintEven(evenQueueQ).start concurrently new PrintOdd(
      oddQueueQ
    ).start
  } yield ()

  override def stream(
    args: List[String],
    requestShutdown: IO[Unit]
  ): Stream[IO, StreamApp.ExitCode] =
    producerStream.drain.as(StreamApp.ExitCode.Success)
}
