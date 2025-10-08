package no.hvl.dat250.experiment1.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;

import java.util.Map;

@Component
public class RedisCache implements AutoCloseable {
    private final UnifiedJedis jedis;
    private final int ttlSeconds;
    
    public RedisCache(
        @Value("${redis.url:redis://localhost:6379}") String url,
        @Value("${redis.ttlSeconds:300}") int ttlSeconds) {
            this.jedis = new UnifiedJedis(url);
            this.ttlSeconds = ttlSeconds;
        }
        
    public Map<String,String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }
    
    public void hsetAndExpire(String key, Map<String,String> map) {
        if (map != null && !map.isEmpty()) {
            jedis.hset(key, map);
            if (ttlSeconds > 0) jedis.expire(key, ttlSeconds);
        }
    }
    
    public void hincrBy(String key, String field, long by) {
        jedis.hincrBy(key, field, by);
    }
    
    public void del(String key) {
        jedis.del(key);
    }
    
    @Override public void close() { jedis.close(); }

}