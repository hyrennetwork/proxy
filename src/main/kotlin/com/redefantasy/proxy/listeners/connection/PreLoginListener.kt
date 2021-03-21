package com.redefantasy.proxy.listeners.connection

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.groups.Group
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * @author Gutyerrez
 */
class PreLoginListener : Listener {

    @EventHandler
    fun on(
        event: PreLoginEvent
    ) {
        val name = event.connection.name
        val user = CoreProvider.Cache.Local.USERS.provide().fetchByName(name)

        if (CoreProvider.Repositories.Postgres.MAINTENANCE_REPOSITORY.provide().fetchByApplication(CoreProvider.application) && (user === null || !user.hasGroup(Group.MANAGER))) {
            event.setCancelReason(
                *ComponentBuilder("§c§lREDE FANTASY")
                    .append("\n\n")
                    .append("§cO servidor está atualmente em manutenção.")
                    .create()
            )
        }
    }

}