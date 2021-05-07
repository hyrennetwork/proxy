package net.hyren.proxy.command.defaults.staff

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.echo.packets.StaffMessagePacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender

/**
 * @author Gutyerrez
 */
class StaffChatCommand : CustomCommand("s"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Enviar uma mensagem no bate-papo da equipe."

    override fun getArguments() = listOf(
        Argument("mensagem")
    )

    override fun getGroup() = Group.HELPER

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        user ?: return false

        val message = args.joinToString(" ")

        val packet = StaffMessagePacket()

        packet.stafferId = user.getUniqueId()
        packet.bukkitApplication = user.getConnectedBukkitApplication()
        packet.message = message

        CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)
        return false
    }

}