package net.hyren.proxy.command.defaults.player.ignore.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.ignored.data.IgnoredUser
import net.hyren.core.shared.users.ignored.storage.dto.CreateIgnoredUserDTO
import net.hyren.proxy.command.defaults.player.ignore.IgnoreCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import org.joda.time.DateTime

/**
 * @author Gutyerrez
 */
class IgnoreAddCommand : CustomCommand("add") {

    override fun getDescription() = "Ignora um usuário em específico."

    override fun getArguments() = listOf(
        Argument("usuário")
    )

    override fun getParent() = IgnoreCommand()

    override fun onCommand(commandSender: CommandSender, user: User?, args: Array<out String>): Boolean? {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (!targetUser.isOnline()) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_ONLINE)
            return false
        }

        CoreProvider.Repositories.MariaDB.IGNORED_USERS_REPOSITORY.provide().create(
            CreateIgnoredUserDTO(
                IgnoredUser(
                    user!!.id,
                    targetUser.id,
                    DateTime.now(
                        CoreConstants.DATE_TIME_ZONE
                    )
                )
            )
        )

        commandSender.sendMessage(TextComponent("§aUsuário ignorado com sucesso."))
        return true
    }

}