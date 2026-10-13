package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient extends BaseClient {
    public UserClient(RestTemplate restTemplate, @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Object dto) {
        return post("/users", null, dto);
    }

    public ResponseEntity<Object> update(Long userId, Object dto) {
        return patch("/users/" + userId, null, dto);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/users/" + userId, null, null);
    }

    public ResponseEntity<Object> getAll() {
        return get("/users", null, null);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/users/" + userId, null);
    }
}
