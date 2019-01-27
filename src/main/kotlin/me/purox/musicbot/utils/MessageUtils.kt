package me.purox.musicbot.utils

import java.util.function.Consumer
import me.purox.musicbot.Emote
import me.purox.musicbot.MusicBot
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.utils.PermissionUtil
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(MusicBot::class.java)

fun sendMessage(channel: MessageChannel, message: Any) {
    sendMessage(channel, message, null, null)
}

fun sendMessage(channel: MessageChannel, message: Any, success: Consumer<in Message>) {
    sendMessage(channel, message, success, null)
}

fun sendMessage(channel: MessageChannel, message: Any, success: Consumer<in Message>?, failure: Consumer<in Throwable>?) {
    if (channel.type == ChannelType.PRIVATE) {
        sendPrivateMessage((channel as PrivateChannel).user, message, success, failure)
        return
    }

    if (channel !is TextChannel) return
    if (!channel.canTalk()) return

    try {

        when (message) {
            is MessageEmbed -> {
                if (!PermissionUtil.checkPermission(channel, channel.guild.selfMember, Permission.MESSAGE_EMBED_LINKS)) {
                    channel.sendMessage("${Emote.ERROR} | I don't have permission to send message embeds in this server").queue(success, failure)
                } else channel.sendMessage(message).queue(success, failure)
            }

            is String -> {
                var msg = message
                if (msg.length >= 2000)
                    msg = message.substring(0, msg.length - 2000)
                channel.sendMessage(msg).queue(success, failure)
            }

            is Message -> channel.sendMessage(message).queue(success, failure)

            else -> {
                var msg = message
                if (msg.toString().length >= 2000)
                    msg = message.toString().substring(0, msg.toString().length - 2000)
                channel.sendMessage(msg.toString()).queue(success, failure)
            }
        }

    } catch (e: Exception) {
        logger.error("", e)
    }

}

fun sendPrivateMessage(user: User, message: Any) {
    sendPrivateMessage(user, message, null, null)
}

fun sendPrivateMessage(user: User, message: Any, success: Consumer<in Message>) {
    sendPrivateMessage(user, message, success, null)
}

fun sendPrivateMessage(user: User, message: Any, success: Consumer<in Message>?, failure: Consumer<in Throwable>?) {
    if (user.isBot) return

    try {

        when (message) {
            is MessageEmbed -> user.openPrivateChannel().queue({ privateChannel ->
                run {
                    privateChannel.sendMessage(message).queue({ done ->
                        run {
                            success?.accept(done)
                            privateChannel.close().queue()
                        }
                    }, { fail -> failure?.accept(fail) })
                }
            }, { fail -> failure?.accept(fail) })

            is String -> user.openPrivateChannel().queue({ privateChannel ->
                run {
                    var msg = message
                    if (msg.length >= 2000)
                        msg = msg.substring(0, message.length - 2000)
                    privateChannel.sendMessage(msg).queue({ done ->
                        run {
                            success?.accept(done)
                            privateChannel.close().queue()
                        }
                    }, { fail -> failure?.accept(fail) })
                }
            }, { fail -> failure?.accept(fail) })

            is Message -> user.openPrivateChannel().queue({ privateChannel ->
                run {
                    privateChannel.sendMessage(message).queue({ done ->
                        run {
                            success?.accept(done)
                            privateChannel.close().queue()
                        }
                    }, { fail -> failure?.accept(fail) })
                }
            }, { fail -> failure?.accept(fail) })

            else -> user.openPrivateChannel().queue({ privateChannel ->
                run {
                    var msg = message
                    if (msg.toString().length >= 2000)
                        msg = msg.toString().substring(0, message.toString().length - 2000)
                    privateChannel.sendMessage(msg.toString()).queue({ done ->
                        run {
                            success?.accept(done)
                            privateChannel.close().queue()
                        }
                    }, { fail -> failure?.accept(fail) })
                }
            }, { fail -> failure?.accept(fail) })
        }

    } catch (e: Exception) {
        logger.error("", e)
    }
}