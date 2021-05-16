package net.hyren.proxy

import net.hyren.core.bungee.misc.plugin.CustomPlugin
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.status.ApplicationStatus
import net.hyren.core.shared.applications.status.task.ApplicationStatusTask
import net.hyren.core.shared.echo.packets.listener.UserPreferencesUpdatedEchoPacketListener
import net.hyren.core.shared.misc.preferences.LOBBY_COMMAND_PROTECTION
import net.hyren.core.shared.misc.preferences.PreferenceRegistry
import net.hyren.core.shared.scheduler.AsyncScheduler
import net.hyren.proxy.command.defaults.player.LobbyCommand
import net.hyren.proxy.command.defaults.player.OnlineCommand
import net.hyren.proxy.command.defaults.player.ReplyCommand
import net.hyren.proxy.command.defaults.player.TellCommand
import net.hyren.proxy.command.defaults.staff.*
import net.hyren.proxy.command.defaults.staff.account.AccountCommand
import net.hyren.proxy.command.defaults.staff.group.GroupCommand
import net.hyren.proxy.command.defaults.staff.server.ServerCommand
import net.hyren.proxy.echo.packets.listeners.BroadCastMessageEchoPacketListener
import net.hyren.proxy.echo.packets.listeners.DisconnectUserEchoPacketListener
import net.hyren.proxy.echo.packets.listeners.StaffMessageEchoPacketListener
import net.hyren.proxy.echo.packets.listeners.TellEchoPacketListener
import net.hyren.proxy.listeners.connection.PostLoginListener
import net.hyren.proxy.listeners.connection.PreLoginListener
import net.hyren.proxy.misc.login.listeners.LoginListeners
import net.hyren.proxy.misc.maintenance.command.MaintenanceCommand
import net.hyren.proxy.misc.punish.command.CheckPunishCommand
import net.hyren.proxy.misc.punish.command.PunishCommand
import net.hyren.proxy.misc.punish.command.RevokeCommand
import net.hyren.proxy.misc.punish.listener.PunishListener
import net.hyren.proxy.misc.punish.packets.listeners.UserPunishedEchoPacketListener
import net.hyren.proxy.misc.punish.packets.listeners.UserUnPunishedEchoPacketListener
import net.hyren.proxy.misc.tablist.listeners.TabListPostLoginListener
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ProxyServer
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class ProxyPlugin : CustomPlugin() {

    private var onlineSince = 0L

    override fun onEnable() {
        ProxyServer.getInstance().logger.info("Starting Proxy Server...")

        try {
            super.onEnable()

            ProxyProvider.prepare()

            this.onlineSince = System.currentTimeMillis()
            val pluginManager = ProxyServer.getInstance().pluginManager
            /**
             * Miscellaneous
             */
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
            /**
             * Punish
             */
            pluginManager.registerCommand(this, PunishCommand())
            pluginManager.registerCommand(this, RevokeCommand())
            pluginManager.registerCommand(this, CheckPunishCommand())
            /**
             * Maintenance
             */
            pluginManager.registerCommand(this, MaintenanceCommand())
            /**
             * Preferences
             */
            PreferenceRegistry.register(
                LOBBY_COMMAND_PROTECTION
            )
            /**
             * Listeners
             */
            pluginManager.registerListener(PunishListener())
            pluginManager.registerListener(LoginListeners())
            pluginManager.registerListener(PreLoginListener())
            pluginManager.registerListener(PostLoginListener())
            pluginManager.registerListener(TabListPostLoginListener())
            /**
             * ECHO
             */
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(TellEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(StaffMessageEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(BroadCastMessageEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(DisconnectUserEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserPunishedEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserUnPunishedEchoPacketListener())
            CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserPreferencesUpdatedEchoPacketListener())
            /**
             * Status
             */
            AsyncScheduler.scheduleAsyncRepeatingTask(
                object : ApplicationStatusTask(
                    ApplicationStatus(
                        CoreProvider.application.name, CoreProvider.application.applicationType, CoreProvider.application.server, CoreProvider.application.address, this.onlineSince
                    )
                ) {
                    override fun buildApplicationStatus(
                        applicationStatus: ApplicationStatus
                    ) {
                        println("AAA")
                        val runtime = Runtime.getRuntime()

                        applicationStatus.heapSize = runtime.totalMemory()
                        applicationStatus.heapMaxSize = runtime.maxMemory()
                        applicationStatus.heapFreeSize = runtime.freeMemory()
                        applicationStatus.onlinePlayers = BungeeCord.getInstance().connections.size
                    }
                }, 0, 1, TimeUnit.SECONDS
            )

            ProxyServer.getInstance().logger.info("Proxy Server started!")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}