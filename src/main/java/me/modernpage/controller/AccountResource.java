package me.modernpage.controller;

import lombok.extern.slf4j.Slf4j;
import me.modernpage.entity.Profile;
import me.modernpage.response.AccountInfo;
import me.modernpage.response.PasswordRequest;
import me.modernpage.security.config.JwtConfig;
import me.modernpage.security.entity.RegisterRequest;
import me.modernpage.service.AccountManager;
import me.modernpage.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@Slf4j
public class AccountResource {
	@Autowired
	private AccountManager accountManager;

	@GetMapping("/register/verify")
    public ResponseUtils verifyAccount(@RequestParam("code") String code) {
	    boolean isVerified = accountManager.verify(code);
	    return isVerified ? ResponseUtils.response(200, "Your account is verified successfully", true) :
                ResponseUtils.response(500, "Your account failed being verified", false);
    }
	
	@PostMapping("/register")
	public ResponseUtils saveAccount(@RequestBody RegisterRequest registerRequest, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
		boolean isSaved = accountManager.saveAccountAsUser(registerRequest, getUrl(request));
		return isSaved ? ResponseUtils.response(200, "Account registered successfully", true) :
				ResponseUtils.response(500, "Registering account failed", false);
	}

	@GetMapping("/register/validate/username")
	public ResponseUtils validateUsername(@RequestParam(value = "username") String username) {
		boolean isValid = !accountManager.checkUsername(username);
		return isValid ? ResponseUtils.response(200, "Username can be used", true) :
				ResponseUtils.response(500, "Username is already used", false);
	}

	@GetMapping("/register/validate/email")
	public ResponseUtils validateEmail(@RequestParam(value = "email") String email) {
		boolean isValid = !accountManager.existsByEmail(email);
		return isValid ? ResponseUtils.response(200, "Email can be used", true) :
				ResponseUtils.response(500, "Email is already used", false);
	}

	@GetMapping("/forgetpassword/email")
	public ResponseUtils emailCheck(@RequestParam String email) {
		boolean isFound = accountManager.existsByEmail(email);
		return isFound ? ResponseUtils.response(200, "Email verification success", true):
				ResponseUtils.response(500, "Email verification failed", false);
	}

	@PutMapping("/forgetpassword/reset")
	public ResponseUtils resetPassword(@RequestBody PasswordRequest passwordRequest) {
		boolean isReset = accountManager.resetPassword(passwordRequest);
		return isReset ? ResponseUtils.response(200, "Password changed successfully", true) :
				ResponseUtils.response(500, "Change password failed", false);
	}

	@GetMapping("/settings/{profileId:[0-9]+}")
	public ResponseUtils getAccountInfo(@PathVariable Long profileId) {
		Optional<AccountInfo> accountInfoOptional = accountManager.getAccountInfoByProfileId(profileId);
		return accountInfoOptional.map(ResponseUtils::success)
				.orElseGet(() -> ResponseUtils.response(500,String.format("Account info with %d profile id not found", profileId), null));
	}

	@PostMapping("/settings/password")
	public ResponseUtils checkPassword(@RequestBody PasswordRequest passwordRequest) {
		boolean isValid = accountManager.checkPassword(passwordRequest);
		return isValid ? ResponseUtils.response(200, "Password is valid", true) :
				ResponseUtils.response(500, "Password is not valid", false);
	}

	@PutMapping("/settings/{profileId:[0-9]+}")
	public ResponseUtils updateAccount(@RequestHeader(value = "Authorization") String token,
									   HttpServletResponse response,
									   @PathVariable long profileId, @RequestBody AccountInfo accountInfo) {
		if(response.getHeader(JwtConfig.tokenHeader) != null) {
			token = response.getHeader(JwtConfig.tokenHeader);
		}
		Optional<Profile> profileOptional = accountManager.updateAccount(profileId, accountInfo, token);
		return profileOptional.map(ResponseUtils::success)
				.orElseGet(() -> ResponseUtils.response(500, String.format("Profile with %d id did not updated", profileId), null));
	}

	@PostMapping(value = "/settings/{profileId:[0-9]+}", consumes = "multipart/form-data")
	public ResponseUtils updateAccount(@RequestHeader(value = "Authorization") String token,
									   HttpServletResponse response,
									   @PathVariable long profileId,
									   @RequestPart AccountInfo accountInfo,
									   @RequestParam MultipartFile file) throws IOException {
		if(response.getHeader(JwtConfig.tokenHeader) != null) {
			token = response.getHeader(JwtConfig.tokenHeader);
		}
		Optional<Profile> profileOptional = accountManager.updateAccount(profileId, accountInfo, file, token);
		return profileOptional.map(ResponseUtils::success)
				.orElseGet(() -> ResponseUtils.response(500, String.format("Profile with %d id did not updated", profileId), null));
	}

    private String getUrl(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
