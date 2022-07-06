package com.main.dmp.utils

import com.typesafe.config.{Config, ConfigFactory, ConfigValueType}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.SparkSession

class SparkConfigHelper(builder: SparkSession.Builder) {

  // 加载配置文件
  private val config: Config = ConfigFactory.load("spark")

  def loadConfig(): SparkSession.Builder = {
    import scala.collection.JavaConverters._

    for (entry <- config.entrySet().asScala) {
      // 3. 获取其中所有的 key, 和所有的 value
      val key = entry.getKey
      val value = entry.getValue.unwrapped().asInstanceOf[String]
      // println((key,value))
      // 判断: 判断配置的来源
      val origin = entry.getValue.origin().filename()
      if (StringUtils.isNotBlank(origin)) {
        // 4. 设置给 builder
        builder.config(key, value)
      }
    }

    builder
  }
}

// 提供伴生对象的意义在于两点: 1. 更方便的创建配置类, 2. 提供隐式转换, 3. 以后可能需要获取某个配置项
object SparkConfigHelper {

  def apply(builder: SparkSession.Builder): SparkConfigHelper = {
    new SparkConfigHelper(builder)
  }

  // 提供隐式转换, 将 SparkSession 转为 ConfigHelper 对象, 从而提供配置加载
  implicit def setSparkSession(builder: SparkSession.Builder) = {
    SparkConfigHelper(builder)
  }

//  def main(args: Array[String]): Unit = {
//    val session = SparkSession.builder()
//      .appName("test")
//      .master("local[6]")
//      .loadConfig()
//      .getOrCreate()
//
//  }
}