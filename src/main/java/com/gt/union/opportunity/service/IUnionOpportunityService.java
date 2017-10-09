package com.gt.union.opportunity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.vo.UnionOpportunityVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商机推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionOpportunityService extends IService<UnionOpportunity> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id和目标盟员身份id，获取所有商家与目标盟员之间的商机推荐支付往来列表记录
     *
     * @param busId        {not null} 商家id
     * @param tgtMemberId  {not null} 目标盟员身份id
     * @param userMemberId 可选项 商家的盟员身份id
     * @return
     * @throws Exception
     */
    Map<String, Object> getContactDetailByBusIdAndTgtMemberId(Integer busId, Integer tgtMemberId, Integer userMemberId) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取商机佣金统计数据
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    Map<String, Object> getStatisticsByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id，分页查询商家推荐的商机列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项，联盟id
     * @param isAccept    可选项，受理状态，当勾选多个时用英文字符的逗号拼接，如=1,2
     * @param clientName  可选项，顾客姓名，模糊查询
     * @param clientPhone 可选项，顾客电话，模糊查询
     * @return
     * @throws Exception
     */
    Page pageFromMeMapByBusId(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

    /**
     * 根据商家id，分页查询推荐给商家的商机列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项，联盟id
     * @param isAccept    可选项，受理状态，当勾选多个时用英文字符的逗号拼接，如=1,2
     * @param clientName  可选项，顾客姓名，模糊查询
     * @param clientPhone 可选项，顾客电话，模糊查询
     * @return
     * @throws Exception
     */
    Page pageToMeMapByBusId(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

    /**
     * 根据商家id，分页获取商家因商机推荐而得到的佣金收入列表信息
     *
     * @param page        {not null} 分页对象
     * @param busId       {not null} 商家id
     * @param unionId     可选项 联盟id
     * @param toMemberId  可选项 接受商机的盟员身份id
     * @param isClose     可选项 是否已结算，0为否，1为是
     * @param clientName  可选项 客户姓名
     * @param clientPhone 可选项 客户电话
     * @return
     * @throws Exception
     */
    Page pageIncomeByBusId(Page page, Integer busId, Integer unionId, Integer toMemberId, Integer isClose
            , String clientName, String clientPhone) throws Exception;

    /**
     * 根据商家id，分页获取商家因接受商机推荐而支付的佣金列表信息
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param unionId      可选项 联盟id
     * @param fromMemberId 可选项 推荐商机的盟员身份id
     * @param isClose      可选项 是否已结算，0为否，1为是
     * @param clientName   可选项 客户姓名
     * @param clientPhone  可选项 客户电话
     * @return
     * @throws Exception
     */
    Page pageExpenseByBusId(Page page, Integer busId, Integer unionId, Integer fromMemberId, Integer isClose
            , String clientName, String clientPhone) throws Exception;

    /**
     * 根据商家id，分页获取商机佣金支付往来列表信息
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param userMemberId 可选项 商家盟员身份id
     * @return
     * @throws Exception
     */
    Page pageContactByBusId(Page page, Integer busId, Integer userMemberId) throws Exception;

    /**
     * 根据商家id，获取所有商机佣金支付往来列表信息
     *
     * @param busId        {not null} 商家id
     * @param userMemberId 可选项 商家盟员身份id
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listContactByBusId(Integer busId, Integer userMemberId) throws Exception;

    /**
     * 根据推荐方的盟员身份id，获取所有已推荐的，且已支付或为支付的商机推荐列表信息
     *
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @param isPaid       {not null} 是否已支付
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listByFromMemberIdAndIsPaid(Integer fromMemberId, Integer isPaid) throws Exception;

    /**
     * 根据商家id和推荐方的盟员身份id，获取所有推荐给商家的，已接受状态的，且已支付或为支付的商机推荐列表信息
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @param isPaid       {not null} 是否已支付
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listByBusIdAndFromMemberIdAndIsPaid(Integer busId, Integer fromMemberId, Integer isPaid) throws Exception;

    /**
     * 根据接收方的盟员身份id，获取所有已接受，且已支付或未支付的商机推荐列表信息
     *
     * @param toMemberId {not null} 接收方的盟员身份id
     * @param isPaid     {not null} 是否已支付
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listByToMemberIdAndIsPaid(Integer toMemberId, Integer isPaid) throws Exception;

    /**
     * 根据商家id和接收方的盟员身份id，获取所有商家推荐的，已接受状态，且已支付或支付的商机推荐列表信息
     *
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接收方的盟员身份id
     * @param isPaid     {not null} 是否已支付
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listByBusIdAndToMemberIdAndIsPaid(Integer busId, Integer toMemberId, Integer isPaid) throws Exception;

    /**
     * 根据推荐方的盟员身份id和接收方的盟员身份id，获取所有已接受的，且已支付或未支付的商机推荐列表信息
     *
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @param toMemberId   {not null} 接收方的盟员身份id
     * @param isPaid       {not null} 是否已支付
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listByFromMemberIdAndToMemberIdAndIsPaid(Integer fromMemberId, Integer toMemberId, Integer isPaid) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id和盟员身份id，保存商机推荐信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param vo
     * @throws Exception
     */
    void saveByBusIdAndMemberId(Integer busId, Integer memberId, UnionOpportunityVO vo) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 接受商机：根据商机推荐id、商家id和受理金额，更新商机推荐信息
     *
     * @param opportunityId {not null} 商机推荐id
     * @param busId         {not null} 商家id
     * @param acceptPrice   {not null} 受理金额
     * @throws Exception
     */
    void updateAcceptYesByIdAndBusId(Integer opportunityId, Integer busId, Double acceptPrice) throws Exception;

    /**
     * 拒绝商机：根据商机推荐id和商家id，更新商机推荐信息
     *
     * @param opportunityId
     * @param busId
     * @throws Exception
     */
    void updateAcceptNoByIdAndBusId(Integer opportunityId, Integer busId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id和接收方盟员身份id，统计商家推荐的、已接受状态的、且已支付或未支付的商机佣金总和
     *
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接收方盟员身份id
     * @param isPaid     {not null} 是否已支付
     * @return
     * @throws Exception
     */
    Double sumBrokeragePriceByBusIdAndToMemberIdAndIsPaid(Integer busId, Integer toMemberId, Integer isPaid) throws Exception;

    /**
     * 根据商家id和推荐方盟员身份id，统计商家接收的、已接受状态的、且已支付或未支付的商机佣金总和
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @param isPaid       {not null} 是否已支付
     * @return
     * @throws Exception
     */
    Double sumBrokeragePriceByBusIdAndFromMemberIdAndIsPaid(Integer busId, Integer fromMemberId, Integer isPaid) throws Exception;

    /**
     * 根据推荐方盟员身份id和接收方盟员身份id，统计推荐方推荐的、已接受状态的、且已支付的商机佣金总和
     *
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @param toMemberId   {not null} 接收方盟员身份id
     * @param isPaid       {not null} 是否已支付
     * @return
     * @throws Exception
     */
    Double sumBrokeragePriceByFromMemberIdAndToMemberIdAndIsPaid(Integer fromMemberId, Integer toMemberId, Integer isPaid) throws Exception;

    /**
     * 根据商机推荐方的盟员身份id，统计已被接受的、已支付或未支付的佣金之和，即佣金收入
     *
     * @param fromMemberId {not null} 商机推荐方的盟员身份id
     * @param isPaid       {not null} 是否已支付，1为是，0为否
     * @return
     * @throws Exception
     */
    Double sumBrokeragePriceByFromMemberIdAndIsPaid(Integer fromMemberId, Integer isPaid) throws Exception;

    /**
     * 根据商机接受方的盟员身份id，统计已接受的、已支付或未支付的佣金之和，即佣金支出
     *
     * @param toMemberId {not null} 商机接受方的盟员身份id
     * @param isPaid     {not null} 是否已支付，1为是，0为否
     * @return
     * @throws Exception
     */
    Double sumBrokeragePriceByToMemberIdAndIsPaid(Integer toMemberId, Integer isPaid) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - other *******************************************
     ******************************************************************************************************************/

    /**
     * 二维码支付佣金  生成支付信息
     *
     * @param busId
     * @param ids
     * @return
     */
    Map<String, Object> payOpportunityQRCode(Integer busId, String ids) throws Exception;

    /**
     * 佣金扫码支付成功后回调
     *
     * @param orderNo
     * @param only
     */
    void payOpportunitySuccess(String orderNo, String only) throws Exception;

    /**
     * 支付成功后批量插入
     *
     * @param list
     * @param orderNo
     */
    void insertBatchByList(List<UnionOpportunity> list, String orderNo, Integer verifierId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionOpportunity getById(Integer opportunityId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception;

    List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionOpportunity newOpportunity) throws Exception;

    void saveBatch(List<UnionOpportunity> newOpportunityList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer opportunityId) throws Exception;

    void removeBatchById(List<Integer> opportunityIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionOpportunity updateOpportunity) throws Exception;

    void updateBatch(List<UnionOpportunity> updateOpportunityList) throws Exception;
}
