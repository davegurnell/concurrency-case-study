package code

import cats.effect.Sync
import cats.effect.implicits._
import cats.implicits._

object Helpers {
  // Increase this to slow everything down:
  val FIBONACCI_TARGET = 38

  private def fib(n: Int): BigDecimal =
    if(n <= 1) 1 else fib(n - 1) + fib(n - 2)

  def work[A](name: A): A =
    log(name.toString) {
      fib(FIBONACCI_TARGET)
      name
    }

  def log[A](name: String)(block: => A): A = {
    val start = System.currentTimeMillis()
    try {
      println(s"Starting $name on ${Thread.currentThread().getName}")
      block
    } finally {
      val end = System.currentTimeMillis()
      println(s"Finished $name in ${end - start} ms")
    }
  }

  def logF[F[_]: Sync, A](name: Int)(block: => F[A]): F[A] =
    logF(name.toString)(block)

  def logF[F[_]: Sync, A](name: String)(block: => F[A]): F[A] = {
    for {
      start <- Sync[F].delay(System.currentTimeMillis())
      _     <- Sync[F].delay(println(s"Starting $name"))
      ans   <- block.guarantee {
                 Sync[F].delay(System.currentTimeMillis()).map { end =>
                   println(s"Finished $name in ${end - start} ms")
                 }
               }
    } yield ans
  }
}
