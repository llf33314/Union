package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionVO;

import java.util.List;

/**
 * 联盟 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public interface IUnionMainService extends IService<UnionMain> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 商家获取联盟基础信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionVO
     * @throws Exception 统一处理异常
     */
    UnionVO getVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取联盟信息
     *
     * @param unionId 联盟id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getById(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取商家所有有效的联盟信息
     *
     * @param busId 商家id
     * @return ist<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyValidByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存联盟信息
     *
     * @param saveUnion 联盟信息
     * @throws Exception Exception 统一处理异常
     */
    void save(UnionMain saveUnion) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新联盟信息
     *
     * @param updateUnionMain 联盟信息
     * @throws Exception 统一处理异常
     */
    void update(UnionMain updateUnionMain) throws Exception;

    /**
     * 我的联盟-联盟设置-基础设置-保存
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      更新内容
     * @throws Exception 统一处理异常
     */
    void updateUnionVOByBusIdAndId(Integer busId, Integer unionId, UnionVO vo) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 获取联盟剩余可加盟数
     *
     * @param unionId 联盟id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countSurplusByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断联盟有效性
     *
     * @param unionId 联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(Integer unionId) throws Exception;

    /**
     * 判断联盟有效性
     *
     * @param union 联盟
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(UnionMain union) throws Exception;

}