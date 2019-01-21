package me.purox.musicbot

import com.google.gson.GsonBuilder

lateinit var musicBot: MusicBot

val GSON = GsonBuilder().create()!!

fun main(args: Array<String>) {
    musicBot = MusicBot()
    musicBot.start()
}