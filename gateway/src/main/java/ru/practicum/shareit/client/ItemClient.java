package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    public ItemClient(RestTemplate restTemplate, @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, Object dto) {
        return post("/items", userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, Object dto) {
        return patch("/items/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/items/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getByOwner(Long userId) {
        return get("/items", userId, null);
    }

    public ResponseEntity<Object> search(Long userId, String text) {
        return get("/items/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object dto) {
        return post("/items/" + itemId + "/comment", userId, dto);
    }
}
