package net.hyren.proxy.command.defaults.staff

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.*
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.*
import java.util.stream.Collectors

/**
 * @author Gutyerrez
 */
class FindCommand : CustomCommand("find"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getDescription() = "Ver informações sobre um usuário."

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

        val color = if (targetUser.isOnline()) {
            ChatColor.GREEN
        } else {
            ChatColor.RED
        }

        val address = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchConnectedAddress(targetUser)
        val punishments = CoreProvider.Cache.Local.USERS_PUNISHMENTS.provide().fetchByUserId(targetUser.id)

        val message = ComponentBuilder()
            .append("\n")
            .append("§2Informações sobre o usuário ${targetUser.name}:")
            .append("\n\n")
            .append(" ${color}Informações básicas")
            .append("\n\n")
            .append("   §fId: §7${targetUser.getUniqueId()}")
            .event(
                ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    targetUser.getUniqueId().toString()
                )
            )
            .append("\n")
            .append("   §fData de registro: §7${DateFormatter.formatToDefault(targetUser.createdAt)}")
            .append("\n")
            .append(
                "   §fÚltima autenticação: §7${
                    if (targetUser.lastLoginAt === null) {
                        "[Desconhecida]"
                    } else DateFormatter.formatToDefault(targetUser.lastLoginAt)
                }"
            )
            .append("\n")
            .append("   §fPunido: ${
                if (punishments !== null && punishments.isNotEmpty() && punishments.stream()
                        .anyMatch {
                            it.startTime === null && it.revokeCategory === null || it.isActive()
                        }
                ) {
                    "§cSim"
                } else {
                    "§aNão"
                }
            }")
            .append("\n\n")
            .append(" ${color}Informações avançadas:")
            .append("\n\n")
            .append("   §fEndereço conectado: §7${if (address === null || address.isEmpty()) "§7[Desconhecido]" else address}")
            .append("\n")
            .append("   §fÚltimo IP: §7${if (user!!.hasGroup(Group.MANAGER)) targetUser.lastAddress else "§c[Sem permissão]"}")
            .append("\n\n")
            .append(" ${color}Associações:")
            .append("\n\n")
            .append("   §fE-mail: §7[Não implementado]")
            .append("\n")
            .append("   §fDiscord: §7[Não implementado]")
            .append("\n")
            .append("   §fTwitter: §7[Não implementado]")
            .append("\n\n")
            .append(" ${color}Servidores:")
            .append("\n\n")

        val groups = CoreProvider.Cache.Local.USERS_GROUPS_DUE.provide().fetchByUserId(targetUser.getUniqueId())
            ?: emptyMap()

        var i = 0

        groups.forEach {
            val server = it.key
            val _groups = it.value

            val groupsToString =
                _groups.stream().sorted { group1, group2 -> group2.priority!!.compareTo(group1.priority!!) }
                    .map { group -> group.getFancyDisplayName() }
                    .collect(Collectors.joining("§f, "))

            message.append("   §f${if (server === null) "Todos" else server.displayName}: $groupsToString")

            if (i + 1 < groups.size) message.append("\n")

            i++
        }

        if (groups.isEmpty()) {
            message.append("   §f--/--")
        }

        val proxyApplication = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchProxyApplication(targetUser)
        val bukkitApplication = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchBukkitApplication(targetUser)

        message.append("\n\n")
            .append(" ${color}Conexão:")
            .append("\n\n")
            .append("   §fConectado: ${if (targetUser.isOnline()) "§aSim" else "§cNão"}")
            .append("\n")
            .append("   §fProxy: §7${if (proxyApplication === null) "[Desconhecido]" else proxyApplication.displayName}")
            .append("\n")
            .append("   §fServidor: §7${if (bukkitApplication === null || bukkitApplication.server === null) "Nenhum" else bukkitApplication.server!!.displayName}")
            .append("\n")

        commandSender.sendMessage(*message.create())
        return true
    }

}