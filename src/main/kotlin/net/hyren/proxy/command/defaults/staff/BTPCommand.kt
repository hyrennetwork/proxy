package net.hyren.proxy.command.defaults.staff

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.echo.packets.ConnectUserToApplicationPacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class BTPCommand : CustomCommand("btp"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Teletransportar-se até um jogador em outra applicação."

    override fun getArguments() = listOf(
        Argument("usuário")
    )

    override fun getGroup() = Group.MANAGER

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (!targetUser.isOnline()) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_ONLINE)
            return false
        }

        val bukkitApplication = targetUser.getConnectedBukkitApplication()

        if (bukkitApplication === null) {
            commandSender.sendMessage(TextComponent("§cNão foi possível localizar a aplicação que o usuário está."))
            return false
        }

        val packet = ConnectUserToApplicationPacket(
            user?.id,
            bukkitApplication
        )

        CoreProvider.Databases.Redis.ECHO.provide().publishToApplicationType(
            packet,
            ApplicationType.PROXY
        )

        commandSender.sendMessage(TextComponent("§aConectando..."))
        return false
    }

}