package com.redefantasy.proxy.command.defaults.staff.group

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.proxy.command.defaults.staff.group.subcommands.GroupAddCommand
import com.redefantasy.proxy.command.defaults.staff.group.subcommands.GroupRemoveCommand

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