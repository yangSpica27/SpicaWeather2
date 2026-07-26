package me.spica.spicaweather2.network.model

import com.squareup.moshi.JsonClass

/**
 * 所有字段都带默认值：批量接口一次返回全部城市，
 * 任何一个字段缺失导致解析抛异常都会让整批数据一起丢掉。
 */
@JsonClass(generateAdapter = true)
class BaseResponse<T> constructor(
  val code: Int = 0,
  var message: String = "",
  val data: T? = null,
)
