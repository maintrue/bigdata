package com.main.dmp.report
import com.main.dmp.etl.PmtETLRunner
import com.main.dmp.utils.KuduHelper
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Schema, Type}
import org.apache.spark.sql.DataFrame

object AdsRegionProcessor extends ReportProcessor {
  private val ORIGIN_TEMP_TABLE: String = "adsregion"
  private val MID_TEMP_TABLE: String = "midtable"

  override def process(origin: DataFrame): DataFrame = {
    lazy val sql =
      s"""
         |SELECT t.region,
         |       t.city,
         |       sum(CASE
         |               WHEN (t.requestmode = 1
         |                     AND t.processnode >= 1) THEN 1
         |               ELSE 0
         |           END) AS orginal_req_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 1
         |                     AND t.processnode >= 2) THEN 1
         |               ELSE 0
         |           END) AS valid_req_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 1
         |                     AND t.processnode = 3) THEN 1
         |               ELSE 0
         |           END) AS ad_req_cnt,
         |       sum(CASE
         |               WHEN (t.adplatformproviderid >= 100000
         |                     AND t.iseffective = 1
         |                     AND t.isbilling =1
         |                     AND t.isbid = 1
         |                     AND t.adorderid != 0) THEN 1
         |               ELSE 0
         |           END) AS join_rtx_cnt,
         |       sum(CASE
         |               WHEN (t.adplatformproviderid >= 100000
         |                     AND t.iseffective = 1
         |                     AND t.isbilling =1
         |                     AND t.iswin = 1) THEN 1
         |               ELSE 0
         |           END) AS success_rtx_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 2
         |                     AND t.iseffective = 1) THEN 1
         |               ELSE 0
         |           END) AS ad_show_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 3
         |                     AND t.iseffective = 1) THEN 1
         |               ELSE 0
         |           END) AS ad_click_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 2
         |                     AND t.iseffective = 1
         |                     AND t.isbilling = 1) THEN 1
         |               ELSE 0
         |           END) AS media_show_cnt,
         |       sum(CASE
         |               WHEN (t.requestmode = 3
         |                     AND t.iseffective = 1
         |                     AND t.isbilling = 1) THEN 1
         |               ELSE 0
         |           END) AS media_click_cnt,
         |       sum(CASE
         |               WHEN (t.adplatformproviderid >= 100000
         |                     AND t.iseffective = 1
         |                     AND t.isbilling = 1
         |                     AND t.iswin = 1
         |                     AND t.adorderid > 20000
         |                     AND t.adcreativeid > 200000) THEN floor(t.winprice / 1000)
         |               ELSE 0
         |           END) AS dsp_pay_money,
         |       sum(CASE
         |               WHEN (t.adplatformproviderid >= 100000
         |                     AND t.iseffective = 1
         |                     AND t.isbilling = 1
         |                     AND t.iswin = 1
         |                     AND t.adorderid > 20000
         |                     AND t.adcreativeid > 200000) THEN floor(t.adpayment / 1000)
         |               ELSE 0
         |           END) AS dsp_cost_money
         |FROM $ORIGIN_TEMP_TABLE t
         |GROUP BY t.region,
         |         t.city
    """.stripMargin

    lazy val sqlWithRate =
      s"""
         |SELECT t.*,
         |       t.success_rtx_cnt / t.join_rtx_cnt AS success_rtx_rate,
         |       t.media_click_cnt / t.ad_click_cnt AS ad_click_rate
         |FROM $MID_TEMP_TABLE t
         |WHERE t.success_rtx_cnt != 0
         |  AND t.join_rtx_cnt != 0
         |  AND t.media_click_cnt != 0
         |  AND t.ad_click_cnt != 0
    """.stripMargin

    origin.createOrReplaceTempView(ORIGIN_TEMP_TABLE)
    val midTemp = origin.sparkSession.sql(sql)
    midTemp.createOrReplaceTempView(MID_TEMP_TABLE)
    origin.sparkSession.sql(sqlWithRate)
  }

  override def sourceTableName(): String = {
    PmtETLRunner.ODS_TABLE_NAME
  }

  override def targetTableName(): String = {
    "ANALYSIS_ADS_REGION_" + KuduHelper.formattedDate()
  }

  override def targetTableSchema(): Schema = {
    import scala.collection.JavaConverters._

    new Schema(
      List(
        new ColumnSchemaBuilder("region", Type.STRING).nullable(false).key(true).build(),
        new ColumnSchemaBuilder("city", Type.STRING).nullable(false).key(true).build(),
        new ColumnSchemaBuilder("orginal_req_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("valid_req_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("ad_req_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("join_rtx_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("success_rtx_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("ad_show_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("ad_click_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("media_show_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("media_click_cnt", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("dsp_pay_money", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("dsp_cost_money", Type.INT64).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("success_rtx_rate", Type.DOUBLE).nullable(true).key(false).build(),
        new ColumnSchemaBuilder("ad_click_rate", Type.DOUBLE).nullable(true).key(false).build()
      ).asJava
    )
  }

  override def targetTableKeys(): List[String] = {
    List("region", "city")
  }
}
