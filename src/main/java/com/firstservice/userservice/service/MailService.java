package com.firstservice.userservice.service;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sendgrid.*;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    public String sendTextEmail() throws IOException {
        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email("rymabnslh@gmail.com");
        String subject = "The subject";
        Email to = new Email("rymabnslh@gmail.com");
        Content content = new Content("text/plain", "This is a test email");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }
    public String send() throws IOException {
        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email("rymabnslh@gmail.com");
        Email to = new Email("rymbnslh@gmail.com");
        Mail mail = new Mail();
        // we create an object of our static class feel free to change the class on it's own file
        // I try to keep every think simple
        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        mail.setFrom(from);
        mail.setSubject("test email");
        // This is the first_name variable that we created on the template
        personalization.addDynamicTemplateData("first_name", "ryma");
        mail.addPersonalization(personalization);
        mail.setTemplateId("d-8f484c59da9d41e1b87c5a573cbad2e8");
        // this is the api key
        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }

    // This class handels the dynamic data for the template
    // Feel free to customise this class our to putted on other file
    private static class DynamicTemplatePersonalization extends Personalization {

        @JsonProperty(value = "dynamic_template_data")
        private Map<String, String> dynamic_template_data;

        @JsonProperty("dynamic_template_data")
        public Map<String, String> getDynamicTemplateData() {
            if (dynamic_template_data == null) {
                return Collections.<String, String>emptyMap();
            }
            return dynamic_template_data;
        }

        public void addDynamicTemplateData(String key, String value) {
            if (dynamic_template_data == null) {
                dynamic_template_data = new HashMap<String, String>();
                dynamic_template_data.put(key, value);
            } else {
                dynamic_template_data.put(key, value);
            }
        }

    }

}
