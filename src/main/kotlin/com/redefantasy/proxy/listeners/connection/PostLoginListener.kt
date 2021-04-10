package com.redefantasy.proxy.listeners.connection

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.misc.skin.Skin
import com.redefantasy.core.shared.misc.skin.controller.SkinController
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.connection.InitialHandler
import net.md_5.bungee.connection.LoginResult
import net.md_5.bungee.event.EventHandler

/**
 * @author Gutyerrez
 */
class PostLoginListener : Listener {

	@EventHandler
	fun on(
		event: PostLoginEvent
	) {
		val player = event.player
		val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)

		val initialHandler = player.pendingConnection as InitialHandler

		val skin: Skin? = if (user !== null) {
			CoreProvider.Cache.Local.USERS_SKINS.provide().invalidate(user.id)

			CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id)?.stream()
				?.filter { it.enabled }
				?.findFirst()
				?.orElse(null)?.skin
		} else SkinController.fetchSkinByName(player.name)

		if (skin !== null) {
			val loginProfile = initialHandler.loginProfile

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