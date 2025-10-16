package no.hvl.dat250.experiment1.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;

@Component
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisPublisher {
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Value("${redis.url:redis://localhost:6379}")
    
    private String redisUrl;
    private UnifiedJedis publisher;
    
    @PostConstruct
    void start() { 
        publisher = new UnifiedJedis(redisUrl); 
    }

    @PreDestroy
    void stop() { 
        if (publisher != null) publisher.close(); 
    }
    
    public void publish(String channel, PollEvent event) {
        try {
            publisher.publish(channel, mapper.writeValueAsString(event));
        } catch (Exception e) {
            System.err.println("Publish failed: " + e.getMessage());
        }
    }
    
    public static String channelFor(long pollId) { 
        return "poll:" + pollId; 
    }
}
