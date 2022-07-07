package com.main.dmp.report

import org.apache.spark.sql.SparkSession

object DailyReportRunner {

  def main(args: Array[String]): Unit = {
    import com.main.dmp.utils.SparkConfigHelper._
    import com.main.dmp.utils.KuduHelper._

    val spark = SparkSession.builder()
      .master("local[6]")
      .appName("pmt_etl")
      .loadConfig()
      .getOrCreate()

    val processors: List[ReportProcessor] = List(
      NewRegionReportProcessor
    )

    for (processor <- processors) {
      val origin = spark.readKuduTable(processor.sourceTableName())

      if (origin.isDefined) {
        val result = processor.process(origin.get)
        result.createKuduTable(processor.targetTableName(), processor.targetTableSchema(), processor.targetTableKeys())
        result.saveToKudu(processor.targetTableName())
      }
    }

    spark.stop()
  }
}
