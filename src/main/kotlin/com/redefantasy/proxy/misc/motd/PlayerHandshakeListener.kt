package com.redefantasy.proxy.misc.motd

import com.redefantasy.core.shared.CoreConstants
import net.md_5.bungee.api.Favicon
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.PlayerHandshakeEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.chat.ComponentSerializer
import net.md_5.bungee.protocol.packet.StatusResponse
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Gutyerrez
 */
class PlayerHandshakeListener : Listener {

    fun on(
        event: PlayerHandshakeEvent
    ) {
        val connection = event.connection
        val protocol = event.handshake.protocolVersion

        val image = ImageIO.read(URL("https://i.imgur.com/ttw2yTOh.jpg"))

        val serverPing = ServerPing(
            ServerPing.Protocol("fantasy-proxy", protocol),
            ServerPing.Players(0, 0, null),
            ComponentSerializer.toString(
                ComponentBuilder()
                    .append("§f§lREDE §6§lFANTASY §7- §eloja.redefantasy.com")
                    .append("\n")
                    .append("§bFactions Ômega: §fabertura dia §b16/03§f às §b17:30 BR§f.")
                    .create()
            ),
            Favicon.create(image)
        )

        connection.unsafe().sendPacket(StatusResponse(
            CoreConstants.GSON.toJson(serverPing)
        ))
    }

}