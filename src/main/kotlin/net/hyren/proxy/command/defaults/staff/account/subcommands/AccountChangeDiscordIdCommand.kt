package net.hyren.proxy.command.defaults.staff.account.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.misc.utils.NumberUtils
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.storage.dto.UpdateUserByIdDTO
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class AccountChangeDiscordIdCommand : CustomCommand("discord") {

    override fun getDescription() = "Alterar o id do discord de um usuário."

    override fun getArguments() = listOf(
        Argument("usuário"),
        Argument("id do discord")
    )

    override fun getParent() = AccountChangeCommand()

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser == null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (!NumberUtils.isValidLong(args[1])) {
            commandSender.sendMessage(TextComponent("§cVocê inseriu um id inválido."))
            return false
        }

        val discordId = args[1].toLong()

        val discordUser = CoreProvider.Cache.Local.USERS.provide().fetchByDiscordId(
            discordId
        )

        if (discordUser != null) {
            commandSender.sendMessage(TextComponent("§cEste id de discord já está sendo utilizado."))
            return false
        }

        targetUser.discordId = discordId

        CoreProvider.Repositories.PostgreSQL.USERS_REPOSITORY.provide().update(
            UpdateUserByIdDTO(
                targetUser.id
            ) {
                this.discordId = discordId
            }
        )

        commandSender.sendMessage(TextComponent("§eId do discord do usuário ${targetUser.name} atualizado!"))
        return true
    }

}