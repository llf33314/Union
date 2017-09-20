package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.vo.UnionMainCreateVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 创建联盟 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainCreateService extends IService<UnionMainCreate> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商家id创建联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Map<String, Object> instanceByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id和服务许可id，获取联盟创建信息
     *
     * @param busId    {not null} 商家id
     * @param permitId {not null} 联盟服务许可
     * @return
     * @throws Exception
     */
    UnionMainCreate getByBusIdAndPermitId(Integer busId, Integer permitId) throws Exception;

    /**
     * 通过盟主服务许可id，获取联盟创建信息
     *
     * @param permitId {not null} 盟主服务许可id
     * @return
     * @throws Exception
     */
    UnionMainCreate getByPermitId(Integer permitId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 获取所有过期的联盟创建列表记录，过期是指因盟主服务许可过期而导致的过期
     *
     * @return
     * @throws Exception
     */
    List<UnionMainCreate> listExpired() throws Exception;

    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id和表单信息，保存新建的联盟信息
     *
     * @param busId             {not null} 商家id
     * @param unionMainCreateVO {not null} 新建的联盟id
     * @throws Exception
     */
    void saveInstanceByBusIdAndVo(Integer busId, UnionMainCreateVO unionMainCreateVO) throws Exception;

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据商家id判断是否创建过联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean hasCreateUnionMain(Integer busId) throws Exception;
}
