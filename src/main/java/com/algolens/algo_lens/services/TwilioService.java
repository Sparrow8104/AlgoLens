package com.algolens.algo_lens.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioService {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials missing. SMS/Voice will fail.");
        }
    }

    public void sendSms(String toPhoneNumber, String text) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    text
            ).create();
            log.info("Sent SMS to {}: SID {}", toPhoneNumber, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }

    public void makeAgenticCall(String toPhoneNumber, String speechText) {
        try {
            // Using TwiML <Say> to read out text. This is a basic agentic Text-To-Speech response.
            String twimlStr = "<Response><Say>" + speechText + "</Say></Response>";
            Call call = Call.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    new Twiml(twimlStr)
            ).create();
            log.info("Initiated agentic voice call to {}: SID {}", toPhoneNumber, call.getSid());
        } catch (Exception e) {
            log.error("Failed to make agentic call to {}: {}", toPhoneNumber, e.getMessage());
        }
    }
}
