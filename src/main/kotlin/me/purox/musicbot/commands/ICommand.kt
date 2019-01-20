package me.purox.musicbot.commands

open class ICommand(val invoke: String, vararg val aliases: String) {

    open fun execute(command: Command) { }

}