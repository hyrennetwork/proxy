package net.hyren.proxy.misc.tablist.listeners

import net.hyren.proxy.ProxyProvider
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.chat.ComponentSerializer
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter

/**
 * @author Gutyerrez
 */
class TabListPostLoginListener : Listener {

    @EventHandler
    fun on(
        event: PostLoginEvent
    ) {
        val player = event.player

        val tabList = ProxyProvider.Repositories.PostgreSQL.TAB_LIST_REPOSITORY.provide().fetch() ?: return

        val packet = PlayerListHeaderFooter(
            ComponentSerializer.toString(tabList.header),
            ComponentSerializer.toString(tabList.footer)
        )

        player.unsafe().sendPacket(packet)
    }

}