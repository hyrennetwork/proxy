package com.redefantasy.proxy.misc.motd.storage.table

import net.md_5.bungee.api.chat.TextComponent
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * @author Gutyerrez
 */
object MOTDTable : Table("motd") {

    val firstLine = text("first_line")
    val secondLine = text("second_line")

    fun ResultRow.asMOTD() = TextComponent(
        String.format(
            "%s\n%s",
            this[firstLine],
            this[secondLine]
        )
    )

}