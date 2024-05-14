package com.final_project_spring.controller;

import com.final_project_spring.entity.Token;
import com.final_project_spring.repository.TokenRepository;
import com.final_project_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ActivationController {

    private static final Logger logger = LoggerFactory.getLogger(ActivationController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

//    @GetMapping("/activate")
//    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
//        logger.info("Attempting to activate account with token: {}", token);
//        Optional<Token> activationToken = tokenRepository.findByToken(token);
//
//        if (!activationToken.isPresent()) {
//            logger.error("Invalid activation token: {}", token);
//            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Invalid activation token.</h1></body></html>");
//        }
//
//        Token storedToken = activationToken.get();
//        if (storedToken.isExpired() || storedToken.isRevoked()) {
//            logger.error("Expired or revoked token: {}", token);
//            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Token has expired or has been revoked.</h1></body></html>");
//        }
//
//        User user = storedToken.getUser();
//        if (!user.isEnabled()) {
//            user.setEnabled(true);
//            userRepository.save(user);
//            storedToken.setRevoked(true);
//            tokenRepository.save(storedToken);
//            logger.info("User activated: {}", user.getUsername());
//            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Account activated successfully.</h1></body></html>");
//        } else {
//            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Account is already activated.</h1></body></html>");
//        }
//    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        logger.info("Attempting to confirm email with token: {}", token);
        Optional<Token> activationToken = tokenRepository.findByToken(token);

        if (!activationToken.isPresent()) {
            logger.error("Invalid token: {}", token);
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Invalid token.</h1></body></html>");
        }

        // Optionally update the token's state to reflected it has been used
        Token storedToken = activationToken.get();
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body("<html><body><h1>Email confirmed successfully.</h1></body></html>");
    }

}
