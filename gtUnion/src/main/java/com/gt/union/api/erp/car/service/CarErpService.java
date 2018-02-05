package com.gt.union.api.erp.car.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.vo.ErpServerVO;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-19 10:00
 **/
public interface CarErpService {

    /**
     * 查询服务项目列表
     *
     * @param shopId 门店id
     * @param search 查询条件
     * @param busId  商家id
     * @param page   分页条件
     * @return
     */
    List<ErpServerVO> listErpServer(Integer shopId, String search, Integer busId, Page page);
}
