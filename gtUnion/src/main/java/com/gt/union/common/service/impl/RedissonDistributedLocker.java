package com.gt.union.common.service.impl;

import com.gt.union.common.service.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author hongjiye
 * @time 2017-12-06 18:03
 **/
public class RedissonDistributedLocker {

	private RedissonClient redissonClient;

	private RLock lock;

	public RedissonDistributedLocker(){

	}

	public RedissonDistributedLocker(String lockKey){
		lock = redissonClient.getLock(lockKey);
	}

	public void lock(String lockKey) {
		lock.lock();
	}

	public void unlock(String lockKey) {
		if(lock.isLocked()){
			lock.unlock();
		}
	}

	public void lock(String lockKey, int leaseTime) {
		lock.lock(leaseTime, TimeUnit.SECONDS);
	}

	public void lock(String lockKey, TimeUnit unit ,int timeout) {
		lock.lock(timeout, unit);
	}

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
		try{
			return lock.tryLock(waitTime, leaseTime, unit);
		}catch (Exception e){
			return false;
		}
	}

}
