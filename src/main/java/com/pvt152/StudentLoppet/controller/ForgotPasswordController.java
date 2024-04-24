package com.pvt152.StudentLoppet.controller;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pvt152.StudentLoppet.dto.MailBody;
import com.pvt152.StudentLoppet.model.ForgotPassword;
import com.pvt152.StudentLoppet.model.User;
import com.pvt152.StudentLoppet.repository.ForgotPasswordRepository;
import com.pvt152.StudentLoppet.repository.UserRepository;
import com.pvt152.StudentLoppet.service.EmailService;
import com.pvt152.StudentLoppet.service.UserService;
import com.pvt152.StudentLoppet.utilities.ChangePassword;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordController.class);
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;
    // private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService,
            ForgotPasswordRepository forgotPasswordRepository, UserService userService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userService = userService;
    }

    @PostMapping("/testPost")
    public ResponseEntity<String> testPostEndpoint() {
        return ResponseEntity.ok("POST request received successfully!");
    }
    // check if email exists, if it does, generate OTP and send it to the email.
    @PostMapping( "/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        try {
            User u = userRepository.findById(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            int otp = otpGenerator();
            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("Here is the OTP for 'forgot password': " + otp)
                    .subject("OTP for forgot password")
                    .build();

            ForgotPassword fPassword = ForgotPassword.builder()
                    .otp(otp)
                    .expirationDate(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // Sets OTP expiration time
                    .user(u)
                    .build();

            emailService.sendSimpleMessage(mailBody);
            forgotPasswordRepository.save(fPassword);

            return ResponseEntity.ok("Email sent, verification available");
        } catch (Exception e) {
            log.error("Error in verifyEmail method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // testing pull

    // @PostMapping("/reset-password")
    // public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
    // @RequestParam("newPassword") String newPassword) {
    // PasswordResetToken prt = passwordResetTokenRepository.findByToken(token);
    // if (prt == null || prt.isExpired()) {
    // return ResponseEntity.badRequest().body("Ogiltig eller utgången token.");
    // }
    // User user = prt.getUser();
    // user.setPassword(passwordEncoder.encode(newPassword));
    // userRepository.save(user);
    // return ResponseEntity.ok("Ditt lösenord har återställts.");
    // }

    // the user will enter the OTP from his email, if the OTP is correct, the user
    // is verified to be able to change his password.
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findById(email).orElseThrow(() -> new IllegalStateException("Email not found"));

        ForgotPassword fPassword = forgotPasswordRepository.findByUserAndToken(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid token for : " + email));

        if (fPassword.getExpirationDate().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fPassword.getId());
            return new ResponseEntity<>("5 minutes have passed, your token has expired!",
                    HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("token verified!");
    }

    // the user will have to enter the new password twice, if the two passwords are
    // the same, the password will be changed.
    // for frontend, the ChangePassword record will be used meaning that a record
    // that contains both the old and the new password will be sent.
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
            @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatedPassword())) {
            return new ResponseEntity<>("Enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String hashedPassword = userService.passwordHashing(changePassword.password());

        // Start a new transaction for the update operation
        userRepository.updatePassword(email, hashedPassword);

        return ResponseEntity.ok("Password has updated sucessfully");
    }
    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}