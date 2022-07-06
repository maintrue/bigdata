package com.main.dmp.etl

import org.apache.spark.sql.{Dataset, Row}

trait Processor {

  def process(dataset: Dataset[Row]): Dataset[Row]
}
