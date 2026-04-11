package com.algolens.algo_lens.auth.services;


import com.algolens.algo_lens.auth.exception.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailRateLimiterService {

    private static final long COOLDOWN_SECONDS = 60;
    private static final long MAX_SENDS_PER_HOUR=3;
    private static final long HOUR_SECONDS = 3600;

    private static final String EMAIL_COOLDOWN="ev:cd:email:";
    private static final String EMAIL_HOURLY="ev:hr:email:";
    private static final String IP_COOLDOWN="ev:cd:ip:";
    private static final String IP_HOURLY="ev:hr:ip:";

    private static final long RESULT_OK=0L;
    private static final long RESULT_COOLDOWN_HIT=1L;
    private static final long RESULT_HOURLY_CAP_HIT=2L;

    private static final String RATE_LIMIT_LUA = """
        local cooldownKey = KEYS[1]
        local hourlyKey   = KEYS[2]
        local cooldownTTL = tonumber(ARGV[1])
        local maxSends    = tonumber(ARGV[2])
        local hourWindow  = tonumber(ARGV[3])
        
        -- Layer 1: cooldown check
        local ttl = redis.call('TTL', cooldownKey)
        if ttl > 0 then
            return {1, ttl}
        end
        
        -- Layer 2: hourly cap check
        local count = tonumber(redis.call('GET', hourlyKey) or '0')
        if count >= maxSends then
            local hourTTL = redis.call('TTL', hourlyKey)
            return {2, hourTTL > 0 and hourTTL or hourWindow}
        end
       
        -- All checks passed — record the send atomically
        redis.call('SET', cooldownKey, '1', 'EX', cooldownTTL)
        
        local newCount = redis.call('INCR', hourlyKey)
        if newCount == 1 then
            redis.call('EXPIRE', hourlyKey, hourWindow)
        end
        
        return {0, maxSends - newCount}
        """;
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> rateLimitScript;

    public EmailRateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(RATE_LIMIT_LUA);
        this.rateLimitScript.setResultType(List.class);
    }

    /**
     * @throws IllegalStateException  if Redis is unavailable or returns a null result
     * @throws RateLimitExceededException if rate limit is exceeded
     */
    public void checkAndRecord(String email, String ip) {
        enforceLimit(EMAIL_COOLDOWN + email, EMAIL_HOURLY + email, "email");
        enforceLimit(IP_COOLDOWN + ip,       IP_HOURLY + ip,       "IP");
    }

    @SuppressWarnings("unchecked")
    private void enforceLimit(String cooldownKey,String hourlyKey,String limitedBy) {
        List<Long> result = (List<Long>) redisTemplate.execute(
                rateLimitScript,
                List.of(cooldownKey, hourlyKey),
                String.valueOf(COOLDOWN_SECONDS),
                String.valueOf(MAX_SENDS_PER_HOUR),
                String.valueOf(HOUR_SECONDS)
        );
        if (result == null) {
            throw new IllegalStateException("Rate limiter script returned null — Redis may be unavailable");
        }

        long code =result.get(0);
        long extra=result.get(1);

        if (code == RESULT_COOLDOWN_HIT) {
            throw new RateLimitExceededException(
                    "Please wait " + extra + "s before requesting another verification email.",
                    extra
            );
        }


        if (code == RESULT_HOURLY_CAP_HIT) {
            throw new RateLimitExceededException(
                    "Too many verification emails requested. Try again in " + formatSeconds(extra) + ".",
                    extra
            );
        }



    }

    private String formatSeconds(long seconds) {
        if (seconds < 120) return seconds + " seconds";
        return (seconds / 60) + " minutes";
    }




}
