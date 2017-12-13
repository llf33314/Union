package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainDict;

import java.util.List;

/**
 * 联盟入盟申请必填信息 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainDictService extends IService<UnionMainDict> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取入盟必填列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取入盟必填字段列表信息
     *
     * @param unionId unionId 联盟id
     * @return List<String>
     * @throws Exception 统一处理异常
     */
    List<String> listItemKeyByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 批量保存
     *
     * @param saveDictList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainDict> saveDictList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 批量删除
     *
     * @param idList 删除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}