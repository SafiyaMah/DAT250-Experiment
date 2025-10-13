package no.hvl.dat250.experiment1.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import no.hvl.dat250.experiment1.manager.PollManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

@Component
@RequiredArgsConstructor
public class RedisPubSubService {
    private final PollManager pollManager;     // to call castVote(...)
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${redis.url:redis://localhost:6379}")
    private String redisUrl;

    private UnifiedJedis publisher; // lightweight publisher
    private JedisPooled subscriber; // dedicated pooled client for PSUBSCRIBE
    private Thread listenerThread;
    private volatile boolean running = false;

    @PostConstruct
    public void start() {
        publisher = new UnifiedJedis(redisUrl);
        subscriber = new JedisPooled(redisUrl);
        running = true;

        listenerThread = new Thread(() -> {
        JedisPubSub handler = new JedisPubSub() {
            @Override
            public void onPMessage(String pattern, String channel, String message) {
            try {
                PollEvent ev = mapper.readValue(message, PollEvent.class);
                if ("vote".equalsIgnoreCase(ev.type())) {
                // delegate to existing logic (validates and updates cache)
                pollManager.castVote(ev.pollId(), ev.optionId(), ev.voterId());
                }
            } catch (Exception e) {
                // log and continue
                System.err.println("PubSub parse/handle error: " + e.getMessage());
            }
            }
        };
        // Listen to all polls using pollId
        subscriber.psubscribe(handler, "poll:*");
        }, "redis-pubsub-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @PreDestroy
    public void stop() {
        try {
        running = false;
        if (subscriber != null) subscriber.close();
        if (publisher != null) publisher.close();
        } catch (Exception ignored) {}
    }

    // Publish a JSON event to a poll channel
    public void publish(String channel, PollEvent event) {
        try {
        String json = mapper.writeValueAsString(event);
        publisher.publish(channel, json);
        } catch (Exception e) {
        System.err.println("Publish failed: " + e.getMessage());
        }
    }

    // Helper for channel naming
    public static String channelFor(long pollId) {
        return "poll:" + pollId;
    }
}
