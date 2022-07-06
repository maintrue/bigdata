package com.main.dmp.utils

import com.typesafe.config.ConfigFactory
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.kudu.Schema
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

import java.util.Date

// 主题设计思路就是将 SparkSession 或者 DataFrame 隐式转换为 KuduHelper, 在 KuduHelper 中提供帮助方法
class KuduHelper {
  private var spark: SparkSession = _
  private var dataset: Dataset[_] = _
  private val config = ConfigFactory.load("kudu")
  private val KUDU_MASTERS = config.getString("kudu.common.master")

  // 在 Helper 内部读取配置文件, 创建 KuduContext
  private var kuduContext: KuduContext = _

  // 将 SparkSession 转为 KuduHelper 时调用
  def this(spark: SparkSession) = {
    this()
    this.spark = spark
    // 可以设置超时时间
    this.kuduContext = new KuduContext(KUDU_MASTERS, spark.sparkContext,Some(900000))
  }

  // 将 Dataset 转为 KuduHelper 时调用
  def this(dataset: Dataset[_]) = {
    this(dataset.sparkSession)
    this.dataset = dataset
  }

  def createKuduTable(tableName: String, schema: Schema,
                      // 此方法就是设计目标 SparkSession.createKuduTable(tableName) 中被调用的方法
                      partitionKey: Seq[String]): Unit = {
    if (kuduContext.tableExists(tableName)) {
//      throw new RuntimeException("kuduContext.tableExists is true, Please check.")
      kuduContext.deleteTable(tableName)
    }

    import scala.collection.JavaConverters._
    val options = new CreateTableOptions()
      .setNumReplicas(config.getInt("kudu.common.factor"))
      .addHashPartitions(partitionKey.asJava, 6)

    kuduContext.createTable(tableName, schema, options)
  }

  // 此方法就是设计目标 DataFrame.saveToKudu(tableName) 中被调用的方法
  def saveToKudu(tableName: String): Unit = {
    if (dataset == null) {
      throw new RuntimeException("请在 DataFrame 上调用 saveToKudu")
    }

    import org.apache.kudu.spark.kudu._

    dataset.write
      .option("kudu.table", tableName)
      .option("kudu.master", KUDU_MASTERS)
      .mode(SaveMode.Append)
      .kudu
  }

  // 此方法就是设计目标 SparkSession.readKuduTable(tableName) 中被调用的方法
  def readKuduTable(tableName: String): Option[DataFrame] = {
    if (StringUtils.isBlank(tableName)) {
      throw new RuntimeException("请传入合法的表名")
    }

    import org.apache.kudu.spark.kudu._

    if (kuduContext.tableExists(tableName)) {
      val dataFrame = spark.read
        .option("kudu.master", KUDU_MASTERS)
        .option("kudu.table", tableName)
        .kudu

      Some(dataFrame)
    } else {
      None
    }
  }

}

object KuduHelper {

  // 将 SparkSession 转为 KuduHelper
  implicit def sparkToKuduContext(spark: SparkSession): KuduHelper = {
    new KuduHelper(spark)
  }

  // 将 Dataset 转为 KuduHelper
  implicit def datasetToKuduContext(dataset: Dataset[_]): KuduHelper = {
    new KuduHelper(dataset)
  }

  def formattedDate(): String = {
    FastDateFormat.getInstance("yyyMMdd").format(new Date())
  }
}