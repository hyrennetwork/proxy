package net.hyren.proxy.echo.packets.listeners

import net.hyren.core.shared.echo.api.listener.EchoListener
import net.hyren.core.shared.echo.packets.BroadcastMessagePacket
import net.md_5.bungee.api.ProxyServer
import org.greenrobot.eventbus.Subscribe

/**
 * @author Gutyerrez
 */
class BroadCastMessageEchoPacketListener : EchoListener {

    @Subscribe
    fun on(
        packet: BroadcastMessagePacket
    ) {
        ProxyServer.getInstance().broadcast(*packet.message!!)
    }

}