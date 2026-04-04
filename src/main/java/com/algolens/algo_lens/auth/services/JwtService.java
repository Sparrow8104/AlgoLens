package com.algolens.algo_lens.auth.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private String secret;

         public <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
             Claims claims = extractAllClaims(token);
             return claimResolver.apply(claims);
         }

         public Claims extractAllClaims(String token) {
             return Jwts
                     .parserBuilder()
                     .setSigningKey(getSignInKey())
                     .build()
                     .parseClaimsJws(token)
                     .getBody();
         }

         public Key getSignInKey() {
             byte[] bytes= Decoders.BASE64.decode(secret);
             return Keys.hmacShaKeyFor(bytes);
         }

         public String generateToken(UserDetails userDetails) {
             return generateToken(new HashMap<>(),userDetails);
         }

         public String extractUsername(String token) {
             return extractAllClaims(token).getSubject();
         }

         public Date extractExpirationTime(String token) {
             return extractAllClaims(token).getExpiration();
         }
         public String generateToken(
                 Map<String, Object> claims,
                 UserDetails userDetails
         ){
             return Jwts.builder()
                     .setClaims(claims)
                     .setSubject(userDetails.getUsername())
                     .setIssuedAt(new Date(System.currentTimeMillis()))
                     .setExpiration(new Date(System.currentTimeMillis()+1000*60*15))
                     .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                     .compact();
         }

         public boolean isTokenExpired(String token) {
             return extractExpirationTime(token).before(new Date());
         }
         public boolean validateToken(String token, UserDetails userDetails) {
             return userDetails.getUsername().equals(extractUsername(token))&&!isTokenExpired(token);
         }
}
