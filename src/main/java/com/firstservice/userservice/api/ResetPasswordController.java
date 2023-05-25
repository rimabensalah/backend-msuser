package com.firstservice.userservice.api;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.payload.request.ResetPasswordRequest;
import com.firstservice.userservice.payload.response.BadRequestException;
import com.firstservice.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000/")
@Controller
@RequestMapping("/api/resetpwd")
public class ResetPasswordController {
    private final UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    public ResetPasswordController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        String resetToken = request.getResetToken();
        String newPassword = request.getNewPassword();

        if (StringUtils.hasText(resetToken) && StringUtils.hasText(newPassword)) {
            Utilisateur user = userRepository.findByResetToken(resetToken)
                    .orElseThrow(() -> new RuntimeException("User not found for the given reset token."));

            // Update the user's password with the new password
            user.setPassword(encoder.encode(newPassword));



            // Save the updated user
            userRepository.save(user);
            System.out.println("Password reset successfully.");
            // Invalidate or delete the reset token
            user.setResetToken(null);
        } else {
            throw new BadRequestException("Reset token and new password are required.");
        }
    }
    @GetMapping("reset/{resetToken}")
    public ResponseEntity<Utilisateur> getComptesById(@PathVariable(value = "resetToken") String resetToken) {
        Utilisateur user = userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Not found user with id = " + resetToken));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
