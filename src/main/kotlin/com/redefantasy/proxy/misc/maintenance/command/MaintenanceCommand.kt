package com.redefantasy.proxy.misc.maintenance.command

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.servers.status.ServerStatus
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
class MaintenanceCommand : CustomCommand("manutenção") {

    override fun getArguments() = listOf(
        Argument("servidor"),
        Argument("novo estado")
    )

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean? {
        val application = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByName(args[0])

        if (application === null) {
            commandSender.sendMessage(TextComponent("§cEste servidor não existe."))
            return false
        }

        val newState = args[1].toBoolean()

        val applicationStatus = CoreProvider.Cache.Redis.APPLICATIONS_STATUS.provide().fetchApplicationStatusByApplication(
            application,
            ServerStatus::class
        )

        if (applicationStatus === null) {
            commandSender.sendMessage(TextComponent("§cNão foi possível localizar o status dessa aplicação."))
            return false
        }

        if (applicationStatus.maintenance == newState) {
            commandSender.sendMessage(TextComponent("§cA aplicação ${applicationStatus.applicationName} já está com o modo manutenção ${if (newState) "ativado" else "desativado"}."))
            return false
        }

        // llllllll
        return true
    }

}