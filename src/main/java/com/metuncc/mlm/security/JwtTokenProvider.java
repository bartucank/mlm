package com.metuncc.mlm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${res.security.secret}")
    private String SECRET;

    @Value("${res.security.exp}")
    private long EXP;

    public String generateJwtToken(Authentication auth){
        JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
        Date expDate= new Date(new Date().getTime() + EXP);
        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
    }

    Long getUserId(String jwt){
        Claims claims = Jwts
                .parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
    boolean validateToken(String jwt){
        try{
            Jwts
                    .parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String jwt){
        Date exp =Jwts
                .parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }
}
