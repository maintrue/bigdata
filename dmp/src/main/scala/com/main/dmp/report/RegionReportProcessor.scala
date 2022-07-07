package com.main.dmp.report

import com.main.dmp.etl.PmtETLRunner
import com.main.dmp.utils.KuduHelper
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Schema, Type}
import org.apache.spark.sql.{DataFrame, SparkSession}

object RegionReportProcessor {

  private val ORIGIN_TABLE_NAME = PmtETLRunner.ODS_TABLE_NAME
  private val TARGET_TABLE_NAME = "ANALYSIS_REGION_" + KuduHelper.formattedDate()

  def main(args: Array[String]): Unit = {
    import com.main.dmp.utils.SparkConfigHelper._
    import com.main.dmp.utils.KuduHelper._
    import org.apache.spark.sql.functions._

    val spark = SparkSession.builder()
      .appName("region_etl")
      .master("local[6]")
      .loadConfig()
      .getOrCreate()

    val origin: Option[DataFrame] = spark.readKuduTable(ORIGIN_TABLE_NAME)

    if(origin.isEmpty) {
      return
    }

    import spark.implicits._
    val result = origin.get
      .groupBy("region", "city")
      .agg(count("*") as "count")
      .select('region,'city,'count)

    spark.createKuduTable(TARGET_TABLE_NAME,schema,keys)
    result.saveToKudu(TARGET_TABLE_NAME)

    spark.stop()
  }

  import scala.collection.JavaConverters._
  private val schema = new Schema(List(
    new ColumnSchemaBuilder("region", Type.STRING).nullable(false).key(true).build,
    new ColumnSchemaBuilder("city", Type.STRING).nullable(false).key(true).build(),
    new ColumnSchemaBuilder("count", Type.INT64).nullable(false).key(false).build()
  ).asJava
  )

  private val keys = Seq("region", "city")
}
