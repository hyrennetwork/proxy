package com.redefantasy.proxy.command.defaults.staff

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import com.redefantasy.core.shared.echo.packets.ConnectUserToApplicationPacket
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.misc.utils.DefaultMessage
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class SendCommand : CustomCommand("send"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.CONSOLE_AND_GAME

    override fun getDescription() = "Enviar um usuário para um servidor específico."

    override fun getArguments() = listOf(
        Argument("usuário"),
        Argument("aplicação")
    )

    override fun getGroup() = Group.MANAGER

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        val user = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])
        val bukkitApplication = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByName(args[1])

        if (user === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (!user.isOnline()) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_ONLINE)
            return false
        }

        if (!user.isLogged()) {
            commandSender.sendMessage(TextComponent("§cVocê não pode interagir com usuário não autenticados."))
            return false
        }

        if (bukkitApplication === null) {
            commandSender.sendMessage(TextComponent("§cEsta aplicação não existe."))
            return false
        }

        val packet = ConnectUserToApplicationPacket(
            user.id,
            bukkitApplication
        )

        CoreProvider.Databases.Redis.ECHO.provide().publishToApplications(
            packet,
            CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByApplicationType(
                ApplicationType.PROXY
            )
        )

        commandSender.sendMessage(TextComponent("§eVocê enviou o usuário ${user.name} para a aplicação ${bukkitApplication?.server?.displayName ?: bukkitApplication?.name ?: "desconhecida"}."))
        return true
    }

}