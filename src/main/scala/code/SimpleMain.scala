package code

object SimpleMain {
  import Helpers._

  val eagerExample =
    work("A")

  val lazyExample=
    () => work("B")

  def sequentialExample =
    ???

  def verySequentialExample =
    ???

  def sleepExample =
    ???

  def happyPathExample =
    ???

  def unhappyPathExample =
    ???

  def mapReduceExample =
    ???

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
