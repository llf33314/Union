package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainDict;

import java.util.List;

/**
 * <p>
 * 联盟设置申请填写信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainDictService extends IService<UnionMainDict> {
    //-------------------------------------------------- get ----------------------------------------------------------
    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据联盟id，获取联盟申请填写信息设置
     *
     * @param unionId {not null} 联盟id
     * @return
     */
    List<UnionMainDict> listByUnionId(Integer unionId) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据联盟id删除联盟申请填写信息设置
     *
     * @param unionId {not null} 联盟id
     * @throws Exception
     */
    void removeByUnionId(Integer unionId) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

}
