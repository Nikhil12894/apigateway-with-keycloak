package com.nk.auth_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/validate")
    public ResponseEntity<String> validateRequest() {
        // If the token is valid, this endpoint is reached.
        return ResponseEntity.ok("Token is valid");
    }
}
