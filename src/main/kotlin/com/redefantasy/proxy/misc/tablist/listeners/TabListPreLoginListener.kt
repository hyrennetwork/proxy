package com.redefantasy.proxy.misc.tablist.listeners

import com.redefantasy.proxy.ProxyProvider
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.chat.ComponentSerializer
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter

/**
 * @author Gutyerrez
 */
class TabListPreLoginListener : Listener {

    @EventHandler
    fun on(
        event: PreLoginEvent
    ) {
        val connection = event.connection

        val tabList = ProxyProvider.Repositories.Postgres.TAB_LIST_REPOSITORY.provide().fetch() ?: return

        val packet = PlayerListHeaderFooter(
            ComponentSerializer.toString(tabList.header),
            ComponentSerializer.toString(tabList.footer)
        )

        connection.unsafe().sendPacket(packet)
    }

}