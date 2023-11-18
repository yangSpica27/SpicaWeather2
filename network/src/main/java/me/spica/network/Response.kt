package me.spica.network

import me.spica.persistence.entity.Weather


class Response constructor(
  val code:Int,
  var message:String,
  val value:List<Weather>
)