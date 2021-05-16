package net.hyren.proxy.echo.packets.listeners

import net.hyren.core.shared.echo.api.listener.EchoPacketListener
import net.hyren.core.shared.echo.packets.DisconnectUserPacket
import net.md_5.bungee.api.ProxyServer
import org.greenrobot.eventbus.Subscribe

/**
 * @author Gutyerrez
 */
class DisconnectUserEchoPacketListener : EchoPacketListener {

    @Subscribe
    fun on(
        packet: DisconnectUserPacket
    ) {
        val userId = packet.userId!!
        val proxiedPlayer = ProxyServer.getInstance().getPlayer(userId)

        if (proxiedPlayer === null) return

        proxiedPlayer.disconnect(*packet.message!!)
    }

}