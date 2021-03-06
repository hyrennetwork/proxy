package net.hyren.proxy.command.defaults.player.friend.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.misc.utils.NumberUtils
import net.hyren.core.shared.misc.utils.TimeCode
import net.hyren.core.shared.users.data.User
import net.hyren.proxy.command.defaults.player.friend.FriendCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class FriendClearCommand : CustomCommand("limpar") {

    override fun getDescription() = "Exclui todos os amigos e pedidos inativos pelo tempo que você determinar."

    override fun getArguments() = listOf(
        Argument("meses")
    )

    override fun getParent() = FriendCommand()

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean? {
        val months = if (NumberUtils.isValidLong(args[0])) {
            TimeUnit.DAYS.toMillis(args[0].toLong()) * 30
        } else null

        if (months === null) {
            commandSender.sendMessage(TextComponent("§cTempo inválido, insira um número natural."))
            return false
        }

        if (user!!.getFriends().isEmpty()) {
            commandSender.sendMessage(TextComponent("§cVocê não possui nenhum amigo."))
            return false
        }

        if (!user.getFriends().stream().anyMatch {
            it.lastLoginAt === null || it.lastLoginAt!!.isBefore(months)
        }) {
            commandSender.sendMessage(TextComponent("§cVocê não possui nenhum amigo inativo há mais de ${TimeCode.toText(months, 1)}."))
            return false
        }

        // enviar packet pra confirmar
        return true
    }

}