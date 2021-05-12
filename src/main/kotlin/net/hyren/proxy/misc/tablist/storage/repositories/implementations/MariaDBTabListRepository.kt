package net.hyren.proxy.misc.tablist.storage.repositories.implementations

import net.hyren.proxy.misc.tablist.data.TabList
import net.hyren.proxy.misc.tablist.storage.repositories.ITabListRepository
import net.hyren.proxy.misc.tablist.storage.table.TabListTable
import net.hyren.proxy.misc.tablist.storage.table.TabListTable.asTabList
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Gutyerrez
 */
class MariaDBTabListRepository : ITabListRepository {

    override fun fetch(): TabList? {
        return transaction {
            val results = TabListTable.selectAll()

            if (results.empty()) return@transaction null

            return@transaction results.first().asTabList()
        }
    }

}