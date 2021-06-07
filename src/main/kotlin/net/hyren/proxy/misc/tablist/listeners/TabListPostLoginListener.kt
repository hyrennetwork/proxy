package net.hyren.proxy.misc.tablist.listeners

import net.hyren.proxy.ProxyProvider
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * @author Gutyerrez
 */
class TabListPostLoginListener : Listener {

    @EventHandler
    fun on(
        event: PostLoginEvent
    ) {
        val player = event.player

        ProxyProvider.Repositories.PostgreSQL.TAB_LIST_REPOSITORY.provide().fetch()?.let {
            player.setTabHeader(it.header, it.footer)
        }
    }

}