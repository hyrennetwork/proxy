package net.hyren.proxy.misc.tablist.storage.repositories

import net.hyren.core.shared.storage.repositories.IRepository
import net.hyren.proxy.misc.tablist.data.TabList

/**
 * @author Gutyerrez
 */
interface ITabListRepository : IRepository {

    fun fetch(): TabList?

}