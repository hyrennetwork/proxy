package net.hyren.proxy.command.defaults.player.friend.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.misc.utils.NumberUtils
import net.hyren.core.shared.users.data.User
import net.hyren.proxy.command.defaults.player.friend.FriendCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder

/**
 * @author Gutyerrez
 */
class FriendRequestsCommand : CustomCommand("pedidos") {

    override fun getParent() = FriendCommand()

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        val friendRequests = user!!.getFriendRequests()

        val pages = friendRequests.size / 10
        val page = if (args.size == 1 && NumberUtils.isValidInteger(args[0])) {
            args[0].toInt()
        } else 0

        val message = ComponentBuilder()
            .append("\n")
            .append("§2Pedidos de amizade $page/$pages:")
            .append("\n\n")

        friendRequests.forEachIndexed { index, it ->
            message.append("§f - ${it.getFancyName()}")

            if (index + 1 < friendRequests.size) message.append("\n")
        }

        commandSender.sendMessage(*message.create())
        return false
    }

}