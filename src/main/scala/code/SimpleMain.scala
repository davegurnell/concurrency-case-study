package code

object SimpleMain {
  import Helpers._

  val eagerExample =
    work("A")

  val lazyExample=
    () => work("B")

  def sleepExample =
    log("C") {
      Thread.sleep(1000)
      work("D")
    }

  def sequentialExample = {
    log("D") {
      (
        work("E"),
        work("F")
      )
    }
  }

  def verySequentialExample =
    log("G") {
      (1 to 20).toList.map(work)
    }

  def happyPathExample =
    log("H") {
      try {
        println("Acquiring resource")
        log("I") {
          42
        }
      } finally {
        println("Releasing resource")
      }
    }

  def unhappyPathExample =
    log("J") {
      try {
        println("Acquiring resource")
        log("K") {
          ???
        }
      } finally {
        println("Releasing resource")
      }
    }

  def mapReduceExample =
    log("L") {
      (1 to FIB_MAX).map(fib).sum
    }

  def runExample(func: => Any): Unit = {
    val ans = func
    println(s"Final result: $ans")
  }

  def main(args: Array[String]): Unit = {
    args(0) match {
      case "eager" => runExample(eagerExample)
      case "lazy" => runExample(lazyExample)
      case "seq" => runExample(sequentialExample)
      case "vseq" => runExample(verySequentialExample)
      case "happy" => runExample(happyPathExample)
      case "unhappy" => runExample(unhappyPathExample)
      case "mapreduce" => runExample(mapReduceExample)
    }
  }
}
