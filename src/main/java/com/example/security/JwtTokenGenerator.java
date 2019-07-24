package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Generate token
 */
public class JwtTokenGenerator {

    public static void main(String[] args) {

        System.out.println(generateToken("johnwick", "user"));
        System.out.println(generateToken("jasonbourne", "user"));
        System.out.println(generateToken("donniedarko", "user"));
        System.out.println(generateToken("bean", "user"));
        System.out.println(generateToken("hancock", "user"));
        System.out.println(generateToken("donniebrasco", "admin"));
        System.out.println(generateToken("forestgump", "user"));

    }

    public static String generateToken(String username, String authority) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", authority);
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, "demo".getBytes())
                .compact();
        return "export HEADER='Authorization: Bearer " + token + "'";
    }

}
