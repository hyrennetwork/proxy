package com.redefantasy.proxy

import com.redefantasy.core.bungee.misc.plugin.CustomPlugin
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.proxy.command.defaults.player.LobbyCommand
import com.redefantasy.proxy.command.defaults.player.OnlineCommand
import com.redefantasy.proxy.command.defaults.player.ReplyCommand
import com.redefantasy.proxy.command.defaults.player.TellCommand
import com.redefantasy.proxy.command.defaults.player.ignore.IgnoreCommand
import com.redefantasy.proxy.command.defaults.staff.*
import com.redefantasy.proxy.command.defaults.staff.account.AccountCommand
import com.redefantasy.proxy.command.defaults.staff.group.GroupCommand
import com.redefantasy.proxy.command.defaults.staff.server.ServerCommand
import com.redefantasy.proxy.echo.packets.listeners.BroadCastMessageEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.DisconnectUserEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.StaffMessageEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.TellEchoPacketListener
import com.redefantasy.proxy.misc.login.listeners.LoginListeners
import com.redefantasy.proxy.misc.motd.PlayerHandshakeListener
import com.redefantasy.proxy.misc.punish.command.CheckPunishCommand
import com.redefantasy.proxy.misc.punish.command.PunishCommand
import com.redefantasy.proxy.misc.punish.command.RevokeCommand
import com.redefantasy.proxy.misc.punish.listener.PunishListener
import com.redefantasy.proxy.misc.punish.packets.listeners.UserPunishedEchoPacketListener
import com.redefantasy.proxy.misc.punish.packets.listeners.UserUnPunishedEchoPacketListener
import com.redefantasy.proxy.misc.tablist.listeners.TabListPreLoginListener
import net.md_5.bungee.api.ProxyServer

/**
 * @author Gutyerrez
 */
class ProxyPlugin : CustomPlugin() {

    override fun onEnable() {
        super.onEnable()

        ProxyProvider.prepare()

        val pluginManager = ProxyServer.getInstance().pluginManager

        pluginManager.registerCommand(this, AccountCommand())
        pluginManager.registerCommand(this, GroupCommand())
        pluginManager.registerCommand(this, ServerCommand())
        pluginManager.registerCommand(this, AnnounceCommand())
        pluginManager.registerCommand(this, BTPCommand())
        pluginManager.registerCommand(this, FindCommand())
        pluginManager.registerCommand(this, KickCommand())
        pluginManager.registerCommand(this, SendCommand())
        pluginManager.registerCommand(this, StaffChatCommand())
        pluginManager.registerCommand(this, StaffListCommand())
        pluginManager.registerCommand(this, LobbyCommand())
        pluginManager.registerCommand(this, OnlineCommand())
        pluginManager.registerCommand(this, ReplyCommand())
        pluginManager.registerCommand(this, TellCommand())
        pluginManager.registerCommand(this, IgnoreCommand())

        pluginManager.registerCommand(this, PunishCommand())
        pluginManager.registerCommand(this, RevokeCommand())
        pluginManager.registerCommand(this, CheckPunishCommand())

        pluginManager.registerListener(this, PunishListener())
        pluginManager.registerListener(this, LoginListeners())
        pluginManager.registerListener(this, TabListPreLoginListener())
        pluginManager.registerListener(this, PlayerHandshakeListener())

        CoreProvider.Databases.Redis.ECHO.provide().registerListener(TellEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(StaffMessageEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(BroadCastMessageEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(DisconnectUserEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserPunishedEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserUnPunishedEchoPacketListener())
    }

}