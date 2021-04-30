package me.modernpage.security.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import me.modernpage.security.config.JwtConfig;
import me.modernpage.security.entity.AuthUserDetails;
import me.modernpage.security.service.UserService;
import me.modernpage.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * JWT generate Token tool
 */
@Slf4j
@Component
public class JWTTokenUtils {

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private UserService userService;
	@Autowired
	private SecretKey secretKey;

	private static JWTTokenUtils jwtTokenUtils;

	@PostConstruct
	public void init() {
		jwtTokenUtils = this;
		jwtTokenUtils.userService = this.userService;
		jwtTokenUtils.secretKey = this.secretKey;
	}

	/**
	 * Create a token
	 * 
	 * @param AuthUserDetails user info
	 * @return
	 */
	public static String createAccessToken(AuthUserDetails userDetails) {
		String token = Jwts.builder()
				.setId(String.valueOf(userDetails.getId()))
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setIssuer("ModernPage")
				.setExpiration(new Date(System.currentTimeMillis() + JwtConfig.expiration))
				.signWith(jwtTokenUtils.secretKey)
				.claim("authorities", JSON.toJSONString(userDetails.getAuthorities()))
				.claim("ip", userDetails.getIp()).compact();
		return JwtConfig.tokenPrefix + token;
	}

	/**
	 * Refresh Token
	 * 
	 * @param oldToken overdue but not expired token
	 * @return
	 */
	public static String refreshAccessToken(String oldToken) {
		String username = JWTTokenUtils.getUserNameByToken(oldToken);
		AuthUserDetails sysUserDetails = (AuthUserDetails) jwtTokenUtils.userService
				.loadUserByUsername(username);
		sysUserDetails.setIp(JWTTokenUtils.getIpByToken(oldToken));
		return createAccessToken(sysUserDetails);
	}

	/**
	 * Parsing Token
	 * 
	 * @param token Token info
	 * @return
	 */
	public static AuthUserDetails parseAccessToken(String token) {
		AuthUserDetails userDetails = null;
		if (StringUtils.isNotEmpty(token)) {
			try {
				token = token.substring(JwtConfig.tokenPrefix.length());

				Claims claims = Jwts.parserBuilder()
									.setSigningKey(jwtTokenUtils.secretKey)
									.build()
									.parseClaimsJws(token)
									.getBody();

				userDetails = new AuthUserDetails();
				userDetails.setId(Long.parseLong(claims.getId()));
				userDetails.setUsername(claims.getSubject());

				Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
				String authority = claims.get("authorities").toString();
				if (StringUtils.isNotEmpty(authority)) {
					List<Map<String, String>> authorityList = JSON.parseObject(authority,
							new TypeReference<List<Map<String, String>>>() {
							});
					for (Map<String, String> role : authorityList) {
						if (!role.isEmpty()) {
							authorities.add(new SimpleGrantedAuthority(role.get("authority")));
						}
					}
				}

				userDetails.setAuthorities(authorities);

				String ip = claims.get("ip").toString();
				userDetails.setIp(ip);
			} catch (Exception e) {
				log.error("parse token expiration：" + e);
			}
		}
		return userDetails;
	}

	/**
	 * Save Token into Redis
	 * 
	 * @param token    Token信息
	 * @param username
	 * @param ip       IP
	 */
	public static void setTokenInfo(String token, String username, String ip) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());

			Integer refreshTime = JwtConfig.refreshTime;
			log.info("setTokenInfo: " + refreshTime + ", " + JwtConfig.refreshTime + ", " + JwtConfig.tokenHeader + ", " + JwtConfig.antMatchers + "" +
					JwtConfig.expiration + ", " + JwtConfig.tokenPrefix + ", " + JwtConfig.secretKey);
			LocalDateTime localDateTime = LocalDateTime.now();
			RedisUtils.hset(token, "username", username, refreshTime);
			RedisUtils.hset(token, "ip", ip, refreshTime);
			RedisUtils.hset(token, "refreshTime",
					df.format(localDateTime.plus(JwtConfig.refreshTime, ChronoUnit.MILLIS)), refreshTime);
			RedisUtils.hset(token, "expiration", df.format(localDateTime.plus(JwtConfig.expiration, ChronoUnit.MILLIS)),
					refreshTime);
		}
	}

	/**
	 * Put token into blacklist
	 * 
	 * @param token
	 */
	public static void addBlackList(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			RedisUtils.hset("blackList", token, df.format(LocalDateTime.now()));
		}
	}

	/**
	 * Remove token on the redis cache
	 * 
	 * @param token
	 */
	public static void deleteRedisToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			RedisUtils.deleteKey(token);
		}
	}

	/**
	 * check if a token is on the blacklist
	 * 
	 * @param token
	 */
	public static boolean isBlackList(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hasKey("blackList", token);
		}
		return false;
	}

	/**
	 * is expired?
	 * 
	 * @param expiration
	 * @return returns True if expired, otherwise false
	 */
	public static boolean isExpiration(String expiration) {
		LocalDateTime expirationTime = LocalDateTime.parse(expiration, df);
		LocalDateTime localDateTime = LocalDateTime.now();
		if (localDateTime.compareTo(expirationTime) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * is a token valid?
	 * 
	 * @param refreshTime
	 * @return valid: True, invalid: false
	 */
	public static boolean isValid(String refreshTime) {
		LocalDateTime validTime = LocalDateTime.parse(refreshTime, df);
		LocalDateTime localDateTime = LocalDateTime.now();
		return localDateTime.compareTo(validTime) <= 0;
	}

	/**
	 * check a token if exists on the redis
	 * 
	 * @param token
	 * @return
	 */
	public static boolean hasToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hasKey(token);
		}
		return false;
	}

	/**
	 * get expiration from the redis
	 * 
	 * @param token
	 * @return
	 */
	public static String getExpirationByToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hget(token, "expiration").toString();
		}
		return null;
	}

	/**
	 * get refresh time from redis
	 * 
	 * @param token
	 * @return
	 */
	public static String getRefreshTimeByToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hget(token, "refreshTime").toString();
		}
		return null;
	}

	/**
	 * get username from redis
	 * 
	 * @param token
	 * @return
	 */
	public static String getUserNameByToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hget(token, "username").toString();
		}
		return null;
	}

	/**
	 * get ip from redis
	 * 
	 * @param token
	 * @return
	 */
	public static String getIpByToken(String token) {
		if (StringUtils.isNotEmpty(token)) {
			token = token.substring(JwtConfig.tokenPrefix.length());
			return RedisUtils.hget(token, "ip").toString();
		}
		return null;
	}

}
