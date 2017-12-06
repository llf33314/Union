package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMemberJoin;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;

import java.util.List;

/**
 * 入盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberJoinService extends IService<UnionMemberJoin> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据入盟申请id和联盟id，获取入盟申请信息
     *
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getByIdAndUnionId(Integer joinId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取联盟入盟申请信息
     *
     * @param buId          商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @param optPhone      盟员手机号
     * @return List<MemberJoinVO>
     * @throws Exception 统一处理异常
     */
    List<MemberJoinVO> listMemberJoinVOByBusIdAndUnionId(Integer buId, Integer unionId, String optMemberName, String optPhone) throws Exception;

    /**
     * 获取联盟入盟申请信息
     *
     * @param unionId      联盟id
     * @param memberStatus 申请入盟的盟员状态
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listByUnionId(Integer unionId, Integer memberStatus) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存入盟申请或推荐入盟申请信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param type    入盟类型
     * @param vo      表单信息
     * @throws Exception 统一处理异常
     */
    void saveJoinCreateVOByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type, MemberJoinCreateVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 通过或不通过入盟申请
     *
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @param busId   商家id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateStatusByIdAndUnionIdAndBusId(Integer joinId, Integer unionId, Integer busId, Integer isPass) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}