package net.hyren.proxy.misc.punish.listener

import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.misc.utils.TimeCode
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.function.Supplier

/**
 * @author Gutyerrez
 */
class PunishListener : Listener {

    @EventHandler
    fun on(
        event: PreLoginEvent
    ) {
        val connection = event.connection
        val userId = connection.uniqueId
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(userId)

        if (user === null) return

        val message = user.validatePunishments()

        if (message !== null) {
            event.setCancelReason(*message)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(
        event: ChatEvent
    ) {
        val proxiedPlayer = event.sender as ProxiedPlayer
        val userId = proxiedPlayer.uniqueId
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(userId)
        val message = if (event.message.contains(" ")) {
            event.message.split(" ")[0]
        } else event.message

        if (user === null ) return

        val currentActiveMutePunishment = user.isMuted()

        if (currentActiveMutePunishment !== null && !CoreConstants.UN_LOGGED_ALLOWED_COMMANDS.stream().anyMatch {
                it.contentEquals(message)
        }) {
            val staffer = CoreProvider.Cache.Local.USERS.provide().fetchById(
                currentActiveMutePunishment.stafferId
            )

            val proof = currentActiveMutePunishment.proof
            val supplier = Supplier<Boolean> {
                if ((event.isCommand && arrayOf("/g", "/tell").contains(message.trim().toLowerCase())) || !event.isCommand) {
                    return@Supplier true
                }

                return@Supplier false
            }

            if (supplier.get()) {
                event.isCancelled = true

                proxiedPlayer.sendMessage(
                    *ComponentBuilder("\n")
                        .append("??c * Voc?? foi ${currentActiveMutePunishment.punishType.sampleName} por ${staffer?.name}.")
                        .append("\n")
                        .append("??c * Motivo: ${currentActiveMutePunishment.punishCategory?.displayName}${if (proof !== null) " - $proof" else ""}")
                        .append("\n")
                        .append("??c * Dura????o: ${TimeCode.toText(currentActiveMutePunishment.duration, 1)}")
                        .append("\n")
                        .create()
                )
            }
        }
    }

}