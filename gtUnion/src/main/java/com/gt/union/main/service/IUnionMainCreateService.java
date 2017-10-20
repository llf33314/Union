package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.vo.UnionMainCreateVO;

import java.util.List;
import java.util.Map;

/**
 * 创建联盟 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainCreateService extends IService<UnionMainCreate> {

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据商家id创建联盟
     *
     * @param busId {not null} 商家id
     * @return Map <String, Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> instanceByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id和服务许可id，获取联盟创建信息
     *
     * @param busId    {not null} 商家id
     * @param permitId {not null} 联盟服务许可
     * @return UnionMainCreate
     * @throws Exception 全局处理异常
     */
    UnionMainCreate getByBusIdAndPermitId(Integer busId, Integer permitId) throws Exception;

    /**
     * 通过盟主服务许可id，获取联盟创建信息
     *
     * @param permitId {not null} 盟主服务许可id
     * @return UnionMainCreate
     * @throws Exception 全局处理异常
     */
    UnionMainCreate getByPermitId(Integer permitId) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 获取所有过期的联盟创建列表记录，过期是指因盟主服务许可过期而导致的过期
     *
     * @return List<UnionMainCreate>
     * @throws Exception 全局处理异常
     */
    List<UnionMainCreate> listExpired() throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    /**
     * 根据商家id和表单信息，保存新建的联盟信息
     *
     * @param busId             {not null} 商家id
     * @param unionMainCreateVO {not null} 新建的联盟id
     * @throws Exception 全局处理异常
     */
    void saveInstanceByBusIdAndVo(Integer busId, UnionMainCreateVO unionMainCreateVO) throws Exception;

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    /**
     * 根据商家id判断是否创建过联盟
     *
     * @param busId {not null} 商家id
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean hasCreateUnionMain(Integer busId) throws Exception;

    //******************************************* Object As a Service - get ********************************************

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    //******************************************* Object As a Service - remove *****************************************

    //******************************************* Object As a Service - update *****************************************

    //******************************************* Object As a Service - count ******************************************

    //******************************************* Object As a Service - boolean ****************************************

}
