package Study.ScalaSpark

object RDDAPI {
  
  def main(args:Array[String]):Unit={
    
//    mapPartitionsWithIndex
val func = (index: Int, iter: Iterator[(String)]) => {
  iter.map(x => "[partID:" +  index + ", val: " + x + "]")
}


//mapPartitionsWithIndex
val func = (index: Int, iter: Iterator[Int]) => {
  iter.map(x => "[partID:" +  index + ", val: " + x + "]")
}
val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7,8,9), 2)
rdd1.mapPartitionsWithIndex(func).collect

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//aggregate

def func1(index: Int, iter: Iterator[(Int)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7,8,9), 2)
rdd1.mapPartitionsWithIndex(func1).collect
rdd1.aggregate(0)(math.max(_, _), _ + _)
rdd1.aggregate(5)(math.max(_, _), _ + _)


val rdd2 = sc.parallelize(List("a","b","c","d","e","f"),2)
def func2(index: Int, iter: Iterator[(String)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
rdd2.aggregate("")(_ + _, _ + _)
rdd2.aggregate("=")(_ + _, _ + _)

val rdd3 = sc.parallelize(List("12","23","345","4567"),2)
rdd3.aggregate("")((x,y) => math.max(x.length, y.length).toString, (x,y) => x + y)

val rdd4 = sc.parallelize(List("12","23","345",""),2)
rdd4.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y)

val rdd5 = sc.parallelize(List("12","23","","345"),2)
rdd5.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y)

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//aggregateByKey

val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 2)
def func2(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
  iter.map(x => "[partID:" +  index + ", val: " + x + "]")
}
pairRDD.mapPartitionsWithIndex(func2).collect
pairRDD.aggregateByKey(0)(math.max(_, _), _ + _).collect
pairRDD.aggregateByKey(100)(math.max(_, _), _ + _).collect

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//checkpoint
sc.setCheckpointDir("hdfs://node-1.edu360.cn:9000/ck")
val rdd = sc.textFile("hdfs://node-1.edu360.cn:9000/wc").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
rdd.checkpoint
rdd.isCheckpointed
rdd.count
rdd.isCheckpointed
rdd.getCheckpointFile

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//coalesce, repartition
val rdd1 = sc.parallelize(1 to 10, 10)
val rdd2 = rdd1.coalesce(2, false)
rdd2.partitions.length

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//collectAsMap
val rdd = sc.parallelize(List(("a", 1), ("b", 2)))
rdd.collectAsMap

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//combineByKey
val rdd1 = sc.textFile("hdfs://node-1.edu360.cn:9000/wc").flatMap(_.split(" ")).map((_, 1))
val rdd2 = rdd1.combineByKey(x => x, (a: Int, b: Int) => a + b, (m: Int, n: Int) => m + n)
rdd2.collect

val rdd3 = rdd1.combineByKey(x => x + 10, (a: Int, b: Int) => a + b, (m: Int, n: Int) => m + n)
rdd3.collect


val rdd4 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val rdd5 = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
val rdd6 = rdd5.zip(rdd4)
val rdd7 = rdd6.combineByKey(List(_), (x: List[String], y: String) => x :+ y, (m: List[String], n: List[String]) => m ++ n)

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//countByKey 

val rdd1 = sc.parallelize(List(("a", 1), ("b", 2), ("b", 2), ("c", 2), ("c", 1)))
rdd1.countByKey
rdd1.countByValue

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//filterByRange

val rdd1 = sc.parallelize(List(("e", 5), ("c", 3), ("d", 4), ("c", 2), ("a", 1)))
val rdd2 = rdd1.filterByRange("b", "d")
rdd2.colllect

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//flatMapValues
val a = sc.parallelize(List(("a", "1 2"), ("b", "3 4")))
rdd3.flatMapValues(_.split(" "))

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//foldByKey

val rdd1 = sc.parallelize(List("dog", "wolf", "cat", "bear"), 2)
val rdd2 = rdd1.map(x => (x.length, x))
val rdd3 = rdd2.foldByKey("")(_+_)

val rdd = sc.textFile("hdfs://node-1.edu360.cn:9000/wc").flatMap(_.split(" ")).map((_, 1))
rdd.foldByKey(0)(_+_)

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//foreachPartition
val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9), 3)
rdd1.foreachPartition(x => println(x.reduce(_ + _)))

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//keyBy
val rdd1 = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val rdd2 = rdd1.keyBy(_.length)
rdd2.collect

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//keys values
val rdd1 = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val rdd2 = rdd1.map(x => (x.length, x))
rdd2.keys.collect
rdd2.values.collect

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
mapPartitions( it: Iterator => {it.map(x => x * 10)})

    
    
  }
}