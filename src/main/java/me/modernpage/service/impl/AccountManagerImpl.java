package me.modernpage.service.impl;

import com.google.common.collect.Sets;
import lombok.Data;
import me.modernpage.controller.UserResource;
import me.modernpage.entity.Account;
import me.modernpage.entity.Image;
import me.modernpage.entity.Profile;
import me.modernpage.repository.*;
import me.modernpage.response.AccountInfo;
import me.modernpage.response.PasswordRequest;
import me.modernpage.security.entity.RegisterRequest;
import me.modernpage.security.utils.JWTTokenUtils;
import me.modernpage.service.AccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.hateoas.Link;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@Service
@ConfigurationProperties(prefix = "upload.path.userimages")
public class AccountManagerImpl implements AccountManager {

	private AccountRepository accountRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private ImageRepository imageRepository;
	private FileSystemRepository fileSystemRepository;
	private String default_path;

	@Autowired
	public AccountManagerImpl(AccountRepository accountRepository,
							  UserRepository userRepository,
							  RoleRepository roleRepository,
							  ImageRepository imageRepository,
							  FileSystemRepository fileSystemRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.imageRepository = imageRepository;
		this.fileSystemRepository = fileSystemRepository;
	}

	@Transactional
	@Override
	public Optional<Account> findAccountByUsername(String username) {
		return accountRepository.findAccountByUsername(username);
	}

	@Transactional
	@Override
	public Optional<AccountInfo> getAccountInfoByProfileId(Long profileId) {
		AccountInfo accountInfo = null;
		Optional<Account> accountOptional = accountRepository.findAccountByProfileId(profileId);
		if(accountOptional.isPresent()) {
			Account account = accountOptional.get();
			accountInfo = new AccountInfo();
			accountInfo.setUsername(account.getUsername());
			accountInfo.setEmail(account.getEmail());
			accountInfo.setBirthdate(account.getProfile().getBirthdate());
			accountInfo.setFullname(account.getProfile().getFullname());
			Link link = linkTo(UserResource.class)
					.slash(account.getProfile().getId())
					.slash("images")
					.slash(account.getProfile().getImage().getId())
					.slash(account.getProfile().getImage().getName() + ".image")
					.withRel("avatar");
			accountInfo.setAvatarUrl(link.getHref());
		}
		return Optional.ofNullable(accountInfo);
	}

	@Transactional
	@Override
	public boolean saveAccountAsUser(RegisterRequest registerRequest) {
		Account account = new Account();
		account.setUsername(registerRequest.getUsername());
		account.setEmail(registerRequest.getEmail());
		account.setPassword(new BCryptPasswordEncoder(10)
				.encode(registerRequest.getPassword()));
		account.setActive(true);
		Profile profile = new Profile();
		profile.setFullname(registerRequest.getFullname());

		// set default image
		Image defaultImage = new Image();
		defaultImage.setLocation(default_path);
		defaultImage.setName("avatar_" + System.currentTimeMillis());
		Image image = imageRepository.save(defaultImage);
		profile.setImage(image);
		// save profile
		userRepository.save(profile);
		// set role and permissions
		roleRepository.findByRole("ROLE_USER").ifPresent(
				role ->	account.setRoles(Sets.newHashSet(role)));
		// save account
		account.setProfile(profile);
		accountRepository.save(account);

		return true;
	}

	@Transactional
	@Override
	public boolean existsByEmail(String email) {
		return accountRepository.existsByEmail(email);
	}

	@Transactional
	@Override
	public boolean resetPassword(PasswordRequest passwordRequest) {
		Optional<Account> account = accountRepository.findAccountByEmail(passwordRequest.getEmail());
		if(account.isPresent()) {
			Account a = account.get();
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
			final String password = a.getPassword();
			if(passwordEncoder.matches(passwordRequest.getPassword(), password)) {
				return false;
			}
			a.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
			return true;
		}
		return false;
	}

	@Transactional
	@Override
	public boolean checkPassword(PasswordRequest passwordRequest) {
		Optional<Account> accountOptional = accountRepository.findAccountByEmail(passwordRequest.getEmail());
		return accountOptional.filter(account -> new BCryptPasswordEncoder(10).matches(passwordRequest.getPassword(), account.getPassword())).isPresent();
	}


	@Transactional
	@Override
	public Optional<Profile> updateAccount(long profileId, AccountInfo accountInfo, String token) {
		Optional<Account> accountOptional = accountRepository.findAccountByProfileId(profileId);
		return accountOptional.map(account -> {
			account.setUsername(accountInfo.getUsername());
			if(accountInfo.getPassword() != null) {
				account.setPassword(new BCryptPasswordEncoder(10).encode(accountInfo.getPassword()));
				JWTTokenUtils.addBlackList(token);
			}
			account.getProfile().setFullname(accountInfo.getFullname());
			account.getProfile().setBirthdate(accountInfo.getBirthdate());
			return account.getProfile();
		});
	}

	@Transactional
	@Override
	public Optional<Profile> updateAccount(long profileId, AccountInfo accountInfo, MultipartFile file, String token)
			throws IOException {
		Optional<Profile> profileOptional = userRepository.findById(profileId);
		Image oldImage = null;
		if(profileOptional.isPresent()) {
			oldImage = profileOptional.get().getImage();
			String name = "avatar_" + System.currentTimeMillis();
			String filePath = fileSystemRepository.saveProfileImage(file.getBytes(), String.valueOf(profileId), name);
			fileSystemRepository.remove(oldImage.getLocation());
			return accountRepository.findAccountByProfileId(profileId).map(account -> {
				account.setUsername(accountInfo.getUsername());
				if(accountInfo.getPassword() != null) {
					account.setPassword(new BCryptPasswordEncoder(10).encode(accountInfo.getPassword()));
					JWTTokenUtils.addBlackList(token);
				}
				account.getProfile().setFullname(accountInfo.getFullname());
				account.getProfile().setBirthdate(accountInfo.getBirthdate());
				account.getProfile().getImage().setName(name);
				account.getProfile().getImage().setLocation(filePath);

				return account.getProfile();
			});
		}
		return Optional.empty();
	}
}
