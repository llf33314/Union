package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainCreate;
import com.gt.union.union.main.vo.UnionCreateVO;
import com.gt.union.union.main.vo.UnionPermitCheckVO;

/**
 * 联盟创建 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainCreateService extends IService<UnionMainCreate> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 我的联盟-创建联盟-检查联盟许可
     *
     * @param busId 商家id
     * @return UnionPermitCheckVO
     * @throws Exception 统一处理异常
     */
    UnionPermitCheckVO getPermitCheckVOByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 我的联盟-创建联盟-保存
     *
     * @param busId 商家id
     * @param vo    表单信息
     * @throws Exception 统一处理异常
     */
    void saveUnionCreateVOByBusId(Integer busId, UnionCreateVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}