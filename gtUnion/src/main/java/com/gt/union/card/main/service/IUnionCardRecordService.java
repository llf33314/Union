package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCardRecord;

import java.util.List;

/**
 * 联盟卡购买记录 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardRecordService extends IService<UnionCardRecord> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取联盟卡购买记录信息
     *
     * @param id 购买记录id
     * @return UnionCardRecord
     * @throws Exception 统一处理异常
     */
    UnionCardRecord getById(Integer id) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 批量保存
     *
     * @param newUnionCardApplyList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardRecord> newUnionCardApplyList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 批量更新
     *
     * @param updateUnionCardApplyList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardRecord> updateUnionCardApplyList) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}