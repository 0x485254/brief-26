package com.easygroup.service;

import com.easygroup.dto.GroupResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PreviewCache {

    private final Map<String, PreviewData> cache = new ConcurrentHashMap<>();

    public void store(UUID userId, UUID listId, List<GroupResponse> groups) {
        String key = userId + ":" + listId;
        cache.put(key, new PreviewData(groups, LocalDateTime.now().plusMinutes(30)));
    }

    public List<GroupResponse> get(UUID userId, UUID listId) {
        String key = userId + ":" + listId;
        PreviewData data = cache.get(key);

        if (data == null || data.isExpired()) {
            cache.remove(key);
            return null;
        }

        return data.groups;
    }

    public void remove(UUID userId, UUID listId) {
        cache.remove(userId + ":" + listId);
    }

    @Scheduled(fixedRate = 3600000) // Clean every hour
    public void cleanup() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private static class PreviewData {
        final List<GroupResponse> groups;
        final LocalDateTime expires;

        PreviewData(List<GroupResponse> groups, LocalDateTime expires) {
            this.groups = groups;
            this.expires = expires;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expires);
        }
    }
}