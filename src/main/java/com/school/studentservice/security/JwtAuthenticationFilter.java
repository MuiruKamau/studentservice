package com.school.studentservice.security;



import com.school.studentservice.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import this
import io.jsonwebtoken.Claims; // Import Claims
import io.jsonwebtoken.Jwts; // Import Jwts

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println("username");
        System.out.println(username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extract roles from token claims


            Claims claims = Jwts.parser().setSigningKey(jwtUtil.getSecretKey()).parseClaimsJws(token).getBody(); // Use getSECRET_KEY()
            List<String> rolesFromToken = claims.get("roles", List.class);
            System.out.println("claims");
            System.out.println(claims);

            // Convert roles from token to GrantedAuthorities
            List<SimpleGrantedAuthority> authorities = rolesFromToken.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Re-add "ROLE_" prefix
                    .collect(Collectors.toList());


            UserDetails userDetails = new User(username, "", authorities); // Pass authorities here

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}

