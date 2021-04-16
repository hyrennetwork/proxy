package com.redefantasy.proxy.command.defaults.staff.account

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.proxy.command.defaults.staff.account.subcommands.AccountChangeCommand
import com.redefantasy.proxy.command.defaults.staff.account.subcommands.AccountRegisterCommand

/**
 * @author Gutyerrez
 */
open class AccountCommand : CustomCommand("conta"), GroupCommandRestrictable {

    override fun getCommandRestriction() = CommandRestriction.CONSOLE_AND_GAME

    override fun getDescription() = "Atualizar os dados da conta de um usuário."

    override fun getSubCommands() = listOf(
        AccountChangeCommand(),
        AccountRegisterCommand()
    )

    override fun getGroup() = Group.MANAGER

}