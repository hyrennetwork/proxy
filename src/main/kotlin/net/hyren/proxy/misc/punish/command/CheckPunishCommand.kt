package net.hyren.proxy.misc.punish.command

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.utils.DateFormatter
import net.hyren.core.shared.misc.utils.DefaultMessage
import net.hyren.core.shared.misc.utils.TimeCode
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * @author Gutyerrez
 */
class CheckPunishCommand : CustomCommand("checkpunir"), GroupCommandRestrictable {

    private val SQUARE_SYMBOL = '\u2588'

    override fun getDescription() = "Ver o histórico de punições de um usuário."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getArguments() = listOf(
            Argument("usuário")
    )

    override fun getGroup() = Group.HELPER

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

        val userPunishments = CoreProvider.Cache.Local.USERS_PUNISHMENTS.provide().fetchByUserId(targetUser.id) ?: emptyList()

        val message = ComponentBuilder()
                .append("\n")
                .append("§e$SQUARE_SYMBOL Pendente §a$SQUARE_SYMBOL Ativo §c$SQUARE_SYMBOL Finalizado §7$SQUARE_SYMBOL Revogado")
                .append("\n\n")

        userPunishments.sortedBy {
            it.id
        }.forEachIndexed { index, userPunishment ->
            val staffer = CoreProvider.Cache.Local.USERS.provide().fetchById(userPunishment.stafferId)
            val revoker = if (userPunishment.revokeStafferId === null) {
                null
            } else CoreProvider.Cache.Local.USERS.provide().fetchById(userPunishment.revokeStafferId!!)
            val revokeReason = if (userPunishment.revokeCategory !== null) {
                userPunishment.revokeCategory!!
            } else null

            val hoverMessage = ComponentBuilder()
                    .append("§fId: §b#${userPunishment.id.value}")
                    .append("\n")
                    .append("§fAplicada por: §7${staffer!!.name}")
                    .append("\n")
                    .append("§fData de início: §7${
                        if (userPunishment.startTime === null && userPunishment.revokeTime === null) {
                            "[Aguardando início]"
                        } else {
                            DateFormatter.formatToDefault(userPunishment.startTime ?: userPunishment.revokeTime)
                        }
                    }")
                    .append("\n")
                    .append("§fDuração: §7${TimeCode.toText(userPunishment.duration, 1)}")
                    .append("\n")
                    .append("§fTipo: §7[${userPunishment.punishType}]")

            if (userPunishment.revokeTime !== null && revoker !== null && revokeReason !== null) {
                hoverMessage.append("\n\n")
                        .append("§fRevogada por: §7${revoker.name}")
                        .append("\n")
                        .append("§fRevogada em: §7${TimeCode.toText(userPunishment.revokeTime!!.millis, 1)}")
                        .append("\n")
                        .append("§fMotivo: §7${revokeReason.displayName}")
            }

            val color = userPunishment.getColor()

            message.append("$color - ")
                    .append("$color[${DateFormatter.formatToDefault(userPunishment.createdAt)}] [${userPunishment.punishCategory?.displayName ?: userPunishment.customReason}]")
                    .event(
                            HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Text(hoverMessage.create())
                            )
                    )
                    .append(" ")
                    .append("$color[Prova]")
                    .event(
                            ClickEvent(
                                    ClickEvent.Action.OPEN_URL,
                                    userPunishment.proof
                            )
                    )
                    .append(" ")
                    .append("$color[Revogar]")
                    .event(
                            ClickEvent(
                                    ClickEvent.Action.SUGGEST_COMMAND,
                                    "/revogar ${userPunishment.id.value}"
                            )
                    )

            if (index + 1 < userPunishments.size) message.append("\n")
        }

        if (userPunishments.isEmpty()) {
            message.append("§f   --/--")
        }

        message.append("\n")

        commandSender.sendMessage(*message.create())
        return false
    }

}