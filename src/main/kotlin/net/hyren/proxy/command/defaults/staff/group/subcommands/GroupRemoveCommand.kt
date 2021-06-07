package net.hyren.proxy.command.defaults.staff.group.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.*
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.echo.packets.UserGroupsUpdatedPacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.groups.due.storage.dto.DeleteUserGroupDueDTO
import net.hyren.proxy.command.defaults.staff.group.GroupCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import java.util.*

/**
 * @author Gutyerrez
 */
class GroupRemoveCommand : CustomCommand("remover") {

    override fun getDescription() = "Remover um grupo de um usuário."

    override fun getArguments() = listOf(
        Argument("usuário"),
        Argument("grupo"),
        Argument("servidor")
    )

    override fun getParent() = GroupCommand()

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])
        val group = EnumSet.allOf(Group::class.java).find { it.name == args[1] }
        val server = CoreProvider.Cache.Local.SERVERS.provide().fetchByName(args[2])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (group == null) {
            commandSender.sendMessage(TextComponent("§cEste grupo não existe"))
            return false
        }

        if (!targetUser.hasStrictGroup(group, server)) {
            commandSender.sendMessage(TextComponent("§cEste usuário não possui esse grupo."))
            return false
        }

        if ((user!!.getHighestGroup().priority == group.priority) && !CoreConstants.WHITELISTED_USERS.contains(user.name)) {
            commandSender.sendMessage(TextComponent("§cVocê não pode gerenciar este grupo."))
            return false
        }

        if (CoreProvider.Repositories.PostgreSQL.USERS_GROUPS_DUE_REPOSITORY.provide().delete(
                DeleteUserGroupDueDTO(
                        targetUser.id,
                        group,
                        server
                )
        )) {
            val packet = UserGroupsUpdatedPacket(
                targetUser.id
            )

            CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)

            commandSender.sendMessage(TextComponent("§aVocê removeu o grupo ${group.displayName} do usuário ${targetUser.name}."))
            return true
        } else {
            commandSender.sendMessage(TextComponent("§cNão foi possível remover o grupo do usuário."))
            return false
        }
    }

}