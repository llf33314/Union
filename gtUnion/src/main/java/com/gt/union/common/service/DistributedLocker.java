package com.gt.union.common.service;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public interface DistributedLocker {

	/**
	 * 可重入锁  加锁
	 * @param lockKey
	 */
	void lock(String lockKey);

	/**
	 * 解锁
	 * @param lockKey
	 */
	void unlock(String lockKey);

	/**
	 * 可重入锁  加锁 有时效  单位：秒
	 * @param lockKey
	 * @param timeout
	 */
	void lock(String lockKey, int timeout);

	/**
	 * 可重入锁  加锁 有时效
	 * @param lockKey
	 * @param unit
	 * @param timeout
	 */
	void lock(String lockKey, TimeUnit unit , int timeout);

	void setRedissonClient(RedissonClient redissonClient);

	/**
	 * 获取锁
	 * @param lockKey
	 * @param waitTime	等待获取锁的时间
	 * @param leaseTime	在没有手动释放锁的情况， 多长时间后自动释放锁
	 * @param unit		时间单位
	 * @return
	 */
	boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);
}
