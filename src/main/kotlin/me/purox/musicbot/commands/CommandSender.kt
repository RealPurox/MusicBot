package me.purox.musicbot.commands

import me.purox.musicbot.Emote
import me.purox.musicbot.utils.sendMessage
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.requests.RestAction
import java.util.function.Consumer

class CommandSender (private val user : User, private val  event: MessageReceivedEvent) : User {

    fun error(message: String) {
        sendMessage(event.channel, "(${Emote.ERROR}): $message")
    }

    fun warning(message: String) {
        sendMessage(event.channel, "(:warning:): $message")
    }

    fun info(message: String) {
        sendMessage(event.channel, "(${Emote.INFO}): $message")
    }

    fun success(message: String) {
        sendMessage(event.channel, "(${Emote.SUCCESS}): $message")
    }

    fun reply(message: Any, consumer: Consumer<Message>) {
        sendMessage(event.channel, message, consumer)
    }

    fun reply(message: Any) {
        sendMessage(event.channel, message)
    }

    override fun getDefaultAvatarId(): String {
        return this.user.defaultAvatarId
    }

    override fun getMutualGuilds(): MutableList<Guild> {
        return this.user.mutualGuilds
    }

    override fun isBot(): Boolean {
        return this.user.isBot
    }

    override fun getDefaultAvatarUrl(): String {
        return this.user.avatarUrl
    }

    override fun getName(): String {
        return this.user.name
    }

    override fun hasPrivateChannel(): Boolean {
        return this.user.hasPrivateChannel()
    }

    override fun getJDA(): JDA {
        return this.user.jda
    }

    override fun getIdLong(): Long {
        return this.user.idLong
    }

    override fun openPrivateChannel(): RestAction<PrivateChannel> {
        return this.user.openPrivateChannel()
    }

    override fun isFake(): Boolean {
        return this.user.isFake
    }

    override fun getAsMention(): String {
        return this.user.asMention
    }

    override fun getAvatarId(): String {
        return this.user.avatarId
    }

    override fun getDiscriminator(): String {
        return this.user.discriminator
    }

    override fun getAvatarUrl(): String {
        return this.user.avatarUrl
    }

    override fun getEffectiveAvatarUrl(): String {
        return this.user.effectiveAvatarUrl
    }
}