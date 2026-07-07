package com.example.library_management.service;

import com.example.library_management.util.AuditLogger;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Autowired
    private AuditLogger log;

    @Value("${twilio.phone-number}")
    private String fromNumber;

    public void sendSms(String toNumber, String messageBody) {
        Message message = Message.creator(
                new PhoneNumber(toNumber),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();

        log.log("SMS sent, SID: {}", message.getSid());
    }
}