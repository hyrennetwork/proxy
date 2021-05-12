package net.hyren.proxy.misc.maintenance.command

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.entities.implementations.GroupCommandRestrictable
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class MaintenanceCommand : CustomCommand("manutenção"), GroupCommandRestrictable {

    override fun getArguments() = listOf(
        Argument("servidor"),
        Argument("novo estado")
    )

    override fun getGroup() = Group.DIRECTOR

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean? {
        val application = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByName(args[0])

        if (application === null) {
            commandSender.sendMessage(TextComponent("§cEsta aplicação não existe."))
            return false
        }

        val currentState = CoreProvider.Repositories.MariaDB.MAINTENANCE_REPOSITORY.provide().fetchByApplication(application)
        val newState = args[1].toBoolean()

        if (currentState == newState) {
            commandSender.sendMessage(
                TextComponent("§cA aplicação ${application.name} já está com o modo manutenção ${if (newState) "ativado" else "desativado"}.")
            )
            return false
        }

        CoreProvider.Repositories.MariaDB.MAINTENANCE_REPOSITORY.provide().update(
            application,
            newState
        )

        commandSender.sendMessage(
            TextComponent(
                "§aO estado da aplicação ${application.name} foi alterado para ${
                    if (newState) {
                        "em manutenção"
                    } else "aberto"
                }."
            )
        )
        return true
    }

}