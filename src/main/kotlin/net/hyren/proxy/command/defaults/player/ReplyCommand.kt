package net.hyren.proxy.command.defaults.player

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.users.data.User
import net.hyren.proxy.echo.packets.TellPacket
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class ReplyCommand : CustomCommand("r") {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Responder uma mensagem privada de um jogador."

    override fun getArguments() = listOf(
            Argument("mensagem")
    )

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean? {
        if (user!!.directMessage === null || !user!!.directMessage!!.isOnline()) {
            commandSender.sendMessage(TextComponent("§cVocê não possui ninguém para responder."))
            return false
        }

        user.directMessage = user.directMessage

        val message = args.joinToString(" ")

        val packet = TellPacket()

        packet.senderId = user.getUniqueId()
        packet.receiverId = user.directMessage!!.getUniqueId()
        packet.message = message

        val senderUserProxyApplication = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchProxyApplication(
            user
        )
        val targetUserProxyApplication = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchProxyApplication(
            user.directMessage!!
        )

        CoreProvider.Databases.Redis.ECHO.provide().publishToApplications(
            packet,
            arrayOf(
                senderUserProxyApplication,
                if (senderUserProxyApplication !== targetUserProxyApplication) {
                    targetUserProxyApplication
                } else null
            )
        )
        return true
    }

}