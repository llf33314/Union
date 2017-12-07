package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionMainVO;

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
     * 根据id获取联盟对象
     *
     * @param unionId 联盟id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getById(Integer unionId) throws Exception;

    /**
     * 根据联盟id，获取联盟基础信息
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @return UnionMainVO
     * @throws Exception 统一处理异常
     */
    UnionMainVO getUnionMainVOByIdAndBusId(Integer unionId, Integer busId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取其他有效的联盟
     *
     * @param busId 商家id
     * @return List<UnionMainVO>
     * @throws Exception 统一处理异常
     */
    List<UnionMainVO> listOtherValidByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存新增联盟
     *
     * @param saveUnion 新增联盟
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
     * 更新联盟基础信息
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @param vo      更新内容
     * @throws Exception 统一处理异常
     */
    void updateUnionMainVOByIdAndBusId(Integer unionId, Integer busId, UnionMainVO vo) throws Exception;

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