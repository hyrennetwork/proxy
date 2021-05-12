package net.hyren.proxy

import net.hyren.core.shared.providers.databases.mariadb.providers.MariaDBRepositoryProvider
import net.hyren.proxy.misc.tablist.storage.repositories.ITabListRepository
import net.hyren.proxy.misc.tablist.storage.repositories.implementations.MariaDBTabListRepository

/**
 * @author Gutyerrez
 */
object ProxyProvider {

    fun prepare() {
        Repositories.MariaDB.TAB_LIST_REPOSITORY.prepare()
    }

    object Repositories {

        object MariaDB {

            val TAB_LIST_REPOSITORY = MariaDBRepositoryProvider<ITabListRepository>(
                MariaDBTabListRepository::class
            )

        }

    }

}