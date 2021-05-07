package net.hyren.proxy

import net.hyren.core.shared.providers.databases.postgres.providers.PostgresRepositoryProvider
import net.hyren.proxy.misc.tablist.storage.repositories.ITabListRepository
import net.hyren.proxy.misc.tablist.storage.repositories.implementations.PostgresTabListRepository

/**
 * @author Gutyerrez
 */
object ProxyProvider {

    fun prepare() {
        Repositories.Postgres.TAB_LIST_REPOSITORY.prepare()
    }

    object Repositories {

        object Postgres {

            val TAB_LIST_REPOSITORY = PostgresRepositoryProvider<ITabListRepository>(
                PostgresTabListRepository::class
            )

        }

    }

}