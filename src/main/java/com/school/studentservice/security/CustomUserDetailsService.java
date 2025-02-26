package com.school.studentservice.security;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Query user details directly from the database
        String queryUser = "SELECT username, password FROM users WHERE username = ?";
        List<UserDetails> users = jdbcTemplate.query(queryUser, new Object[]{username}, (rs, rowNum) ->
                new org.springframework.security.core.userdetails.User(
                        rs.getString("username"),
                        rs.getString("password"),
                        getAuthorities(username) // Fetch roles separately
                )
        );

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return users.get(0);
    }

    private List<SimpleGrantedAuthority> getAuthorities(String username) {
        String queryRoles = "SELECT role FROM user_roles WHERE username = ?";
        return jdbcTemplate.query(queryRoles, new Object[]{username}, (rs, rowNum) ->
                new SimpleGrantedAuthority("ROLE_" + rs.getString("role"))
        );
    }
}


