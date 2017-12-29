package com.ifood.service.posserverbroker.provider.redis;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisProvider.class);
	
	@Resource(name = "redisTemplate")
	private ListOperations<String, String> listOps;
	
	@Value("${pos.state.ttl.timeInMinutes}")
	private long expireInMinutes;
	
	public void set(final String key, final String value) {
		listOps.getOperations().opsForValue().set(key, value);
		listOps.getOperations().expire(key, expireInMinutes, TimeUnit.MINUTES);
	}
	
	public String get(final String key) throws RedisConnectionFailureException {
		String value = null;
		try {
			final Object cachedValue = listOps.getOperations().opsForValue().get(key);
			if (cachedValue != null) {
				value  = cachedValue.toString();
			}
		} catch(Exception e){
			LOGGER.error("Fail to GET cache value", e);
		}
		return value;
	}

	public void delete(String key) {
		listOps.getOperations().opsForValue().getOperations().delete(key);
	}
	
}