package com.redefantasy.proxy.command.defaults.staff

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.misc.utils.DefaultMessage
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class KickCommand : CustomCommand("kick"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.CONSOLE_AND_GAME

    override fun getDescription() = "Chutar um jogador para fora do servidor."

    override fun getArguments() = listOf(
        Argument("usuário")
    )

    override fun getAliases() = arrayOf("chutar")

    override fun getGroup() = Group.MODERATOR

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

        if (user == targetUser) {
            commandSender.sendMessage(TextComponent("§cVocê não pode expulsar a si mesmo."))
            return false
        }

        val message = args.copyOfRange(1, args.size).joinToString(" ")

        targetUser.disconnect(
            ComponentBuilder()
                .append("§c§lREDE FANTASY")
                .append("\n\n")
                .append("§cVocê foi chutado por: ${user?.name}")
                .append("\n")
                .append("Motivo: $${if (message.isEmpty()) "Nenhum motivo informado" else message}")
                .create()
        )

        commandSender.sendMessage(TextComponent("§eVocê chutou ${targetUser.name} para fora do servidor por: ${if (message.isEmpty()) "Nenhum motivo informado" else message}."))
        return false
    }

}