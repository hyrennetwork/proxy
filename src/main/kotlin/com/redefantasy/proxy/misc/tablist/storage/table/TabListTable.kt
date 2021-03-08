package com.redefantasy.proxy.misc.tablist.storage.table

import com.redefantasy.proxy.misc.tablist.data.TabList
import net.md_5.bungee.api.chat.TextComponent
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * @author Gutyerrez
 */
object TabListTable : Table("tab_list") {

    private val header = text("header")
    private val footer = text("footer")

    fun ResultRow.asTabList() = TabList(
        TextComponent.fromLegacyText(
            this[header]
        ),
        TextComponent.fromLegacyText(
            this[footer]
        )
    )

}