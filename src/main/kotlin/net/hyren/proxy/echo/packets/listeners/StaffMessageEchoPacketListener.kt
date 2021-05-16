package net.hyren.proxy.echo.packets.listeners

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.echo.api.listener.EchoPacketListener
import net.hyren.core.shared.echo.packets.StaffMessagePacket
import net.hyren.core.shared.groups.Group
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ComponentBuilder
import org.greenrobot.eventbus.Subscribe
import java.util.stream.Collectors

/**
 * @author Gutyerrez
 */
class StaffMessageEchoPacketListener : EchoPacketListener {

    @Subscribe
    fun on(
            packet: StaffMessagePacket
    ) {
        val bukkitApplication = packet.bukkitApplication

        val staffers = ProxyServer.getInstance().players.stream()
                .filter {
                    val user = CoreProvider.Cache.Local.USERS.provide().fetchById(it.uniqueId)

                    user !== null && user.isLogged() && user.hasGroup(Group.HELPER)
                }
                .collect(Collectors.toList())

        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(packet.stafferId!!)
        val group = user!!.getHighestGroup()

        val message = ComponentBuilder()
                .append("§d§l[S] ")
                .append("§7(${bukkitApplication?.server?.getFancyDisplayName() ?: bukkitApplication?.getFancyDisplayName()}) ")
                .append("${group.getColoredPrefix()}${user.name}: ")
                .append("§f${packet.message}")
                .create()

        staffers.forEach { it.sendMessage(*message) }
    }

}