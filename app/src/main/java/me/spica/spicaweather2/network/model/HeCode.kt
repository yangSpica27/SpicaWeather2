package me.spica.spicaweather2.network.model

enum class HeCode(
    val code: String,
    val message: String,
) {
    Ok("200", "成功"),
    NO_DATA("204", "成功, 但你查询的地区暂时没有你需要的数据"),
    BAD_REQUEST("400", "请求错误，可能包含错误的请求参数或缺少必选的请求参数。"),
    INVALID_KEY("401", "认证失败，可能使用了错误的KEY、数字签名错误、KEY的类型错误（如使用SDK的KEY去访问Web API）。"),
    NO_MORE_REQUESTS("402", "用户的访问量已耗尽"),
    PERMISSION_DENIED("403", "无访问权限，可能是绑定的PackageName、BundleID、域名IP地址不一致，或者是需要额外付费的数据。"),
    INVALID_PARAM("404", "查询的数据或地区不存在。"),
    TOO_FAST("429", "超过限定的QPM（每分钟访问次数）"),
    INTERNET("500", "无响应或超时"),
}
