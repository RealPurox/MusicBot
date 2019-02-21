package me.purox.musicbot

enum class Emote (private val emote : String) {

    ERROR("<:error:455049961831268354>"),
    SUCCESS("<:success:455049950930403348>"),
    INFO("<:info:458663927568400397>"),
    LOADING("<a:loading:548204474351747103>");

    override fun toString(): String {
        return emote
    }

}