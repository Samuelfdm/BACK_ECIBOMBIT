package edu.eci.arsw.ecibombit.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import edu.eci.arsw.ecibombit.model.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final String SECRET_KEY = "clave-super-secreta-segura-12345678901234567890";

    public String generateToken(UserAccount user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("oid", user.getOid())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10h
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}