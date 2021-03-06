package ScalaBase.ScalaLearn

object CallByName {
  def currentTime(): Long ={
        println("打印系统当前时间,单位纳秒")
        System.nanoTime()
    }

    def delayedf(f: => Long): Unit = {
        println("delayed ===============")
        println("time = " + f)
    }

    def delayed(time: Long) = {
        println("delayed1 ===============")
        println("time1 = " + time)
    }

    def main(args:Array[String]):Unit={
      // 调用方式一
      val ct = currentTime _
      delayedf(ct())
  
      println("----------------------")
  
      // 调用方式二
      val time = currentTime()
      delayed(time)
    }
    
    
    

  
  
}