package net.hyren.proxy.command.defaults.player.friend.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.friends.storage.dto.DeleteFriendUserDTO
import net.hyren.proxy.command.defaults.player.friend.FriendCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class FriendCancelCommand : CustomCommand("cancelar") {

    override fun getDescription() = "Canela um pedido de amizade."

    override fun getArguments() = listOf(
        Argument("usuário")
    )

    override fun getParent() = FriendCommand()

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean? {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (targetUser == user) {
            commandSender.sendMessage(TextComponent("§cVocê não pode ser amigo de si mesmo."))
            return false
        }

        if (user!!.getFriends().contains(targetUser) && targetUser.getFriends().contains(user)) {
            commandSender.sendMessage(TextComponent("§cVocê não pode cancelar amizades aceitas."))
            return false
        }

        if (!user.getFriends().contains(targetUser)) {
            commandSender.sendMessage(TextComponent("§cVocê não enviou nenhum pedido de amizade para este usuário."))
            return false
        }

        CoreProvider.Repositories.MariaDB.USERS_FRIENDS_REPOSITORY.provide().delete(
            DeleteFriendUserDTO(
                user.id,
                targetUser.id
            )
        )

        // send message
        return true
    }

}