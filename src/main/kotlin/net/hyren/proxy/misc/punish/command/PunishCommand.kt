package net.hyren.proxy.misc.punish.command

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.echo.packets.UserPunishedPacket
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.ChatColor
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.misc.utils.Patterns
import net.hyren.core.shared.misc.utils.TimeCode
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.punishments.storage.dto.CreateUserPunishmentDTO
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * @author Gutyerrez
 */
class PunishCommand : CustomCommand("punir"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getArguments(): List<Argument> = mutableListOf(
        Argument("usuário")
    )

    override fun getDescription() = "Punir um usuário."

    override fun getGroup() = Group.HELPER

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        if (user === null) return false

        val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])

        if (targetUser === null) {
            commandSender.sendMessage(DefaultMessage.USER_NOT_FOUND)
            return false
        }

        if (targetUser == user) {
            commandSender.sendMessage(TextComponent("§cVocê não pode punir a si mesmo."))
            return false
        }

        when (args.size) {
            1 -> {
                val punishCategories = CoreProvider.Cache.Local.PUNISH_CATEGORIES.provide().fetchAll()
                val componentBuilder = ComponentBuilder("\n")
                    .append("§eLista de infração disponíveís (${punishCategories.size})")
                    .append("\n\n")

                var color = ChatColor.WHITE

                punishCategories.forEachIndexed { index, it ->
                    val _componentBuilder = ComponentBuilder("§e${it.displayName}")
                        .append("\n\n")
                        .append("§f${it.getDescription()}")
                        .append("\n\n")
                        .append("§fGrupo mínimo: ${it.group.getFancyDisplayName()}")
                        .append("\n\n")

                    it.punishDurations.forEachIndexed { index, punishDuration ->
                        _componentBuilder.append("§e${index + 1}º:")
                            .append(" ")
                            .append("§f[${punishDuration.punishType.name}]")
                            .append(" ")
                            .append(TimeCode.toText(punishDuration.duration, 1))

                        if (index + 1 < it.punishDurations.size) _componentBuilder.append("\n")
                    }

                    componentBuilder.append("$color - ${it.displayName}")
                        .event(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text(_componentBuilder.create())
                            )
                        )
                        .event(
                            ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/punir ${args[0]} ${it.name} "
                            )
                        )

                    if (index + 1 < punishCategories.size) componentBuilder.append("\n")

                    if (color === ChatColor.WHITE) color = ChatColor.GRAY else color = ChatColor.WHITE
                }

                componentBuilder.append("\n")

                commandSender.sendMessage(*componentBuilder.create())
                return true
            }
            2, 3, 4, 5 -> {
                val punishCategory = CoreProvider.Cache.Local.PUNISH_CATEGORIES.provide().fetchByName(args[1])

                if (punishCategory === null) {
                    commandSender.sendMessage(TextComponent("§cEsta categoria não existe"))
                    return false
                }

                if (!user.hasGroup(punishCategory.group)) {
                    commandSender.sendMessage(TextComponent("§cÉ necessário o grupo ${punishCategory.group.getFancyDisplayName()} §cpara punir por esta categoria."))
                    return false
                }

                val proof = if (args.size >= 3) args[2] else null
                val customReason = if (args.size >= 4) args[3] else null
                val hidden = if (args.size >= 5) args[4].toLowerCase() === "-h" else false

                if ((proof === null || !Patterns.URL.matches(proof)) && !user.hasGroup(Group.MANAGER)) {
                    commandSender.sendMessage(TextComponent("§cA prova inserida está inválida."))
                    return true
                }

                val userPunishments = targetUser.getPunishments()
                    .stream()
                    .filter {
                        it.punishCategory === punishCategory
                    }.collect(Collectors.toList())

                val latestPunishment = userPunishments.stream()
                    .filter { it.isStrictActive() || it.isPending() }
                    .findFirst()
                    .orElse(null)

                if (latestPunishment !== null && latestPunishment.createdAt + TimeUnit.MINUTES.toMillis(3) >= DateTime.now(
                    CoreConstants.DATE_TIME_ZONE
                )) {
                    commandSender.sendMessage(TextComponent("§cEste usuário possui uma punição recente por essa categoria."))
                    return false
                }

                val punishDuration = punishCategory.punishDurations[
                    if (punishCategory.punishDurations.size <= userPunishments.size + 1) punishCategory.punishDurations.size - 1 else userPunishments.size
                ]

                val userPunishment = CoreProvider.Repositories.PostgreSQL.USERS_PUNISHMENTS_REPOSITORY.provide().create(
                    CreateUserPunishmentDTO(
                        targetUser.id,
                        user.id,
                        punishDuration.punishType,
                        punishCategory.name,
                        punishDuration.duration,
                        customReason,
                        proof,
                        hidden
                    )
                )

                val message = ComponentBuilder("\n")
                    .append("§c (!) ${targetUser.name} foi ${punishDuration.punishType.displayName} por ${user.name}.")
                    .append("\n")
                    .append("§c (!) Motivo: ${punishCategory.displayName}${if (proof !== null && proof.isNotEmpty()) " - $proof" else ""}")
                    .append("\n")
                    .append("§c (!) Duração: ${TimeCode.toText(punishDuration.duration, 1)}")
                    .append("\n")
                    .create()

                val packet = UserPunishedPacket()

                packet.id = userPunishment?.id
                packet.userId = targetUser.getUniqueId()
                packet.message = message

                CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)

                commandSender.sendMessage(TextComponent("§eUsuário punido com sucesso!"))
                return true
            }
            else -> {
                commandSender.sendMessage(*this.getUsage())
                return true
            }
        }
    }

}