package com.redefantasy.proxy.misc.tablist.storage.repositories.implementations

import com.redefantasy.proxy.misc.tablist.data.TabList
import com.redefantasy.proxy.misc.tablist.storage.repositories.ITabListRepository
import com.redefantasy.proxy.misc.tablist.storage.table.TabListTable
import com.redefantasy.proxy.misc.tablist.storage.table.TabListTable.asTabList
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Gutyerrez
 */
class PostgresTabListRepository : ITabListRepository {

    override fun fetch(): TabList? {
        return transaction {
            val results = TabListTable.selectAll()

            if (results.empty()) return@transaction null

            return@transaction results.first().asTabList()
        }
    }

}