package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.exception.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimiterService {

    private static final long COOLDOWN_SECONDS = 60;
    private static final long MAX_SENDS_PER_HOUR = 10;
    private static final long HOUR_SECONDS = 3600;

    private static final long PHONE_COOLDOWN_SECONDS = 60;
    private static final long PHONE_MAX_SENDS_PER_HOUR = 5;

    private static final String EMAIL_COOLDOWN = "ev:cd:email:";
    private static final String EMAIL_HOURLY = "ev:hr:email:";
    private static final String IP_COOLDOWN = "ev:cd:ip:";
    private static final String IP_HOURLY = "ev:hr:ip:";

    private static final String PHONE_COOLDOWN = "phone:cd:";
    private static final String PHONE_HOURLY = "phone:hr:";
    private static final String PHONE_IP_COOLDOWN = "phone:cd:ip:";
    private static final String PHONE_IP_HOURLY = "phone:hr:ip:";

    private static final String MULTI_RATE_LIMIT_LUA = """
        local cooldownKey1 = KEYS[1]
        local hourlyKey1   = KEYS[2]
        local cooldownKey2 = KEYS[3]
        local hourlyKey2   = KEYS[4]
        
        local cooldownTTL1 = tonumber(ARGV[1])
        local maxSends1    = tonumber(ARGV[2])
        local hourWindow1  = tonumber(ARGV[3])
        local cooldownTTL2 = tonumber(ARGV[4])
        local maxSends2    = tonumber(ARGV[5])
        local hourWindow2  = tonumber(ARGV[6])
        
        -- Check Limit 1 (Email/Phone)
        local ttl1 = redis.call('TTL', cooldownKey1)
        if ttl1 > 0 then
            return {1, ttl1}
        end
        local count1 = tonumber(redis.call('GET', hourlyKey1) or '0')
        if count1 >= maxSends1 then
            local hourTTL1 = redis.call('TTL', hourlyKey1)
            return {2, hourTTL1 > 0 and hourTTL1 or hourWindow1}
        end
        
        -- Check Limit 2 (IP)
        local ttl2 = redis.call('TTL', cooldownKey2)
        if ttl2 > 0 then
            return {3, ttl2}
        end
        local count2 = tonumber(redis.call('GET', hourlyKey2) or '0')
        if count2 >= maxSends2 then
            local hourTTL2 = redis.call('TTL', hourlyKey2)
            return {4, hourTTL2 > 0 and hourTTL2 or hourWindow2}
        end
        
        -- All checks passed — commit changes atomically
        redis.call('SET', cooldownKey1, '1', 'EX', cooldownTTL1)
        local newCount1 = redis.call('INCR', hourlyKey1)
        if newCount1 == 1 then
            redis.call('EXPIRE', hourlyKey1, hourWindow1)
        end
        
        redis.call('SET', cooldownKey2, '1', 'EX', cooldownTTL2)
        local newCount2 = redis.call('INCR', hourlyKey2)
        if newCount2 == 1 then
            redis.call('EXPIRE', hourlyKey2, hourWindow2)
        end
        
        return {0, 0}
        """;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> rateLimitScript;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(MULTI_RATE_LIMIT_LUA);
        this.rateLimitScript.setResultType(List.class);
    }

    public void checkAndRecordEmail(String email, String ip) {
        enforceLimit(
                EMAIL_COOLDOWN + email, EMAIL_HOURLY + email, "email", COOLDOWN_SECONDS, MAX_SENDS_PER_HOUR,
                IP_COOLDOWN + ip, IP_HOURLY + ip, "IP", COOLDOWN_SECONDS, MAX_SENDS_PER_HOUR
        );
    }

    public void checkAndRecordPhone(String phoneNumber, String ip) {
        enforceLimit(
                PHONE_COOLDOWN + phoneNumber, PHONE_HOURLY + phoneNumber, "phone number", PHONE_COOLDOWN_SECONDS, PHONE_MAX_SENDS_PER_HOUR,
                PHONE_IP_COOLDOWN + ip, PHONE_IP_HOURLY + ip, "IP", PHONE_COOLDOWN_SECONDS, PHONE_MAX_SENDS_PER_HOUR
        );
    }

    @SuppressWarnings("unchecked")
    private void enforceLimit(
            String cooldownKey1, String hourlyKey1, String limitedBy1, long cooldownSeconds1, long maxSendsPerHour1,
            String cooldownKey2, String hourlyKey2, String limitedBy2, long cooldownSeconds2, long maxSendsPerHour2
    ) {
        List<Long> result = (List<Long>) redisTemplate.execute(
                rateLimitScript,
                List.of(cooldownKey1, hourlyKey1, cooldownKey2, hourlyKey2),
                String.valueOf(cooldownSeconds1),
                String.valueOf(maxSendsPerHour1),
                String.valueOf(HOUR_SECONDS),
                String.valueOf(cooldownSeconds2),
                String.valueOf(maxSendsPerHour2),
                String.valueOf(HOUR_SECONDS)
        );
        if (result == null) {
            throw new IllegalStateException("Rate limiter script returned null — Redis may be unavailable");
        }

        long code = result.get(0);
        long extra = result.get(1);

        if (code == 1) {
            throw new RateLimitExceededException(
                    "Please wait " + extra + "s before requesting another verification " + (limitedBy1.equals("phone number") ? "OTP" : "email") + ".",
                    extra
            );
        }
        if (code == 2) {
            throw new RateLimitExceededException(
                    "Too many verification requests. Try again in " + formatSeconds(extra) + ".",
                    extra
            );
        }
        if (code == 3) {
            throw new RateLimitExceededException(
                    "Please wait " + extra + "s before requesting another verification from this IP.",
                    extra
            );
        }
        if (code == 4) {
            throw new RateLimitExceededException(
                    "Too many verification requests from this IP. Try again in " + formatSeconds(extra) + ".",
                    extra
            );
        }
    }

    private String formatSeconds(long seconds) {
        if (seconds < 120) return seconds + " seconds";
        return (seconds / 60) + " minutes";
    }
}
