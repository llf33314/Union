package com.gt.union.card.sharing.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;

import java.util.Date;
import java.util.List;

/**
 * 联盟卡售卡分成记录 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardSharingRecordService extends IService<UnionCardSharingRecord> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据商家id和联盟id，获取商家的售卡佣金分成记录
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optCardNumber 卡号
     * @param optBeginTime  开始时间
     * @param optEndTime    结束时间
     * @return List<CardSharingRecordVO>
     * @throws Exception 统一处理异常
     */
    List<CardSharingRecordVO> listCardSharingRecordVOByBusIdAndUnionId(
            Integer busId, Integer unionId, String optCardNumber, Date optBeginTime, Date optEndTime) throws Exception;

    /**
     * 根据分成盟员id和联盟id，获取售卡佣金分成记录
     *
     * @param sharingMemberId 分成盟员id
     * @param unionId  联盟id
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listBySharingMemberIdAndUnionId(Integer sharingMemberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟id进行过滤
     *
     * @param recordList 数据源
     * @param unionId    联盟id
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> filterByUnionId(List<UnionCardSharingRecord> recordList, Integer unionId) throws Exception;
}