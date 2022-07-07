package com.main.dmp.report

import org.apache.kudu.Schema
import org.apache.spark.sql.DataFrame

trait ReportProcessor {

  def process(origin: DataFrame): DataFrame

  def sourceTableName(): String

  def targetTableName(): String

  def targetTableSchema(): Schema

  def targetTableKeys(): Seq[String]

}
