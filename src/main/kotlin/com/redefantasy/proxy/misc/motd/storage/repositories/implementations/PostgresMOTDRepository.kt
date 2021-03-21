package com.redefantasy.proxy.misc.motd.storage.repositories.implementations

import com.redefantasy.proxy.misc.motd.storage.repositories.IMOTDRepository
import com.redefantasy.proxy.misc.motd.storage.table.MOTDTable
import com.redefantasy.proxy.misc.motd.storage.table.MOTDTable.asMOTD
import net.md_5.bungee.api.chat.TextComponent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Gutyerrez
 */
class PostgresMOTDRepository : IMOTDRepository {

    override fun fetch(): TextComponent {
        return transaction {
            val result = MOTDTable.selectAll()

            if (result.empty()) return@transaction TextComponent("Falha ao carregar MOTD.")

            return@transaction result.first().asMOTD()
        }
    }

}