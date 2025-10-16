package no.hvl.dat250.experiment1.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import no.hvl.dat250.experiment1.manager.PollManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisPubSubService { // listner only
    private final PollManager pollManager;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Value("${redis.url:redis://localhost:6379}")
    
    private String redisUrl;
    private JedisPooled subscriber;
    private Thread listenerThread;
    
    @PostConstruct
    public void start() {
        subscriber = new JedisPooled(redisUrl);
        listenerThread = new Thread(() -> {
            JedisPubSub handler = new JedisPubSub() {
                @Override public void onPMessage(String pattern, String channel, String message) {
                    try {
                        PollEvent ev = mapper.readValue(message, PollEvent.class);
                        if ("vote".equalsIgnoreCase(ev.type())) {
                            pollManager.castVote(ev.pollId(), ev.optionId(), ev.voterId());
                        }
                    } catch (Exception e) {
                        System.err.println("PubSub handle error: " + e.getMessage());
                    }
                }
            };
            subscriber.psubscribe(handler, "poll:*");
        }, "redis-vote-subscriber");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    @PreDestroy
    public void stop() {
        try { 
            if (subscriber != null) subscriber.close(); 
        } catch (Exception ignored) {}
    }
}
