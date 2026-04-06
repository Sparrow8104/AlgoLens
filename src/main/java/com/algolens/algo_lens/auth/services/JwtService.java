package com.algolens.algo_lens.auth.services;


import com.algolens.algo_lens.auth.exception.TokenRefreshException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secret;

        @Value("${jwt.access-token-expiration}")
        private long accessTokenExpiration;

         private  <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
             return claimResolver.apply( extractAllClaims(token));
         }

         private Claims extractAllClaims(String token) {
             try {
                 return Jwts
                         .parserBuilder()
                         .setSigningKey(getSignInKey())
                         .build()
                         .parseClaimsJws(token)
                         .getBody();
             } catch (ExpiredJwtException e) {
                 throw new TokenRefreshException("Jwt Token has expired");
             } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
                 throw new TokenRefreshException("Invalid jwt token: "+e.getMessage());
             }  catch (IllegalArgumentException e) {
                 throw new TokenRefreshException("JWT claims string is empty");
             }
         }

         private Key getSignInKey() {
             byte[] bytes= Decoders.BASE64.decode(secret);
             return Keys.hmacShaKeyFor(bytes);
         }

         public String generateToken(UserDetails userDetails) {
             return generateToken(new HashMap<>(),userDetails);
         }

         public String extractUsername(String token) {
             return extractClaims(token,Claims::getSubject);
         }

         private Date extractExpirationTime(String token) {
             return extractClaims(token, Claims::getExpiration);
         }
         public String generateToken(
                 Map<String, Object> claims,
                 UserDetails userDetails
         ){

             return Jwts.builder()
                     .setClaims(claims)
                     .setSubject(userDetails.getUsername())
                     .setIssuedAt(new Date(System.currentTimeMillis()))
                     .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                     .signWith(getSignInKey())
                     .compact();
         }

         public boolean isTokenExpired(String token) {
             return extractExpirationTime(token).before(new Date());
         }
         public boolean validateToken(String token, UserDetails userDetails) {
             return userDetails.getUsername().equals(extractUsername(token))&&!isTokenExpired(token);
         }
}
