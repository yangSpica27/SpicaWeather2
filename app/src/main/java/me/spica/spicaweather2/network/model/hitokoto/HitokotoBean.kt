package me.spica.spicaweather2.network.model.hitokoto


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class HitokotoBean(
    @Json(name = "commit_from")
    val commitFrom: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    val creator: String? = null,
    @Json(name = "creator_uid")
    val creatorUid: Int? = null,
    val from: String? = null,
    @Json(name = "from_who")
    val fromWho: String? = null,
    val hitokoto: String? = null,
    val id: Int? = null,
    val length: Int? = null,
    val reviewer: Int? = null,
    val type: String? = null,
    val uuid: String? = null
)