package net.hyren.proxy.command.defaults.staff.account

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.groups.Group
import net.hyren.proxy.command.defaults.staff.account.subcommands.AccountChangeCommand
import net.hyren.proxy.command.defaults.staff.account.subcommands.AccountRegisterCommand

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