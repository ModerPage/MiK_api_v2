package me.modernpage.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@SuppressWarnings("static-access")
public class JwtConfig {
    public static String tokenPrefix;
    public static String secretKey;
    public static Integer expiration;
    public static String antMatchers;
    public static Integer refreshTime;
    public static String tokenHeader;

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public void setExpiration(Integer expiration) {
    	this.expiration = expiration * 1000;
    }

    public void setRefreshTime(Integer refreshTime) {
        this.refreshTime = refreshTime * 24 * 60 * 60 * 1000;
    }


    public void setTokenPrefix(String tokenPrefix) {
    	this.tokenPrefix = tokenPrefix;
    }

    public void setSecretKey(String secretKey) {
    	this.secretKey = secretKey;
    }

    public void setAntMatchers(String antMatchers) {
    	this.antMatchers = antMatchers;
    }
}
