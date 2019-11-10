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

  def parallelExample =
    ??? // Impossible

  def verySequentialExample =
    log("G") {
      (1 to 20).toList.map(work)
    }

  def veryParallelExample =
    ??? // Impossible

  def happyPathExample =
    try {
      println("Acquiring resource")
      42
    } finally {
      println("Releasing resource")
    }

  def unhappyPathExample =
    try {
      println("Acquiring resource")
      ???
    } finally {
      println("Releasing resource")
    }

  def runExample(numThreads: Int)(func: => Any): Unit = {
    val ans = func
    println(s"Final result: $ans")
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)(eagerExample)
      case "lazy" => runExample(numThreads = 1)(lazyExample)
      case "seq1" => runExample(numThreads = 1)(sequentialExample)
      case "seq2" => runExample(numThreads = 2)(sequentialExample)
      case "par1" => runExample(numThreads = 1)(parallelExample)
      case "par2" => runExample(numThreads = 2)(parallelExample)
      case "vseq1" => runExample(numThreads = 1)(verySequentialExample)
      case "vseq2" => runExample(numThreads = 2)(verySequentialExample)
      case "vpar1" => runExample(numThreads = 1)(veryParallelExample)
      case "vpar2" => runExample(numThreads = 2)(veryParallelExample)
      case "vpar3" => runExample(numThreads = 3)(veryParallelExample)
      case "vpar4" => runExample(numThreads = 4)(veryParallelExample)
      case "vpar8" => runExample(numThreads = 8)(veryParallelExample)
      case "happy" => runExample(numThreads = 1)(happyPathExample)
      case "unhappy" => runExample(numThreads = 2)(unhappyPathExample)
    }
  }
}
