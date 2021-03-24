package com.redefantasy.proxy

import com.redefantasy.core.bungee.misc.plugin.CustomPlugin
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.status.ApplicationStatus
import com.redefantasy.core.shared.applications.status.task.ApplicationStatusTask
import com.redefantasy.core.shared.misc.preferences.PreferenceRegistry
import com.redefantasy.core.shared.misc.preferences.data.TellPreference
import com.redefantasy.core.shared.scheduler.AsyncScheduler
import com.redefantasy.proxy.command.defaults.player.LobbyCommand
import com.redefantasy.proxy.command.defaults.player.OnlineCommand
import com.redefantasy.proxy.command.defaults.player.ReplyCommand
import com.redefantasy.proxy.command.defaults.player.TellCommand
import com.redefantasy.proxy.command.defaults.staff.*
import com.redefantasy.proxy.command.defaults.staff.account.AccountCommand
import com.redefantasy.proxy.command.defaults.staff.group.GroupCommand
import com.redefantasy.proxy.command.defaults.staff.server.ServerCommand
import com.redefantasy.proxy.echo.packets.listeners.BroadCastMessageEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.DisconnectUserEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.StaffMessageEchoPacketListener
import com.redefantasy.proxy.echo.packets.listeners.TellEchoPacketListener
import com.redefantasy.proxy.listeners.connection.PreLoginListener
import com.redefantasy.proxy.misc.login.listeners.LoginListeners
import com.redefantasy.proxy.misc.maintenance.command.MaintenanceCommand
import com.redefantasy.proxy.misc.punish.command.CheckPunishCommand
import com.redefantasy.proxy.misc.punish.command.PunishCommand
import com.redefantasy.proxy.misc.punish.command.RevokeCommand
import com.redefantasy.proxy.misc.punish.listener.PunishListener
import com.redefantasy.proxy.misc.punish.packets.listeners.UserPunishedEchoPacketListener
import com.redefantasy.proxy.misc.punish.packets.listeners.UserUnPunishedEchoPacketListener
import com.redefantasy.proxy.misc.tablist.listeners.TabListPostLoginListener
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class ProxyPlugin : CustomPlugin() {

    private var onlineSince = 0L

    override fun onEnable() {
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
         * Listeners
         */

        pluginManager.registerListener(PunishListener())
        pluginManager.registerListener(LoginListeners())
        pluginManager.registerListener(PreLoginListener())
        pluginManager.registerListener(TabListPostLoginListener())

        pluginManager.registerListener(
            object : Listener {
                @EventHandler
                fun on(
                    event: ProxyPingEvent
                ) {
                    val connection = event.connection

                    event.response.players.max = 600
                    event.response.players.online = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsers().size

                    event.response.version.name = "Fantasy Proxy"
                    event.response.version.protocol = connection.version

                    val applicationStatus =
                        CoreProvider.Cache.Redis.APPLICATIONS_STATUS.provide().fetchApplicationStatusByApplication(
                            CoreProvider.application,
                            ApplicationStatus::class
                        )

                    val motd = ProxyProvider.Cache.Local.MOTD.provide().fetch()

                    if (motd === null) {
                        event.response.descriptionComponent = TextComponent("Não foi possível carregar a MOTD.")
                    } else {
                        if (applicationStatus !== null && CoreProvider.Repositories.Postgres.MAINTENANCE_REPOSITORY.provide()
                                .fetchByApplication(
                                    CoreProvider.application
                                )
                        ) {
                            val firstLine = motd.toLegacyText().split("\n")[0]

                            event.response.descriptionComponent = TextComponent(
                                String.format(
                                    "%s\n%s",
                                    firstLine,
                                    "§cO servidor atualmente encontra-se em manutenção."
                                )
                            )
                        } else {
                            event.response.descriptionComponent = motd
                        }
                    }
                }
            }
        )

        /**
         * Preferences
         */

        PreferenceRegistry.register(
            TellPreference()
        )

        /**
         * ECHO
         */

        CoreProvider.Databases.Redis.ECHO.provide().registerListener(TellEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(StaffMessageEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(BroadCastMessageEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(DisconnectUserEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserPunishedEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(UserUnPunishedEchoPacketListener())

        /**
         * Status
         */

        AsyncScheduler.scheduleAsyncRepeatingTask(
            object : ApplicationStatusTask(
                ApplicationStatus(
                    CoreProvider.application.name,
                    CoreProvider.application.applicationType,
                    CoreProvider.application.server,
                    CoreProvider.application.address,
                    this.onlineSince
                )
            ) {
                override fun buildApplicationStatus(
                    applicationStatus: ApplicationStatus
                ) {
                    val runtime = Runtime.getRuntime()

                    applicationStatus.heapSize = runtime.totalMemory()
                    applicationStatus.heapMaxSize = runtime.maxMemory()
                    applicationStatus.heapFreeSize = runtime.freeMemory()
                }
            },
            0,
            1,
            TimeUnit.SECONDS
        )
    }

}