package com.venkatesh.it.notificationmanagementservice.service;

import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendOrderCreationEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipientEmail());
            message.setSubject("Order Confirmation - " + request.getOrderId());
            message.setText(buildOrderCreationEmailBody(request));
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOrderDeliveryEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipientEmail());
            message.setSubject("Order Delivered - " + request.getOrderId());
            message.setText(buildOrderDeliveryEmailBody(request));
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOrderCancellationEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipientEmail());
            message.setSubject("Order Cancelled - " + request.getOrderId());
            message.setText(buildOrderCancellationEmailBody(request));
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOfferUpdateEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipientEmail());
            message.setSubject("Special Offer Just For You!");
            message.setText(buildOfferUpdateEmailBody(request));
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildOrderCreationEmailBody(NotificationRequest request) {
        return "Dear " + request.getCustomerName() + ",\n\n" +
                "Thank you for your order!\n\n" +
                "Order ID: " + request.getOrderId() + "\n" +
                "Order Details: " + request.getOrderDetails() + "\n\n" +
                "We will notify you once your order is shipped.\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }

    private String buildOrderDeliveryEmailBody(NotificationRequest request) {
        return "Dear " + request.getCustomerName() + ",\n\n" +
                "Great news! Your order has been delivered successfully.\n\n" +
                "Order ID: " + request.getOrderId() + "\n" +
                "Order Details: " + request.getOrderDetails() + "\n\n" +
                "Thank you for shopping with us. We hope you enjoy your purchase!\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }

    private String buildOrderCancellationEmailBody(NotificationRequest request) {
        return "Dear " + request.getCustomerName() + ",\n\n" +
                "Your order has been cancelled as requested.\n\n" +
                "Order ID: " + request.getOrderId() + "\n" +
                "Order Details: " + request.getOrderDetails() + "\n\n" +
                "If you have any questions or need further assistance, please contact our support team.\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }

    private String buildOfferUpdateEmailBody(NotificationRequest request) {
        return "Dear " + request.getCustomerName() + ",\n\n" +
                "We have an exciting offer for you!\n\n" +
                request.getOfferDetails() + "\n\n" +
                "Don't miss out on this amazing deal. Shop now!\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }
}
