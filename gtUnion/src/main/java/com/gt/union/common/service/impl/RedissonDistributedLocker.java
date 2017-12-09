package com.gt.union.common.service.impl;

import com.gt.union.common.service.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author hongjiye
 * @time 2017-12-06 18:03
 **/
public class RedissonDistributedLocker implements DistributedLocker {

	private RedissonClient redissonClient;

	@Override
	public void lock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
	}

	@Override
	public void unlock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.unlock();
	}

	@Override
	public void lock(String lockKey, int leaseTime) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(leaseTime, TimeUnit.SECONDS);
	}

	@Override
	public void lock(String lockKey, TimeUnit unit ,int timeout) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(timeout, unit);
	}

	@Override
	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	@Override
	public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
		RLock lock = redissonClient.getLock(lockKey);
		try{
			return lock.tryLock(waitTime, leaseTime, unit);
		}catch (Exception e){
			return false;
		}
	}
}
