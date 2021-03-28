package com.redefantasy.proxy.command.defaults.player

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.misc.preferences.PreferenceState
import com.redefantasy.core.shared.misc.preferences.TELL_PREFERENCE
import com.redefantasy.core.shared.misc.utils.DefaultMessage
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.proxy.echo.packets.TellPacket
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class TellCommand : CustomCommand("tell") {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Enviar uma mensagem privada para um usuário"

    override fun getArguments() = listOf(
            Argument("usuário"),
            Argument("mensagem")
    )

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        if (user === null) return false

        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (!targetUser.isOnline()) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_ONLINE)
            return false
        }

        if (targetUser == user) {
            commandSender.sendMessage(TextComponent("§cEnviar mensagem para si mesmo é algo um pouco estranho não acha? Por quê não faz novos amigos?"))
            return false
        }

        if (!targetUser.isLogged()) {
            commandSender.sendMessage(TextComponent("§cVocê não pode interagir com usuários que ainda não logaram."))
            return false
        }

        val message = args.copyOfRange(1, args.size).joinToString(" ")

        if (user.lastSentMessage !== null && user.lastSentMessage!!.toLowerCase().contains(message.toLowerCase())) {
            commandSender.sendMessage(
                TextComponent("§cVocê não pode enviar uma mensagem tão similar a anterior.")
            )
            return false
        }

        println(targetUser.getPreferences())

        if (targetUser.getPreferences().find { it == TELL_PREFERENCE }?.preferenceState === PreferenceState.DISABLED) {
            commandSender.sendMessage(
                TextComponent("§cEste usuário está com o recebimento de mensagens privadas desativado.")
            )
            return false
        }

        user.directMessage = targetUser
        user.lastSentMessage = message

        val packet = TellPacket()

        packet.senderId = user.getUniqueId()
        packet.receiverId = targetUser.getUniqueId()
        packet.message = message

        val proxyApplications = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByApplicationType(ApplicationType.PROXY)

        CoreProvider.Databases.Redis.ECHO.provide().publishToApplications(
            packet,
            proxyApplications
        )
        return true
    }

}