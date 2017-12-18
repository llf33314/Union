package com.gt.union.finance.verifier.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.vo.VerifierVO;

import java.util.List;

/**
 * 平台管理者 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
public interface IUnionVerifierService extends IService<UnionVerifier> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 列表：获取平台管理者
     *
     * @param busId 商家id
     * @return VerifierVO
     * @throws Exception 统一处理异常
     */
    VerifierVO getVerifierVOByBusId(Integer busId) throws Exception;

    /**
     * 获取平台人员信息
     *
     * @param busId      商家id
     * @param verifierId 平台人员id
     * @return UnionVerifier
     * @throws Exception 统一处理异常
     */
    UnionVerifier getByBusIdAndId(Integer busId, Integer verifierId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取平台管理者列表信息
     *
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> list() throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 新增平台管理者
     *
     * @param busId    商家id
     * @param code     手机验证码
     * @param verifier 表单内容
     * @throws Exception 统一处理异常
     */
    void saveByBusId(Integer busId, String code, UnionVerifier verifier) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 删除平台管理者
     *
     * @param busId      商家id
     * @param verifierId 平台管理人员id
     * @throws Exception 统一处理异常
     */
    void removeByBusIdAndId(Integer busId, Integer verifierId) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新
     *
     * @param updateUnionVerifier 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionVerifier updateUnionVerifier) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断是否存在平台人员信息
     *
     * @param busId 商家id
     * @param phone 电话
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existByBusIdAndPhone(Integer busId, String phone) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据手机号进行过滤
     *
     * @param verifierList 数据源
     * @param phone        手机号
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> filterByPhone(List<UnionVerifier> verifierList, String phone) throws Exception;
}