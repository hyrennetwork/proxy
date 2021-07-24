package net.hyren.proxy.command.defaults.staff.account.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.proxy.command.defaults.staff.account.AccountCommand

/**
 * @author Gutyerrez
 */
class AccountChangeCommand : CustomCommand("mudar") {

    override fun getSubCommands() = listOf(
        AccountChangeDiscordIdCommand()
    )

    override fun getParent() = AccountCommand()

}