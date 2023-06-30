package com.firstservice.userservice.api;

import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.payload.request.ForgotPasswordRequest;
import com.firstservice.userservice.payload.response.BadRequestException;
import com.firstservice.userservice.repository.UserRepository;

import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin("http://localhost:3000/")
@RestController
@RequestMapping("/api/forgotpwd")
public class ForgotPasswordController {
    @Autowired
    private UserRepository userRepo;



    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) throws IOException {
        String email = request.getEmail();

        if (StringUtils.hasText(email)) {
            Utilisateur user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found."));

            // Generate a unique reset token
            String resetToken = UUID.randomUUID().toString();

            // Save the reset token in the user entity
            user.setResetToken(resetToken);
            userRepo.save(user);

            String subject = "Password Reset";
            String plainTextContent = "Please click on the following link to reset your password: " +
                    "http://localhost:3000/resetpassword?token=" + resetToken; // Remplacez par votre URL de réinitialisation de mot de passe
            //sendPasswordResetEmail2("rymbnslh@gmail.com", resetToken);

            sendMail(subject,"ryma","rymabnslh@gmail.com",plainTextContent);
        } else {
            throw new BadRequestException("Email is required.");
        }
    }
    public void sendMail(String subject, String recepientName, String recepientEmail, String content) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("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");

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail("rymabnslh@gmail.com");
            sender.setName("Ryma");
            List<SendSmtpEmailTo> toList = new ArrayList<>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(recepientEmail); // to make dynamic
            to.setName(recepientName); // to make dynamic
            toList.add(to);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            // sendSmtpEmail.setTemplateId(1L);
            sendSmtpEmail.setHtmlContent("<html><body>" + content + "</body></html>");
            sendSmtpEmail.setSubject(subject);

            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("Exception occurred:- " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendPasswordResetEmail(String email, String resetToken) {
        // Construct your email content using the SendinblueTransactionalEmailsApi
        // and send the email using the Sendinblue API client
        // Configure the Sendinblue API client
        /*String apiKey = "xsmtpsib-b0991b9b9a62b408074b0a9d0b1af3a923a4e32294b52553b044d0d4bd30dee2-CjZTb58SwntzH9Dx"; // Replace with your Sendinblue API key
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setApiKey(apiKey);

        // Create an instance of the Sendinblue TransactionalEmailsApi
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        // Construct the email content
        SendSmtpEmail sender = new SendSmtpEmail();

        sender.setSender(new SendSmtpEmailSender().email("rymabnslh@gmail.com").name("Ryma"));
        sender.setTo(new ArrayList<SendSmtpEmailTo>(Arrays.asList(new SendSmtpEmailTo().email("rymabnslh@gmail.com"))));
        sender.setSubject("Password Reset");
        sender.setTextContent("Please click on the link below to reset your password: \n\n" +
                "Reset Link: https://example.com/reset-password?token=" + resetToken);*/

        // Send the email using the Sendinblue API client
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("xsmtpsib-b0991b9b9a62b408074b0a9d0b1af3a923a4e32294b52553b044d0d4bd30dee2-7hXfgjyDq1GmrUxW");

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail("rymabnslh@gmail.com");
            sender.setName("Ryma");
            List<SendSmtpEmailTo> toList = new ArrayList<>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail("rymabnslh@gmail.com"); // to make dynamic
            to.setName("test"); // to make dynamic
            toList.add(to);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setTemplateId(1L);
            sendSmtpEmail.setHtmlContent("<html><body>" + "Password Reset" + "</body></html>");
            sendSmtpEmail.setSubject("Password Reset");

            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            System.out.println(response.toString());

           // CreateSmtpEmail response = apiInstance.sendTransacEmail(sender);
            System.out.println("Password reset email sent successfully. Message ID: " + response.getMessageId());
        } catch (ApiException e) {
            System.err.println("Error sending password reset email: " + e.getResponseBody());
            e.printStackTrace();
        }

    }
    private void sendPasswordResetEmail2(String email, String resetToken) throws IOException {
        Email from = new Email("rymabnslh@gmail.com"); // Remplacez par votre adresse e-mail
        String subject = "Password Reset";
        Email to = new Email(email);
        String plainTextContent = "Please click on the following link to reset your password: " +
                "http://localhost:3000/resetpassword?token=" + resetToken; // Remplacez par votre URL de réinitialisation de mot de passe
        Content content = new Content("text/plain", plainTextContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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"); // Remplacez par votre clé d'API SendGrid
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Password reset email sent successfully.");
            } else {
                throw new IOException("Failed to send password reset email. Response: " + response.getBody());
            }
        } catch (IOException ex) {
            throw new IOException("Failed to send password reset email. Error: " + ex.getMessage());
        }
    }



}
