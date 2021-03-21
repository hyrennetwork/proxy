package com.redefantasy.proxy.misc.motd.storage.cache.local

import com.github.benmanes.caffeine.cache.Caffeine
import com.redefantasy.core.shared.cache.local.LocalCache
import com.redefantasy.proxy.ProxyProvider
import net.md_5.bungee.api.chat.TextComponent
import java.util.concurrent.TimeUnit

/**
  * @author Gutyerrez
  */
class MOTDLocalCache : LocalCache {

      private val CACHE = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build<Any, TextComponent> {
                  ProxyProvider.Repositories.Postgres.MOTD_REPOSITORY.provide().fetch()
            }

      fun fetch() = this.CACHE.get("proxy-motd")

}