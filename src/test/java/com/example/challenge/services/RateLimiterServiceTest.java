package com.example.challenge.services;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @Mock
    private ProxyManager<String> proxyManager;

    @Mock
    private RemoteBucketBuilder remoteBucketBuilder;

    @Mock
    BucketProxy bucketProxy;

    @Test
    public void testWhenBucketReturnTrueBecauseStillHasTokens() {

        when(proxyManager.builder()).thenReturn(remoteBucketBuilder);

        when(remoteBucketBuilder.build(anyString(), ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(bucketProxy);
        when(bucketProxy.tryConsume(1)).thenReturn(true);
        assertTrue(rateLimiterService.resolveBucket("chache").tryConsume(1));
    }

    @Test
    public void testWhenBucketReturnFalseBecauseDoesNotHaveTokens() {

        when(proxyManager.builder()).thenReturn(remoteBucketBuilder);

        when(remoteBucketBuilder.build(anyString(), ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(bucketProxy);
        when(bucketProxy.tryConsume(1)).thenReturn(false);
        assertFalse(rateLimiterService.resolveBucket("chache").tryConsume(1));
    }
}