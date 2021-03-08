package com.redefantasy.proxy.misc.tablist.data

import net.md_5.bungee.api.chat.BaseComponent

/**
 * @author Gutyerrez
 */
data class TabList(
    val header: Array<BaseComponent>,
    val footer: Array<BaseComponent>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as TabList

        if (!header.contentEquals(other.header) || !footer.contentEquals(other.footer)) return false

        return true
    }

    override fun hashCode(): Int {
        return this.header.hashCode() + this.footer.hashCode()
    }

}