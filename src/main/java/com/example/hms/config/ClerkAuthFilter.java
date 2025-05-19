package com.example.hms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

@Component
public class ClerkAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                DecodedJWT jwt = JWT.decode(token); // Có thể dùng thư viện Auth0 hoặc Clerk SDK nếu cần

                String email = jwt.getClaim("email").asString();
                String role = jwt.getClaim("role").asString(); // Clerk phải thêm custom claim "role" khi tạo token

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

//                System.out.println("JWT payload: " + jwt.getPayload());
//                jwt.getClaims().forEach((k, v) -> System.out.println(k + " -> " + v.asString()));

                var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
