package me.modernpage.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.modernpage.security.config.JwtConfig;
import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.security.utils.JWTTokenUtils;
import me.modernpage.utils.AccessAddressUtils;
import me.modernpage.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthVerifierFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public AuthVerifierFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader(JwtConfig.tokenHeader);
        if(token != null && token.startsWith(JwtConfig.tokenPrefix)) {
            if(JWTTokenUtils.isBlackList(token)) {
                ResponseUtils.responseJson(response, ResponseUtils.response(
                        505, "Token expired and joined to black list", null), objectMapper);
                return;
            }
            // is on Redis
            if(JWTTokenUtils.hasToken(token)) {
                String ip = AccessAddressUtils.getIpAddress(request);
                String expiration = JWTTokenUtils.getExpirationByToken(token);
                String username = JWTTokenUtils.getUserNameByToken(token);

                if (JWTTokenUtils.isExpiration(expiration)) {
                    JWTTokenUtils.addBlackList(token);
                    String validTime = JWTTokenUtils.getRefreshTimeByToken(token);
                    if (JWTTokenUtils.isValid(validTime)) {
                        String newToke = JWTTokenUtils.refreshAccessToken(token);
                        JWTTokenUtils.deleteRedisToken(token);
                        JWTTokenUtils.setTokenInfo(newToke, username, ip);
                        response.setHeader(JwtConfig.tokenHeader, newToke);

                        log.info("User: {}'s Token was expired, but it is within valid refresh time, refreshed", username);
                        token = newToke;
                    } else {
                        log.info("User: {}'s Token was expired but refresh time is not valid, not refreshed", username);
                        JWTTokenUtils.addBlackList(token);
                        ResponseUtils.responseJson(response, ResponseUtils.response(
                                505, "Token expired and refresh time is not valid range", null), objectMapper);
                        return;
                    }
                }
                AuthUserDetails userDetails = JWTTokenUtils.parseAccessToken(token);

                if (userDetails != null) {
                    if (!StringUtils.equals(ip, userDetails.getIp())) {

                        log.info("User: {}'s request IP and IP on the Token is not consistent", username);

                        JWTTokenUtils.addBlackList(token);
                        ResponseUtils.responseJson(response, ResponseUtils.response(
                                505, "Token expired, there may be a risk of IP forgery",null), objectMapper);
                        return;
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getId(), userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
