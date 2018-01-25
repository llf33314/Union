package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.vo.*;
import com.gt.union.union.main.vo.UnionPayVO;

import java.util.Date;
import java.util.List;

/**
 * 联盟卡 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 前台-办理联盟卡-联盟卡列表
     *
     * @param busId 商家id
     * @return CardApplyVO
     * @throws Exception 统一处理异常
     */
    CardApplyVO getCardApplyVOByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的未过期的联盟卡信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    /**
     * 获取未删除的未过期的活动卡信息
     *
     * @param unionId    联盟id
     * @param fanId      粉丝id
     * @param activityId 活动id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的联盟列表信息
     *
     * @param unionId      联盟id
     * @param memberIdList 盟员id列表
     * @param type         联盟卡类型
     * @param geCreateTime 大于等于创建时间
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndMemberIdListAndTypeAndGECreateTime(Integer unionId, List<Integer> memberIdList, Integer type, Date geCreateTime) throws Exception;

    /**
     * 获取未删除的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    /**
     * 获取未删除的活动卡列表信息
     *
     * @param unionId    联盟id
     * @param fanId      粉丝id
     * @param activityId 活动id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception;

    /**
     * 获取未删除的未过期的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidUnexpiredByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception;

    /**
     * 获取未删除的未过期的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    /**
     * 获取未删除的未过期的联盟卡列表信息
     *
     * @param fanId 粉丝id
     * @param type  联盟卡类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidUnexpiredByFanIdAndType(Integer fanId, Integer type) throws Exception;

    /**
     * 财务-数据统计-联盟折扣卡领卡统计
     *
     * @param busId          商家id
     * @param statisticsType 统计类型:1按天统计;2按月统计
     * @param beginTime      统计开始时间
     * @param endTime        统计结束时间
     * @return DiscountCardStatisticsVO
     * @throws Exception 统一处理异常
     */
    List<DiscountCardStatisticsVO> listDiscountCardStatisticsVOByBusId(Integer busId, Integer statisticsType, Date beginTime, Date endTime) throws Exception;

    /**
     * 财务-数据统计-联盟活动卡发售统计
     *
     * @param busId 商家id
     * @return ActivityCardStatisticsVO
     * @throws Exception 统一处理异常
     */
    List<ActivityCardStatisticsVO> listActivityCardStatisticsVOByBusId(Integer busId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 前台-办理联盟卡-确定
     *
     * @param busId                 商家id
     * @param applyPostVO           办理联盟卡对象
     * @param unionCardApplyService 支付回调策略接口
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO saveApplyByBusId(Integer busId, CardPhoneResponseVO applyPostVO, IUnionCardApplyService unionCardApplyService) throws Exception;

    /**
     * 前台-办理联盟卡-校验手机验证码
     *
     * @param vo 表单内容
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    CardPhoneResponseVO checkCardPhoneVO(CardPhoneVO vo) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    /**
     * 前台-办理联盟卡-确定-回调
     *
     * @param orderNo    订单号
     * @param socketKey  socket关键字
     * @param payType    支付类型
     * @param payOrderNo 支付订单号
     * @param isSuccess  是否成功
     * @return String 返回结果
     */
    String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess);

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未杉树的联盟卡数量
     *
     * @param unionId 联盟id
     * @param type    联盟卡类型
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidByUnionIdAndType(Integer unionId, Integer type) throws Exception;

    /**
     * 统计未删除的活动卡数量
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 是否存在未删除的未过期的联盟卡信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    /**
     * 是否存在未删除的未过期的联盟卡信息
     *
     * @param unionId    联盟id
     * @param fanId      粉丝id
     * @param activityId 活动id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardList 数据源
     * @param delStatus     删除状态
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByDelStatus(List<UnionCard> unionCardList, Integer delStatus) throws Exception;

    /**
     * 根据联盟卡类型进行过滤
     *
     * @param cardList 数据源
     * @param type     类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param cardList 数据源
     * @param unionId  联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByUnionId(List<UnionCard> cardList, Integer unionId) throws Exception;

    /**
     * 根据活动id进行过滤
     *
     * @param cardList   数据源
     * @param activityId 活动id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByActivityId(List<UnionCard> cardList, Integer activityId) throws Exception;

    /**
     * 过滤掉过期的联盟卡
     *
     * @param cardList 数据源
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterExpired(List<UnionCard> cardList) throws Exception;

    /**
     * 根据时间段进行过滤
     *
     * @param cardList     数据源
     * @param optBeginTime 开始时间
     * @param optEndTime   结束时间
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterBetweenTime(List<UnionCard> cardList, Date optBeginTime, Date optEndTime) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟卡信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟卡信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟卡信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCard> unionCardList) throws Exception;

    /**
     * 获取对象集对应的的联盟id集
     *
     * @param unionCardList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getUnionIdList(List<UnionCard> unionCardList) throws Exception;

    /**
     * 获取对象集对应的的活动id集
     *
     * @param unionCardList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getActivityIdList(List<UnionCard> unionCardList) throws Exception;

    /**
     * 获取联盟卡列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取联盟卡列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取联盟卡列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByFanId(Integer fanId) throws Exception;

    /**
     * 获取未删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByFanId(Integer fanId) throws Exception;

    /**
     * 获取已删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listInvalidByFanId(Integer fanId) throws Exception;

    /**
     * 获取联盟卡列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByActivityId(Integer activityId) throws Exception;

    /**
     * 获取未删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取已删除的联盟卡列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listInvalidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCard> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCard 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCard newUnionCard) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCard> newUnionCardList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionCard 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCard updateUnionCard) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCard> updateUnionCardList) throws Exception;

}