package com.justteam.test_quest_api.jwt.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(value = "jwt", ignoreInvalidFields = true)
@Getter
@Setter
public class JwtConfigProperties {
    private Integer expiresIn;
    private Integer mobileExpiresIn;
    private String secretKey;
    private String header;
}
