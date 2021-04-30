package me.modernpage.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.security.utils.JWTTokenUtils;
import me.modernpage.utils.AccessAddressUtils;
import me.modernpage.utils.ResponseUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    public UserLoginSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess: called");
        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
        // get ip address
        String ip = AccessAddressUtils.getIpAddress(request);
        userDetails.setIp(ip);
        String token = JWTTokenUtils.createAccessToken(userDetails);

        // save token into redis
        JWTTokenUtils.setTokenInfo(token, userDetails.getUsername(), ip);

        log.info("User: {} login successfully, Token info saved into redis", userDetails.getUsername());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("token", token);
        dataMap.put("profile", userDetails.getProfile());

        ResponseUtils.responseJson(response, ResponseUtils.response(200, "login success", dataMap), objectMapper);
    }
}
