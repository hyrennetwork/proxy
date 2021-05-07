package net.hyren.proxy.listeners.connection

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.misc.preferences.PREMIUM_ACCOUNT
import net.hyren.core.shared.misc.preferences.PreferenceState
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
        val connection = event.connection
        val name = connection.name
        val user = CoreProvider.Cache.Local.USERS.provide().fetchByName(name)

        /*connection.isOnlineMode = user?.getPreferences()?.find { it == PREMIUM_ACCOUNT }?.preferenceState == PreferenceState.ENABLED*/
    }

}
