package com.venkatesh.it.notificationmanagementservice.service;

import com.venkatesh.it.notificationmanagementservice.config.WhatsAppConfig;
import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    public boolean sendOrderCreationWhatsApp(NotificationRequest request) {
        try {
            String messageBody = buildOrderCreationWhatsAppMessage(request);
            Message.creator(
                    new PhoneNumber("whatsapp:" + request.getRecipientPhone()),
                    new PhoneNumber("whatsapp:" + whatsAppConfig.getFromNumber()),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOrderDeliveryWhatsApp(NotificationRequest request) {
        try {
            String messageBody = buildOrderDeliveryWhatsAppMessage(request);
            Message.creator(
                    new PhoneNumber("whatsapp:" + request.getRecipientPhone()),
                    new PhoneNumber("whatsapp:" + whatsAppConfig.getFromNumber()),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOrderCancellationWhatsApp(NotificationRequest request) {
        try {
            String messageBody = buildOrderCancellationWhatsAppMessage(request);
            Message.creator(
                    new PhoneNumber("whatsapp:" + request.getRecipientPhone()),
                    new PhoneNumber("whatsapp:" + whatsAppConfig.getFromNumber()),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOfferUpdateWhatsApp(NotificationRequest request) {
        try {
            String messageBody = buildOfferUpdateWhatsAppMessage(request);
            Message.creator(
                    new PhoneNumber("whatsapp:" + request.getRecipientPhone()),
                    new PhoneNumber("whatsapp:" + whatsAppConfig.getFromNumber()),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildOrderCreationWhatsAppMessage(NotificationRequest request) {
        return "Hello " + request.getCustomerName() + "! 🎉\n\n" +
                "Your order has been placed successfully!\n\n" +
                "📦 Order ID: " + request.getOrderId() + "\n" +
                "📋 Order Details: " + request.getOrderDetails() + "\n\n" +
                "We'll notify you once your order is shipped.\n\n" +
                "Thank you for shopping with us!";
    }

    private String buildOrderDeliveryWhatsAppMessage(NotificationRequest request) {
        return "Hello " + request.getCustomerName() + "! 🚚\n\n" +
                "Great news! Your order has been delivered successfully!\n\n" +
                "📦 Order ID: " + request.getOrderId() + "\n" +
                "📋 Order Details: " + request.getOrderDetails() + "\n\n" +
                "We hope you enjoy your purchase!\n\n" +
                "Thank you for shopping with us!";
    }

    private String buildOrderCancellationWhatsAppMessage(NotificationRequest request) {
        return "Hello " + request.getCustomerName() + "!\n\n" +
                "Your order has been cancelled as requested.\n\n" +
                "📦 Order ID: " + request.getOrderId() + "\n" +
                "📋 Order Details: " + request.getOrderDetails() + "\n\n" +
                "If you have any questions, please contact our support team.\n\n" +
                "Thank you!";
    }

    private String buildOfferUpdateWhatsAppMessage(NotificationRequest request) {
        return "Hello " + request.getCustomerName() + "! 🎁\n\n" +
                "We have an exciting offer for you!\n\n" +
                "🔥 " + request.getOfferDetails() + "\n\n" +
                "Don't miss out on this amazing deal. Shop now!\n\n" +
                "Thank you for being a valued customer!";
    }
}
