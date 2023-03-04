package com.example.challenge.services;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimiterService {
    private final ProxyManager<String> proxyManager;

    public RateLimiterService(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser();

        return proxyManager.builder().build(key, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser() {

        Refill refill = Refill.intervally(3, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(3, refill);
        return () -> (BucketConfiguration.builder()
                                         .addLimit(limit)
                                         .build());
    }

}
