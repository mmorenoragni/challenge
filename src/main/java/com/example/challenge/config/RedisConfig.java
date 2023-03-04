package com.example.challenge.config;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Configuration
public class RedisConfig {

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return config;
    }

    @Bean(value = "cacheChallenge")
    public CacheManager cacheManager(Config config) {
        CacheManager manager = Caching.getCachingProviders().iterator().next().getCacheManager();
        manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        return manager;
    }

    @Bean
    public ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }
}
