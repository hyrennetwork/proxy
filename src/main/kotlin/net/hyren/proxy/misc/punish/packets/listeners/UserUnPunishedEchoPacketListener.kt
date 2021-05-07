package net.hyren.proxy.misc.punish.packets.listeners

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.echo.api.listener.EchoListener
import net.hyren.proxy.misc.punish.packets.UserUnPunishedPacket
import net.md_5.bungee.api.ProxyServer
import org.greenrobot.eventbus.Subscribe

/**
 * @author Gutyerrez
 */
class UserUnPunishedEchoPacketListener : EchoListener {

    @Subscribe
    fun on(
            packet: UserUnPunishedPacket
    ) {
        val userId = packet.userId!!
        val message = packet.message!!

        CoreProvider.Cache.Local.USERS_PUNISHMENTS.provide().invalidate(userId)

        ProxyServer.getInstance().broadcast(*message)
    }

}