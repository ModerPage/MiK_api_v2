package me.modernpage.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 
 * @author CL
 *
 */
@Component
@Slf4j
public class RedisUtils {

	@Autowired
	private StringRedisTemplate redisTemplate;

	private static RedisUtils redisUtils;

	@PostConstruct
	public void init() {
		redisUtils = this;
		redisUtils.redisTemplate = this.redisTemplate;
	}


	public static Set<String> keys(String key) {
		return redisUtils.redisTemplate.keys(key);
	}

	public static Object get(String key) {
		return redisUtils.redisTemplate.opsForValue().get(key);
	}

	public static void set(String key, String value) {
		redisUtils.redisTemplate.opsForValue().set(key, value);
	}


	public static void set(String key, String value, Integer expire) {
		redisUtils.redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
	}


	public static void delete(String key) {
		redisUtils.redisTemplate.opsForValue().getOperations().delete(key);
	}


	public static void hset(String key, String hashKey, Object object) {
		redisUtils.redisTemplate.opsForHash().put(key, hashKey, object);
	}

	public static void hset(String key, String hashKey, Object object, Integer expire) {
		redisUtils.redisTemplate.opsForHash().put(key, hashKey, object);
		redisUtils.redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}


	public static void hset(String key, HashMap<String, Object> map) {
		redisUtils.redisTemplate.opsForHash().putAll(key, map);
	}


	public static void hsetAbsent(String key, String hashKey, Object object) {
		redisUtils.redisTemplate.opsForHash().putIfAbsent(key, hashKey, object);
	}


	public static Object hget(String key, String hashKey) {
		return redisUtils.redisTemplate.opsForHash().get(key, hashKey);
	}


	public static Object hget(String key) {
		return redisUtils.redisTemplate.opsForHash().entries(key);
	}


	public static void deleteKey(String key) {
		redisUtils.redisTemplate.opsForHash().getOperations().delete(key);
	}


	public static Boolean hasKey(String key) {
		return redisUtils.redisTemplate.opsForHash().getOperations().hasKey(key);
	}


	public static Boolean hasKey(String key, String hasKey) {
		return redisUtils.redisTemplate.opsForHash().hasKey(key, hasKey);
	}

}
