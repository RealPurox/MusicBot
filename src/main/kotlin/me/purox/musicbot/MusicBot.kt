package me.purox.musicbot

import me.purox.musicbot.commands.CommandHandler
import me.purox.musicbot.listener.CommandListener
import me.purox.musicbot.listener.ReadyListener
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game

class MusicBot {

    lateinit var jda: JDA

    val musicManager : MusicManager = MusicManager()
    val commandHandler : CommandHandler = CommandHandler()
    val config : Config = Config().load()

    fun start() {
        val builder = JDABuilder(AccountType.BOT)

        builder.setToken(config.token)
        builder.setGame(Game.playing("music"))
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB)

        builder.addEventListener(CommandListener())
        builder.addEventListener(ReadyListener())

        jda = builder.build()
    }
}