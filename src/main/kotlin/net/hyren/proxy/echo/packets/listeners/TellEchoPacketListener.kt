package net.hyren.proxy.echo.packets.listeners

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.echo.api.listener.EchoPacketListener
import net.hyren.proxy.echo.packets.TellPacket
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.greenrobot.eventbus.Subscribe

/**
 * @author Gutyerrez
 */
class TellEchoPacketListener : EchoPacketListener {

    @Subscribe
    fun on(
        packet: TellPacket
    ) {
        val senderId = packet.senderId
        val receiverId = packet.receiverId
        val message = packet.message

        val sender = CoreProvider.Cache.Local.USERS.provide().fetchById(senderId!!)!!
        val receiver = CoreProvider.Cache.Local.USERS.provide().fetchById(receiverId!!)!!

        val toMessage = ComponentBuilder()
            .append("§8[Mensagem para ${receiver.getHighestGroup().getColoredPrefix()}${receiver.name}§8]: ")
            .event(
                ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND, "/tell ${receiver.name} "
                )
            )
            .append("§6$message")
            .create()

        val fromMessage = ComponentBuilder()
            .append("§8[Mensagem de ${sender.getHighestGroup().getColoredPrefix()}${sender.name}§8]: ")
            .event(
                ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND, "/tell ${sender.name} "
                )
            )
            .append("§6$message")
            .create()

        val senderPlayer = ProxyServer.getInstance().getPlayer(sender.getUniqueId())
        val receiverPlayer = ProxyServer.getInstance().getPlayer(receiver.getUniqueId())

        if (senderPlayer !== null) senderPlayer.sendMessage(*toMessage)

        if (receiverPlayer !== null) receiverPlayer.sendMessage(*fromMessage)
    }

}