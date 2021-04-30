package me.modernpage.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.service.AccountManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import me.modernpage.entity.Account;
import me.modernpage.entity.Role;

@Service
public class UserService implements UserDetailsService {
	private AccountManager accountManager;
	
	@Autowired	
	public UserService(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> account = accountManager.findAccountByUsername(username);
		Account a = account.orElseThrow(() -> new UsernameNotFoundException(
				String.format("Account with %s username not found", username)));
		return buildAccountForAuthentication(a, getAuthority(a.getRoles()));
	}
	
	private List<GrantedAuthority> getAuthority(Set<Role> roles) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for(Role role: roles) {
			grantedAuthorities.addAll(role.getGrantedAuthorities());
		}
		return grantedAuthorities;
	}
	
	private UserDetails buildAccountForAuthentication(Account account, List<GrantedAuthority> authorities) {
		AuthUserDetails userDetails = new AuthUserDetails();
		BeanUtils.copyProperties(account, userDetails);
		userDetails.setAuthorities(authorities);
		return userDetails;
	}
}
