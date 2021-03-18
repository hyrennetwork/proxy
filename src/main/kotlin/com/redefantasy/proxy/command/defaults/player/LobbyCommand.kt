package com.redefantasy.proxy.command.defaults.player

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreConstants
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * @author Gutyerrez
 */
class LobbyCommand : CustomCommand("lobby") {

    override fun getDescription() = "Teleportar-se para um saguão."

    override fun onCommand(
            commandSender: CommandSender,
            user: User?,
            args: Array<out String>
    ): Boolean {
        var bukkitApplication = user?.getConnectedBukkitApplication()

        if (bukkitApplication !== null && bukkitApplication.applicationType === ApplicationType.LOBBY) {
            commandSender.sendMessage(TextComponent("§cVocê já está no saguão."))
            return false
        }

        bukkitApplication = CoreConstants.fetchLobbyApplication()

        if (bukkitApplication === null) {
            commandSender.sendMessage(TextComponent("§cNão foi possível localizar um saguão disponível."))
            return false
        }

        commandSender as ProxiedPlayer

        commandSender.sendMessage(TextComponent("§aConectando..."))
        commandSender.connect { bukkitApplication.address }
        return true
    }

}