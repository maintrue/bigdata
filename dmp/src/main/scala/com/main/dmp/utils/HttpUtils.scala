package com.main.dmp.utils

import com.typesafe.config.ConfigFactory
import okhttp3.{OkHttpClient, Request}

object HttpUtils {

  private val commonConfig = ConfigFactory.load("common")

  private val client = new OkHttpClient()

  def getAddressInfo(longitude: Double, latitude: Double): Option[String] = {
    val url = s"${commonConfig.getString("amap.baseUrl")}" +
      s"/v3/geocode/regeo" +
      s"?key=${commonConfig.getString("amap.key")}" +
      s"&location=$longitude,$latitude"

    val request = new Request.Builder()
      .url(url)
      .get()
      .build()

    try {
      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        Some(response.body().string())
      } else {
        None
      }
    } catch {
      case _: Exception => None
    }
  }

  def parseAddressInfo(json: String): AMapResponse = {
    import org.json4s._
    import org.json4s.jackson.Serialization
    import org.json4s.jackson.Serialization.read

    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    read[AMapResponse](json)
  }
//  def main(args: Array[String]): Unit = {
//
//    val maybeString = HttpUtils.getAddressInfo(116, 39)
//
//    val response = HttpUtils.parseAddressInfo(maybeString.get)
//
//    println(response.regeocode.addressComponent.businessAreas.get.head.name)
//  }

}

case class AMapResponse(status: String, info: String, infocode: String, regeocode: ReGeoCode)

case class ReGeoCode(addressComponent: AddressComponent)

case class AddressComponent(businessAreas: Option[List[BusinessArea]])

case class BusinessArea(location: String, name: String)