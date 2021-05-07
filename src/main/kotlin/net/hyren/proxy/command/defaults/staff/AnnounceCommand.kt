package net.hyren.proxy.command.defaults.staff

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.echo.packets.BroadcastMessagePacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder

/**
 * @author Gutyerrez
 */
class AnnounceCommand : CustomCommand("alerta"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Anunciar uma mensagem."

    override fun getArguments() = listOf(
        Argument("mensagem")
    )

    override fun getGroup() = Group.MASTER

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        val message = args.joinToString(" ")

        val packet = BroadcastMessagePacket()

        val highestGroup = user!!.getHighestGroup()

        packet.message = ComponentBuilder()
            .append("\n")
            .append("${highestGroup.getColoredPrefix()}${user.name}§e: $message")
            .append("\n")
            .create()

        CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)
        return false
    }

}