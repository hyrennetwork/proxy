package com.redefantasy.proxy.listeners.connection

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.misc.preferences.PREMIUM_ACCOUNT
import com.redefantasy.core.shared.misc.preferences.PreferenceState
import com.redefantasy.core.shared.misc.skin.Skin
import com.redefantasy.core.shared.misc.skin.controller.SkinController
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.connection.InitialHandler
import net.md_5.bungee.connection.LoginResult
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

        if (CoreProvider.Cache.Local.MAINTENANCE.provide().fetch(CoreProvider.application) == true) {
            when {
                user === null || !user.hasGroup(Group.MANAGER) -> {
                    event.setCancelReason(
                        *ComponentBuilder("§c§lREDE FANTASY")
                            .append("\n\n")
                            .append("§cO servidor está atualmente em manutenção.")
                            .create()
                    )
                    event.isCancelled = true
                }
            }
        }

        if (user?.getPreferences()?.find { it == PREMIUM_ACCOUNT }?.preferenceState == PreferenceState.ENABLED) {
            connection.isOnlineMode = true
        }

        val skin: Skin? =
            if (user !== null && CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id) !== null
                && CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id)!!.isNotEmpty()
            ) {
                CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id)?.stream()
                    ?.filter { it.enabled }
                    ?.findFirst()
                    ?.orElse(null)?.skin
            } else SkinController.fetchSkinByName(connection.name)

        if (skin !== null) {
            val loginProfile = (connection as InitialHandler).loginProfile

            loginProfile.properties = arrayOf(
                LoginResult.Property(
                    "textures",
                    skin.value,
                    skin.signature
                )
            )
        }
    }

}