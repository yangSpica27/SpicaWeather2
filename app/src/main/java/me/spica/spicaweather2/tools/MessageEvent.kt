package me.spica.spicaweather2.tools

data class MessageEvent(val tag: String, val extra: Any? = null) {
    companion object {
        fun create(tag: MessageType, extra: Any? = null): MessageEvent = MessageEvent(tag.tag, extra)
    }
}
enum class MessageType(val tag: String) {
    Get2MainActivityAnim("1")
}
