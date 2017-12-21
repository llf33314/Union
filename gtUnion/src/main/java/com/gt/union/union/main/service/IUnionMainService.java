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
     * 我的联盟-联盟设置-联盟基本信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionVO
     * @throws Exception 统一处理异常
     */
    UnionVO getUnionVOByBusIdAndId(Integer busId, Integer unionId) throws Exception;

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
     * 分页：我的联盟-加入联盟-选择联盟
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listOtherValidByBusId(Integer busId) throws Exception;

    /**
     * 获取我的有效的具有读权限的联盟列表
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyValidReadByBusId(Integer busId) throws Exception;

    /**
     * 获取我的有效的具有写权限的联盟列表
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyValidWriteByBusId(Integer busId) throws Exception;

    /**
     * 辅助接口：获取我的联盟列表
     *
     * @param busId 商家id
     * @return ist<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionVO> listUnionVOByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param saveUnion 保存内容
     * @throws Exception Exception 统一处理异常
     */
    void save(UnionMain saveUnion) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新
     *
     * @param updateUnionMain 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMain updateUnionMain) throws Exception;

    /**
     * 我的联盟-联盟设置-联盟基本信息-保存
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