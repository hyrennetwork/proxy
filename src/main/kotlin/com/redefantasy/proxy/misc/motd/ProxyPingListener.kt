package com.redefantasy.proxy.misc.motd

import com.redefantasy.core.shared.CoreConstants
import net.md_5.bungee.api.Favicon
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.protocol.packet.StatusResponse
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Gutyerrez
 */
class ProxyPingListener : Listener {

    fun on(
        event: ProxyPingEvent
    ) {
        val connection = event.connection

        val image = ImageIO.read(URL("https://i.imgur.com/ttw2yTOh.jpg"))

        val serverPing = ServerPing(
            ServerPing.Protocol("fantasy-proxy", ProxyServer.getInstance().protocolVersion),
            ServerPing.Players(0, 0, null),
            TextComponent(
                "§f§lREDE §6§lFANTASY §7- §eloja.redefantasy.com" +
                     "\n" +
                     "§bFactions Ômega: §fabertura dia §b16/03§f às §b17:30 BR§f."
            ),
            Favicon.create(image)
        )

        connection.unsafe().sendPacket(
            StatusResponse(
                CoreConstants.GSON.toJson(serverPing)
            )
        )
    }

}