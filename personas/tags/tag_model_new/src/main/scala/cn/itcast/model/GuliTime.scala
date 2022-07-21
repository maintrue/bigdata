package cn.itcast.model

import scala.io.Source

object GuliTime {

  def main(args: Array[String]): Unit = {

    val filepath ="C:\\Users\\dell\\Desktop\\1.txt"
    val source = Source.fromFile(filepath, "UTF-8")//创建一个BufferedSource对象，读取格式为“UTF-8”
    val lines = source.getLines().toArray//读取所有行，每一行就是一个数组元素
    source.close()//BufferedSource对象是一个运行在内存的过程，使用完及时关闭避免资源浪费

    val i = lines.map(line => {
      val strings = line.split(":")
      val mini = strings(0).toInt
      val senconds = strings(1).toInt
      mini * 60 + senconds
    }).sum


    println(i / 60 / 60)

  }

}
