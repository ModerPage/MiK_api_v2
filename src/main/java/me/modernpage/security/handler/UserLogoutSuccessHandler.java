package me.modernpage.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.modernpage.security.config.JwtConfig;
import me.modernpage.security.utils.JWTTokenUtils;
import me.modernpage.utils.ResponseUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
@Slf4j
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper objectMapper;

    public UserLogoutSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader(JwtConfig.tokenHeader);
        log.info("onLogoutSuccess: called: " + request + ", response: " + response + ", header: " + request.toString()+ "" +
                " token: " + token);
        JWTTokenUtils.addBlackList(token);
        log.info("User: {} logout successfully, Token info added to black list", JWTTokenUtils.getUserNameByToken(token));
        SecurityContextHolder.clearContext();
        ResponseUtils.responseJson(response, ResponseUtils.response(200, "logout success", null),
                objectMapper);
    }
}
