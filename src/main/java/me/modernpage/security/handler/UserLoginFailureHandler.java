package me.modernpage.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.modernpage.utils.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserLoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    public UserLoginFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
        ResponseUtils.responseJson(response, ResponseUtils.response(
                500, "login failed: " + e.getMessage(), null), objectMapper);
    }
}
