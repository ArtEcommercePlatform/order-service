package com.artztall.order_service.service;

import com.artztall.order_service.dto.NotificationSendDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationClientServiceTest {

    @Mock
    private WebClient notificationServiceWebClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private NotificationClientService notificationClientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {
        // Arrange
        NotificationSendDTO notificationSendDTO = new NotificationSendDTO();
        notificationSendDTO.setUserId("user123");
        notificationSendDTO.setMessage("This is a test message.");
        notificationSendDTO.setType("INFO");
        notificationSendDTO.setActionUrl("http://example.com/action");

        // Mock the WebClient chain
        when(notificationServiceWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/api/notifications/send"))).thenReturn(requestBodySpec);
       // when(requestBodySpec.bodyValue(any(NotificationSendDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Act
        notificationClientService.sendNotification(notificationSendDTO);

        // Assert
        verify(notificationServiceWebClient).post();
        verify(requestBodyUriSpec).uri(eq("/api/notifications/send"));
        verify(requestBodySpec).bodyValue(eq(notificationSendDTO));
        verify(requestBodySpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }
}
