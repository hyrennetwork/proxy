package net.hyren.proxy.listeners.connection

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.misc.skin.Skin
import net.hyren.core.shared.misc.skin.controller.SkinController
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
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

		val skin: Skin? = if (user !== null) {
			CoreProvider.Cache.Local.USERS_SKINS.provide().invalidate(user.id)

			CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id)?.stream()
				?.filter { it.enabled }
				?.findFirst()
				?.orElse(null)?.skin ?: SkinController.fetchSkinByName(player.name)
		} else SkinController.fetchSkinByName(player.name)

		if (skin !== null) {
			try {
				val loginProfile = LoginResult(
					player.uuid, player.name, emptyArray()
				)

				loginProfile.properties = arrayOf(
					LoginResult.Property(
						"textures", skin.value, skin.signature
					)
				)

				val field = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredField("loginProfile")

				field.isAccessible = true

				field.set(player.pendingConnection, loginProfile)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

}