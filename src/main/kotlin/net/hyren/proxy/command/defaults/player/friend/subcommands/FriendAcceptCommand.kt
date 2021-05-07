package net.hyren.proxy.command.defaults.player.friend.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.friends.data.FriendUser
import net.hyren.core.shared.users.friends.storage.dto.CreateFriendUserDTO
import net.hyren.proxy.command.defaults.player.friend.FriendCommand
import net.hyren.proxy.echo.packets.FriendAcceptedPacket
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.joda.time.DateTime

/**
 * @author Gutyerrez
 */
class FriendAcceptCommand : CustomCommand("aceitar") {

    override fun getDescription() = "Aceitar uma solicitação de amizade."

    override fun getArguments() = listOf(
        Argument("usuário")
    )

    override fun getParent() = FriendCommand()

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

        if (targetUser.getFriends().contains(user) && user!!.getFriends().contains(targetUser)) {
            commandSender.sendMessage(TextComponent("§cVocê já é amigo deste usuário."))
            return false
        }

        if (!targetUser.getFriends().contains(user)) {
            commandSender.sendMessage(TextComponent("§cVocê não possui uma solicitação de amizade deste usuário. Você pode enviar uma através do comando '/amigo add ${targetUser.name}'."))
            return false
        }

        CoreProvider.Repositories.Postgres.USERS_FRIENDS_REPOSITORY.provide().create(
            CreateFriendUserDTO(
                FriendUser(
                    user!!.id,
                    targetUser.id,
                    DateTime.now(
                        CoreConstants.DATE_TIME_ZONE
                    )
                )
            )
        )

        val message = ComponentBuilder()
            .append("\n")
            .append("§eO usuário ${targetUser.getFancyName()}§e agora é seu amigo!")
            .append("\n\n")
            .create()

        val _message = ComponentBuilder()
            .append("\n")
            .append("§eO usuário ${user.getFancyName()}§e agora é seu amigo!")
            .append("\n\n")
            .create()

        val packet = FriendAcceptedPacket()

        packet.targetUserId = targetUser.id
        packet.message = _message

        CoreProvider.Databases.Redis.ECHO.provide().publishToApplications(
            packet,
            CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByApplicationType(
                ApplicationType.PROXY
            )
        )

        commandSender.sendMessage(*message)
        return true
    }

}