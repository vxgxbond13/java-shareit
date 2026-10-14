package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.util.RequestHeaders;

import java.util.Map;

public abstract class BaseClient {

    protected final RestTemplate restTemplate;
    protected final String serverUrl;

    protected BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        if (params == null) {
            return restTemplate.exchange(serverUrl + path, HttpMethod.GET, entity, Object.class);
        }
        return restTemplate.exchange(serverUrl + path, HttpMethod.GET, entity, Object.class, params);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(serverUrl + path, entity, Object.class);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(serverUrl + path, HttpMethod.PATCH, entity, Object.class);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl + path, HttpMethod.DELETE, entity, Object.class);
    }
}
