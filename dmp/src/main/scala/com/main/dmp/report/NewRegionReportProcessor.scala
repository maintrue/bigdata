package com.main.dmp.report
import com.main.dmp.etl.PmtETLRunner
import com.main.dmp.utils.KuduHelper
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Schema, Type}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.count

object NewRegionReportProcessor extends ReportProcessor {

  override def process(origin: DataFrame): DataFrame = {
    import origin.sparkSession.implicits._
    val result = origin
      .groupBy("region", "city")
      .agg(count("*") as "count")
      .select('region,'city,'count)
    result
  }

  override def sourceTableName(): String = PmtETLRunner.ODS_TABLE_NAME

  override def targetTableName(): String = "ANALYSIS_REGION_" + KuduHelper.formattedDate()

  import scala.collection.JavaConverters._
  override def targetTableSchema(): Schema = new Schema(List(
      new ColumnSchemaBuilder("region", Type.STRING).nullable(false).key(true).build,
      new ColumnSchemaBuilder("city", Type.STRING).nullable(false).key(true).build(),
      new ColumnSchemaBuilder("count", Type.INT64).nullable(false).key(false).build()
    ).asJava
  )

  override def targetTableKeys(): Seq[String] = Seq("region", "city")
}
