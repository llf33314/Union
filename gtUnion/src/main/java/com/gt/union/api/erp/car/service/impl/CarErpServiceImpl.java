package com.gt.union.api.erp.car.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.vo.ErpServerVO;
import com.gt.union.api.erp.car.service.CarErpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-19 10:00
 **/
@Service
public class CarErpServiceImpl implements CarErpService {

	private Logger logger = LoggerFactory.getLogger(CarErpServiceImpl.class);

	@Override
	public List<ErpServerVO> listErpServer(Integer shopId, String search, Integer busId, Page page) {
		logger.info("查询服务项目列表门店id：{}，商家id：{}，search：{}", shopId, busId, search);
		//TODO  查询车小算服务项目
		return null;
	}
}
