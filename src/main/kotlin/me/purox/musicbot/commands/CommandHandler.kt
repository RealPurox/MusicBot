package me.purox.musicbot.commands

import me.purox.musicbot.commands.music.PlayCommand
import me.purox.musicbot.commands.music.QueueCommand
import me.purox.musicbot.commands.music.StopCommand
import me.purox.musicbot.musicBot
import org.slf4j.LoggerFactory

open class CommandHandler {

    private val logger = LoggerFactory.getLogger(CommandHandler::class.java)

    val commands: MutableMap<String, ICommand> = mutableMapOf()
    private val unmodifiedCommands: MutableMap<String, ICommand> = mutableMapOf()

    init {
        //register commands here
        registerCommand(PlayCommand())
        registerCommand(StopCommand())
        registerCommand(QueueCommand())
    }

    private fun registerCommand(iCommand: ICommand) {
        commands[iCommand.invoke] = iCommand
        unmodifiedCommands[iCommand.invoke] = iCommand
        for (alias in iCommand.aliases) {
            commands[alias] = iCommand
        }
    }

    fun handleCommand(command: Command) {
        val iCommand = command.iCommand

        if (iCommand == null) {
            logger.warn("Command not found : ${command.invoke}")
            return
        }

        logger.info("Command ${musicBot.config.prefix + command.invoke} executed by ${command.event.author.name}#${command.event.author.discriminator}")
        iCommand.execute(command)
    }
}