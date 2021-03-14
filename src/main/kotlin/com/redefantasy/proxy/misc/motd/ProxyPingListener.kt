package com.redefantasy.proxy.misc.motd

import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener

/**
 * @author Gutyerrez
 */
class ProxyPingListener : Listener {

    fun on(
        event: ProxyPingEvent
    ) {
//        val connection = event.connection
//
//        val image = ImageIO.read(URL("https://i.imgur.com/ttw2yTOh.jpg"))
//
//        val serverPing = ServerPing(
//            ServerPing.Protocol("fantasy-proxy", ProxyServer.getInstance().protocolVersion),
//            ServerPing.Players(0, 0, null),
//            ,
//            Favicon.create(image)
//        )

        event.response.descriptionComponent = TextComponent(
            "§f§lREDE §6§lFANTASY §7- §eloja.redefantasy.com" +
                    "\n" +
                    "§bFactions Ômega: §fabertura dia §b16/03§f às §b17:30 BR§f."
        )
    }

}