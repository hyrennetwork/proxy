package com.redefantasy.proxy.misc.tablist.storage.repositories

import com.redefantasy.core.shared.storage.repositories.IRepository
import com.redefantasy.proxy.misc.tablist.data.TabList

/**
 * @author Gutyerrez
 */
interface ITabListRepository : IRepository {

    fun fetch(): TabList?

}