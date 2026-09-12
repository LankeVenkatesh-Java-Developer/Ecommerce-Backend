package com.venkatesh.it.notificationmanagementservice.controller;

import com.venkatesh.it.notificationmanagementservice.model.NotificationChannel;
import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationResponse;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import com.venkatesh.it.notificationmanagementservice.service.EmailService;
import com.venkatesh.it.notificationmanagementservice.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private WhatsAppService whatsAppService;

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = new NotificationResponse();
        
        boolean emailSuccess = false;
        boolean whatsappSuccess = false;

        if (request.getChannel() == NotificationChannel.EMAIL || request.getChannel() == NotificationChannel.BOTH) {
            emailSuccess = sendEmailNotification(request);
            response.setEmailStatus(emailSuccess ? "Sent" : "Failed");
        }

        if (request.getChannel() == NotificationChannel.WHATSAPP || request.getChannel() == NotificationChannel.BOTH) {
            whatsappSuccess = sendWhatsAppNotification(request);
            response.setWhatsappStatus(whatsappSuccess ? "Sent" : "Failed");
        }

        response.setSuccess(emailSuccess || whatsappSuccess);
        response.setMessage(emailSuccess || whatsappSuccess ? "Notification sent successfully" : "Failed to send notification");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/order-created")
    public ResponseEntity<NotificationResponse> orderCreated(@RequestBody NotificationRequest request) {
        request.setNotificationType(NotificationType.ORDER_CREATED);
        return sendNotification(request);
    }

    @PostMapping("/order-delivered")
    public ResponseEntity<NotificationResponse> orderDelivered(@RequestBody NotificationRequest request) {
        request.setNotificationType(NotificationType.ORDER_DELIVERED);
        return sendNotification(request);
    }

    @PostMapping("/order-cancelled")
    public ResponseEntity<NotificationResponse> orderCancelled(@RequestBody NotificationRequest request) {
        request.setNotificationType(NotificationType.ORDER_CANCELLED);
        return sendNotification(request);
    }

    @PostMapping("/offer-update")
    public ResponseEntity<NotificationResponse> offerUpdate(@RequestBody NotificationRequest request) {
        request.setNotificationType(NotificationType.OFFER_UPDATE);
        return sendNotification(request);
    }

    private boolean sendEmailNotification(NotificationRequest request) {
        switch (request.getNotificationType()) {
            case ORDER_CREATED:
                return emailService.sendOrderCreationEmail(request);
            case ORDER_DELIVERED:
                return emailService.sendOrderDeliveryEmail(request);
            case ORDER_CANCELLED:
                return emailService.sendOrderCancellationEmail(request);
            case OFFER_UPDATE:
                return emailService.sendOfferUpdateEmail(request);
            default:
                return false;
        }
    }

    private boolean sendWhatsAppNotification(NotificationRequest request) {
        switch (request.getNotificationType()) {
            case ORDER_CREATED:
                return whatsAppService.sendOrderCreationWhatsApp(request);
            case ORDER_DELIVERED:
                return whatsAppService.sendOrderDeliveryWhatsApp(request);
            case ORDER_CANCELLED:
                return whatsAppService.sendOrderCancellationWhatsApp(request);
            case OFFER_UPDATE:
                return whatsAppService.sendOfferUpdateWhatsApp(request);
            default:
                return false;
        }
    }
}
