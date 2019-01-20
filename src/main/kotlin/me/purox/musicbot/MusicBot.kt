package me.purox.musicbot

import me.purox.musicbot.commands.CommandHandler
import me.purox.musicbot.listener.CommandListener
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game

val COMMAND_HANDLER = CommandHandler()
val MUSIC_MANAGER = MusicManager()

lateinit var JDA : JDA

fun main(args: Array<String>) {
    val builder = JDABuilder(AccountType.BOT)

    builder.setToken(TOKEN)
    builder.setGame(Game.playing("music"))
    builder.setStatus(OnlineStatus.DO_NOT_DISTURB)

    builder.addEventListener(CommandListener())

    JDA = builder.build()
}