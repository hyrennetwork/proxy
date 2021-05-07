package net.hyren.proxy.command.defaults.player.ignore

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.proxy.command.defaults.player.ignore.subcommands.IgnoreAddCommand
import net.hyren.proxy.command.defaults.player.ignore.subcommands.IgnoreListCommand
import net.hyren.proxy.command.defaults.player.ignore.subcommands.IgnoreRemoveCommand

/**
 * @author Gutyerrez
 */
class IgnoreCommand : CustomCommand("ignorar") {

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getSubCommands() = listOf(
        IgnoreAddCommand(),
        IgnoreRemoveCommand(),
        IgnoreListCommand()
    )

}