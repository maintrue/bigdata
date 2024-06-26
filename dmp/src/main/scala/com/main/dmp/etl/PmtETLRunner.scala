package com.main.dmp.etl

import com.main.dmp.utils.KuduHelper
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder
import org.apache.kudu.{Schema, Type}
import org.apache.spark.sql.{Column, Dataset, Row, SparkSession}

object PmtETLRunner {

  def main(args: Array[String]): Unit = {
    import com.main.dmp.utils.SparkConfigHelper._
    import com.main.dmp.utils.KuduHelper._

    // 创建 SparkSession
    val spark = SparkSession.builder()
      .master("local[6]")
      .appName("pmt_etl")
      .loadConfig()
      .getOrCreate()

    import spark.implicits._

    // 读取数据
    val originDataset = spark.read.json("dmp/dataset/pmt.json")
    // 调用 IPConverter, 传入 originDataset, 生成包含经纬度和省市的 DataFrame
    val ipConvertedResult: Dataset[Row] = IPProccessor.process(originDataset)


    // 要 Select 的列们, 用于组织要包含的结果集中的数据
    // 因为太多, 不再此处展示, 若要查看, 请移步代码工程
    val selectRows: Seq[Column] = Seq('sessionid, 'advertisersid, 'adorderid, 'adcreativeid, 'adplatformproviderid,
      'sdkversion, 'adplatformkey, 'putinmodeltype, 'requestmode, 'adprice, 'adppprice,
      'requestdate, 'ip, 'appid, 'appname, 'uuid, 'device, 'client, 'osversion, 'density,
      'pw, 'ph, 'longitude, 'latitude, 'region, 'city, 'ispid, 'ispname, 'networkmannerid,
      'networkmannername, 'iseffective, 'isbilling, 'adspacetype, 'adspacetypename,
      'devicetype, 'processnode, 'apptype, 'district, 'paymode, 'isbid, 'bidprice, 'winprice,
      'iswin, 'cur, 'rate, 'cnywinprice, 'imei, 'mac, 'idfa, 'openudid, 'androidid,
      'rtbprovince, 'rtbcity, 'rtbdistrict, 'rtbstreet, 'storeurl, 'realip, 'isqualityapp,
      'bidfloor, 'aw, 'ah, 'imeimd5, 'macmd5, 'idfamd5, 'openudidmd5, 'androididmd5,
      'imeisha1, 'macsha1, 'idfasha1, 'openudidsha1, 'androididsha1, 'uuidunknow, 'userid,
      'reqdate, 'reqhour, 'iptype, 'initbidprice, 'adpayment, 'agentrate, 'lomarkrate,
      'adxrate, 'title, 'keywords, 'tagid, 'callbackdate, 'channelid, 'mediatype, 'email,
      'tel, 'age, 'sex)

    val result = ipConvertedResult.select(selectRows:_*)

    // 4. 数据落地
    spark.createKuduTable(ODS_TABLE_NAME, schema, keys)
    result.limit(200).saveToKudu(ODS_TABLE_NAME)

  }

  val ODS_TABLE_NAME: String = "ODS_" + KuduHelper.formattedDate()

  import scala.collection.JavaConverters._
  private val schema = new Schema(List(
    new ColumnSchemaBuilder("uuid", Type.STRING).nullable(false).key(true).build,
    new ColumnSchemaBuilder("sessionid", Type.STRING).nullable(true).key(false).build(),
    new ColumnSchemaBuilder("advertisersid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adorderid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adcreativeid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adplatformproviderid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("sdkversion", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adplatformkey", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("putinmodeltype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("requestmode", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adppprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("requestdate", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("ip", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("appid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("appname", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("device", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("client", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("osversion", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("density", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("pw", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("ph", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("longitude", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("latitude", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("region", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("city", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("ispid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("ispname", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("networkmannerid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("networkmannername", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("iseffective", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("isbilling", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adspacetype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adspacetypename", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("devicetype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("processnode", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("apptype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("district", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("paymode", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("isbid", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("bidprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("winprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("iswin", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("cur", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("rate", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("cnywinprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("imei", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("mac", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("idfa", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("openudid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("androidid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("rtbprovince", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("rtbcity", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("rtbdistrict", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("rtbstreet", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("storeurl", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("realip", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("isqualityapp", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("bidfloor", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("aw", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("ah", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("imeimd5", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("macmd5", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("idfamd5", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("openudidmd5", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("androididmd5", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("imeisha1", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("macsha1", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("idfasha1", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("openudidsha1", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("androididsha1", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("uuidunknow", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("userid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("reqdate", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("reqhour", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("iptype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("initbidprice", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adpayment", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("agentrate", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("lomarkrate", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("adxrate", Type.DOUBLE).nullable(true).key(false).build,
    new ColumnSchemaBuilder("title", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("keywords", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("tagid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("callbackdate", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("channelid", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("mediatype", Type.INT64).nullable(true).key(false).build,
    new ColumnSchemaBuilder("email", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("tel", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("age", Type.STRING).nullable(true).key(false).build,
    new ColumnSchemaBuilder("sex", Type.STRING).nullable(true).key(false).build
  ).asJava)

  private val keys = List("uuid")
}
