package net.hyren.proxy

import net.hyren.core.shared.providers.databases.mariadb.providers.MariaDBRepositoryProvider
import net.hyren.proxy.misc.tablist.storage.repositories.ITabListRepository
import net.hyren.proxy.misc.tablist.storage.repositories.implementations.PostgreSQLTabListRepository

/**
 * @author Gutyerrez
 */
object ProxyProvider {

    fun prepare() {
        Repositories.PostgreSQL.TAB_LIST_REPOSITORY.prepare()
    }

    object Repositories {

        object PostgreSQL {

            val TAB_LIST_REPOSITORY = MariaDBRepositoryProvider<ITabListRepository>(
                PostgreSQLTabListRepository::class
            )

        }

    }

}