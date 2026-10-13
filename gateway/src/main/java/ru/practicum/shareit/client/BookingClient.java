package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    public BookingClient(RestTemplate restTemplate, @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, Object dto) { return post("/bookings", userId, dto); }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        return patch("/bookings/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, userId, null);
    }

    public ResponseEntity<Object> getByUser(Long userId, String state) {
        return get("/bookings?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getByOwner(Long userId, String state) {
        return get("/bookings/owner?state={state}", userId, Map.of("state", state));
    }
}
