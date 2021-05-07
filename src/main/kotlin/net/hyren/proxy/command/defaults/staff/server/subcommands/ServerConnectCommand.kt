package net.hyren.proxy.command.defaults.staff.server.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.users.data.User
import net.hyren.proxy.command.defaults.staff.server.ServerCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * @author Gutyerrez
 */
class ServerConnectCommand : CustomCommand("conectar") {

    override fun getDescription() = "Conectar-se a um servidor."

    override fun getArguments() = listOf(
        Argument("servidor")
    )

    override fun getParent() = ServerCommand()

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        val targetServer = CoreProvider.Cache.Local.SERVERS.provide().fetchByName(args[0])

        if (targetServer === null) {
            commandSender.sendMessage(TextComponent("§cEste servidor não existe."))
            return false
        }

        val bukkitApplication = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByServerAndApplicationType(
            targetServer,
            ApplicationType.SERVER_SPAWN
        )

        if (bukkitApplication === null) {
            commandSender.sendMessage(TextComponent("§cEste servidor ainda não possui um spawn."))
            return false
        }

        commandSender as ProxiedPlayer

        commandSender.sendMessage(TextComponent("§aConectando..."))

        commandSender.connect { bukkitApplication.address }
        return true
    }

}