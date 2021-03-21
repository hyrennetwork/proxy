package com.redefantasy.proxy.misc.motd.storage.repositories

import com.redefantasy.core.shared.storage.repositories.IRepository
import net.md_5.bungee.api.chat.TextComponent

/**
 * @author Gutyerrez
 */
interface IMOTDRepository : IRepository {

    fun fetch(): TextComponent

}