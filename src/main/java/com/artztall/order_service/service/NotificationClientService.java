package com.artztall.order_service.service;

import com.artztall.order_service.dto.NotificationSendDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class NotificationClientService {
    private final WebClient notificationServiceWebClient;

    public void sendNotification(NotificationSendDTO notificationSendDTO) {
        notificationServiceWebClient.post()
                .uri("/api/notifications/send")
                .bodyValue(notificationSendDTO)
                .retrieve()
                .bodyToMono(NotificationSendDTO.class)
                // Consider adding error handling
                .onErrorMap(ex -> new RuntimeException("Failed to send notification", ex));
    }
}