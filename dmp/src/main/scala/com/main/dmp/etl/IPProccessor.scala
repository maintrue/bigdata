package com.main.dmp.etl

import com.maxmind.geoip.LookupService
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DoubleType, StringType}
import org.apache.spark.sql.{Dataset, Row}
import org.lionsoul.ip2region.{DbConfig, DbSearcher}

object IPProccessor extends Processor {

  @Override
  def process(dataset: Dataset[Row]): Dataset[Row] = {
    val dataConverted: RDD[Row] = dataset
      .rdd
      // 通过 mapPartitions 算子, 对每一个分区数据调用 convertIPtoLocation 进行转换, 需要注意的是, 这个地方已经被转为 RDD 了, 而不是 DataFrame, 因为 DataFrame 在转换中不能更改 Schema
      .mapPartitions(convertIPtoLocation)

    val schema = dataset.schema
      .add("region", StringType)
      .add("city", StringType)
      .add("longitude", DoubleType)
      .add("latitude", DoubleType)

    // 新的 row 对象的 RDD 结合 被扩充过的 Schema, 合并生成新的 DataFrame 返回给 Processor
    val completeDataFrame = dataset
      .sparkSession
      .createDataFrame(dataConverted, schema)

    completeDataFrame
  }

  // convertIPtoLocation 主要做的事情是扩充原来的 row, 增加四个新的列
  def convertIPtoLocation(iterator: Iterator[Row]): Iterator[Row] = {
    val searcher = new DbSearcher(new DbConfig(), "dmp/dataset/ip2region.db")

    val lookupService = new LookupService(
      "dmp/dataset/GeoLiteCity.dat",
      LookupService.GEOIP_MEMORY_CACHE)

    iterator.map(row => {
      val ip = row.getAs[String]("ip")
      //  获取省市中文名
      val regionData = searcher.btreeSearch(ip).getRegion.split("\\|")
      val region = regionData(2)
      val city = regionData(3)

      // 获取经纬度
      val location = lookupService.getLocation(ip)
      val longitude = location.longitude.toDouble
      val latitude = location.latitude.toDouble

      // 根据原始 row 生成新的 row, 新的 row 中包含了省市和经纬度信息
      val rowSeq = row.toSeq :+ region :+ city :+ longitude :+ latitude
      Row.fromSeq(rowSeq)
    })
  }
}
