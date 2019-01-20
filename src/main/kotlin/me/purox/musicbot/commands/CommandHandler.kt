package me.purox.musicbot.commands

import me.purox.musicbot.commands.music.PlayCommand
import me.purox.musicbot.commands.music.TestCommand

open class CommandHandler {

    val commands: MutableMap<String, ICommand> = mutableMapOf()
    private val unmodifiedCommands: MutableMap<String, ICommand> = mutableMapOf()

    init {
        //register commands here
        registerCommand(TestCommand())
        registerCommand(PlayCommand())
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
            println("Command not found : ${command.invoke}")
            return
        }

        println("Command ${command.invoke} executed by ${command.event.author.name}#${command.event.author.discriminator}")
        iCommand.execute(command)
    }
}