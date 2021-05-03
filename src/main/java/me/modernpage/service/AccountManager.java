package me.modernpage.service;

import me.modernpage.entity.Account;
import me.modernpage.entity.Profile;
import me.modernpage.response.AccountInfo;
import me.modernpage.response.PasswordRequest;
import me.modernpage.security.entity.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface AccountManager {
	Optional<Account> findAccountByUsername(String username);
	Optional<AccountInfo> getAccountInfoByProfileId(Long profileId);
	boolean saveAccountAsUser(RegisterRequest registerRequest, String url) throws UnsupportedEncodingException, MessagingException;
	boolean existsByEmail(String email);
	boolean resetPassword(PasswordRequest passwordRequest);
	boolean checkPassword(PasswordRequest passwordRequest);

	Optional<Profile> updateAccount(long profileId, AccountInfo accountInfo, String token);
	Optional<Profile> updateAccount(long profileId, AccountInfo accountInfo, MultipartFile file, String token) throws IOException;

    boolean checkUsername(String username);

	boolean verify(String code);
}
