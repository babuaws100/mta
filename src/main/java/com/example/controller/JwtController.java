package com.example.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class JwtController {

    private static final String SECRET = "mysecretkey";

    @GetMapping("/token")
    public Map<String, String> getToken(@RequestParam String tenant) {
        String jwt = Jwts.builder()
                .setSubject("user")
                .claim("tenantId", tenant)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        return Collections.singletonMap("token", jwt);
    }


}