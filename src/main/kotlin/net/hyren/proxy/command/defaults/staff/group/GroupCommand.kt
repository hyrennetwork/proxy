package net.hyren.proxy.command.defaults.staff.group

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.groups.Group
import net.hyren.proxy.command.defaults.staff.group.subcommands.GroupAddCommand
import net.hyren.proxy.command.defaults.staff.group.subcommands.GroupRemoveCommand

/**
 * @author Gutyerrez
 */
class GroupCommand : CustomCommand("grupo"), GroupCommandRestrictable {

    override fun getDescription() = "Gerenciar grupos de um usuário"

    override fun getGroup() = Group.MANAGER

    override fun getSubCommands() = listOf(
        GroupAddCommand(),
        GroupRemoveCommand()
    )

}