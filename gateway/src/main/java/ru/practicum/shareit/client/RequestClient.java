package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestClient extends BaseClient {
    public RequestClient(RestTemplate restTemplate, @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, Object dto) { return post("/requests", userId, dto); }
    public ResponseEntity<Object> getByUser(Long userId) { return get("/requests", userId, null); }
    public ResponseEntity<Object> getAll(Long userId) { return get("/requests/all", userId, null); }
    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/requests/" + requestId, userId, null);
    }
}
