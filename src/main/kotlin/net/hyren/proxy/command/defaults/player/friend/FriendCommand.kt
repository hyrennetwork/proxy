package net.hyren.proxy.command.defaults.player.friend

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.proxy.command.defaults.player.friend.subcommands.*

/**
 * @author Gutyerrez
 */
class FriendCommand : CustomCommand("amigo") {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getSubCommands() = listOf(
        FriendAcceptCommand(),
        FriendAddCommand(),
        FriendCancelCommand(),
        FriendDeleteCommand(),
        FriendClearCommand(),
        FriendListCommand(),
        FriendDenyCommand(),
        FriendRequestsCommand()
    )

}