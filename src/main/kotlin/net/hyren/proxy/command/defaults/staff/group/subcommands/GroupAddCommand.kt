package net.hyren.proxy.command.defaults.staff.group.subcommands

import com.google.common.base.Enums
import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.echo.packets.UserGroupsUpdatedPacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.groups.due.storage.dto.CreateUserGroupDueDTO
import net.hyren.proxy.command.defaults.staff.group.GroupCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class GroupAddCommand : CustomCommand("adicionar") {

    override fun getDescription() = "Adicionar um grupo a um usuário."

    override fun getArguments() = listOf(
        Argument("usuário"),
        Argument("grupo"),
        Argument("servidor"),
        Argument("duração")
    )

    override fun getParent() = GroupCommand()

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])
        val group = Enums.getIfPresent(Group::class.java, args[1])
        val server = CoreProvider.Cache.Local.SERVERS.provide().fetchByName(args[2])
        val dueAt = DateTime.now() + TimeUnit.DAYS.toMillis(args[3].toLongOrNull() ?: 0)

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (group === null || !group.isPresent) {
            commandSender.sendMessage(TextComponent("§cGrupo não localizado"))
            return false
        }

        if ((user!!.getHighestGroup().priority!! <= group.get().priority!!) && !CoreConstants.WHITELISTED_USERS.contains(user.name)) {
            commandSender.sendMessage(TextComponent("§cVocê não pode gerenciar este grupo."))
            return false
        }

        CoreProvider.Repositories.PostgreSQL.USERS_GROUPS_DUE_REPOSITORY.provide().create(
                CreateUserGroupDueDTO(
                        targetUser.id,
                        group.get(),
                        server,
                        dueAt
                )
        )

        val packet = UserGroupsUpdatedPacket(
            targetUser.id
        )

        CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)

        commandSender.sendMessage(TextComponent("§aVocê adicionou o grupo ${group.get().displayName}§a para o usuário ${targetUser.name}."))
        return false
    }

}