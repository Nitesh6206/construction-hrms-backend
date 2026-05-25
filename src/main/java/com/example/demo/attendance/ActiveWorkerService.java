package com.example.demo.attendance;

import com.example.demo.dto.ActiveWorkerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActiveWorkerService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ACTIVE_WORKERS_KEY = "active:workers";

    public void clockIn(Long workerId, Long siteId, LocalDateTime clockInTime) {
        Map<String, Object> data = Map.of(
                "workerId", workerId,
                "siteId", siteId,
                "clockInTime", clockInTime.toString()
        );
        redisTemplate.opsForHash().put(ACTIVE_WORKERS_KEY, workerId.toString(), data);
        // TTL Safety Net - 16 hours
        redisTemplate.expire(ACTIVE_WORKERS_KEY, 16, TimeUnit.HOURS);
    }

    public void clockOut(Long workerId) {
        redisTemplate.opsForHash().delete(ACTIVE_WORKERS_KEY, workerId.toString());
    }

    @SuppressWarnings("unchecked")
    public List<ActiveWorkerDTO> getActiveWorkers() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(ACTIVE_WORKERS_KEY);

        return entries.values().stream()
                .map(entry -> {
                    Map<String, Object> data = (Map<String, Object>) entry;
                    return new ActiveWorkerDTO(
                            (Long) data.get("workerId"),
                            "Worker Name",           // You can fetch name if needed
                            (Long) data.get("siteId"),
                            "Site Name",             // You can fetch site name
                            (String) data.get("clockInTime")
                    );
                })
                .collect(Collectors.toList());
    }
}