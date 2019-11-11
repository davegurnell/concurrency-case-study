package code

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO}
import cats.implicits._

import scala.concurrent.duration._
import scala.concurrent.{Future, _}

object FutureMain {

  import Helpers._

  val eagerExample =
    Future.successful(work("A"))

  def lazyExample(implicit ec: ExecutionContext) =
    Future(work("B"))

  def sleepExample(implicit ec: ExecutionContext) = {
    import akka.actor.ActorSystem
    import akka.pattern.Patterns.after

    val as = ActorSystem()

    after(1.second, as.scheduler, ec, () => Future(work("C")))
  }

  def sequentialExample(implicit ec: ExecutionContext) = {
    for {
      a <- Future(work("G"))
      b <- Future(work("H"))
    } yield (a, b)
  }

  def parallelExample(implicit ec: ExecutionContext) = {
    val futureA = Future(work("F"))
    val futureB = Future(work("G"))
    for {
      a <- futureA
      b <- futureB
    } yield (a, b)
  }

  def verySequentialExample(implicit ec: ExecutionContext) = {
    val inputs: List[Future[Int]] =
      (1 to 20).toList.map(Future.successful)

    val seed: Future[List[Int]] =
      Future.successful(List.empty[Int])

    inputs.foldRight(seed) { (index, accum) =>
      for {
        i <- index
        t <- accum
        h <- Future(work(i))
      } yield h :: t
    }
  }

  def veryParallelExample(implicit ec: ExecutionContext) =
    Future.traverse((1 to 20).toList) { i =>
      Future(work(i))
    }

  def raceExample(implicit ec: ExecutionContext) =
    Future.firstCompletedOf(List(
      Future(work("H1")),
      Future(work("I1")) *> Future(work("I2")) *> Future(work("I3"))
    ))

  def acquire(implicit ec: ExecutionContext): Future[Int] =
    Future {
      val resource = (Math.random * 1000).toInt
      println("Acquiring resource: " + resource)
      resource
    }

  def release(resource: Int)(implicit ec: ExecutionContext): Future[Unit] =
    Future {
      println("Releasing resource: " + resource)
    }

  def happyPathExample(implicit ec: ExecutionContext) =
    acquire.flatMap { resource =>
      Future(work("H"))
        .flatMap(ans => release(resource) *> Future.successful(ans))
        .recoverWith { case exn => release(resource) *> Future.failed(exn) }
    }

  def unhappyPathExample(implicit ec: ExecutionContext) =
    acquire.flatMap { resource =>
      Future(???)
        .flatMap(ans => release(resource) *> Future.successful(ans))
        .recoverWith { case exn => release(resource) *> Future.failed(exn) }
    }

  def mapReduceExample(implicit ec: ExecutionContext) =
    Future.traverse((1 to FIB_MAX).toList) { i =>
      Future(log(i.toString)(fib(i)))
    }.map(_.combineAll)

  def runExample(numThreads: Int)(func: ExecutionContext => Future[Any]): Unit = {
    val service: ExecutorService =
      Executors.newFixedThreadPool(numThreads)

    val context: ExecutionContext =
      ExecutionContext.fromExecutor(service)

    try {
      val ans = Await.result(func(context), Duration.Inf)
      println(s"Final result: $ans")
    } finally {
      service.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)(_ => eagerExample)
      case "lazy" => runExample(numThreads = 1)(lazyExample(_))
      case "seq1" => runExample(numThreads = 1)(sequentialExample(_))
      case "seq2" => runExample(numThreads = 2)(sequentialExample(_))
      case "par1" => runExample(numThreads = 1)(parallelExample(_))
      case "par2" => runExample(numThreads = 2)(parallelExample(_))
      case "vseq1" => runExample(numThreads = 1)(verySequentialExample(_))
      case "vseq2" => runExample(numThreads = 2)(verySequentialExample(_))
      case "vpar1" => runExample(numThreads = 1)(veryParallelExample(_))
      case "vpar2" => runExample(numThreads = 2)(veryParallelExample(_))
      case "vpar4" => runExample(numThreads = 4)(veryParallelExample(_))
      case "vpar8" => runExample(numThreads = 8)(veryParallelExample(_))
      case "race" => runExample(numThreads = 2)(raceExample(_))
      case "happy" => runExample(numThreads = 2)(happyPathExample(_))
      case "unhappy" => runExample(numThreads = 2)(unhappyPathExample(_))
      case "mapreduce" => runExample(numThreads = 8)(mapReduceExample(_))
    }
  }
}
