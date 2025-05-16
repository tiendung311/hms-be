package com.example.hms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ClerkConfig {
    @Value("${clerk.secretKey}")
    private String secretKey;
}
