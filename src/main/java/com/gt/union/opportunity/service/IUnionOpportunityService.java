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
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商机推荐id，获取商机推荐信息
     *
     * @param opportunityId {not null} 商家推荐id
     * @return
     * @throws Exception
     */
    UnionOpportunity getById(Integer opportunityId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

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
     * 根据推荐方的盟员身份id，获取所有已推荐的，且已支付的商机推荐列表信息
     *
     * @param fromMemberId {not null} 推荐方的盟员身份id
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listPaidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 根据接收方的盟员身份id，获取所有已接受，且已支付的商机推荐列表信息
     *
     * @param toMemberId {not null} 接收方的盟员身份id
     * @return
     * @throws Exception
     */
    List<UnionOpportunity> listPaidByToMemberId(Integer toMemberId) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

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

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id和盟员身份id，保存商机推荐信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param vo
     * @throws Exception
     */
    void saveByBusIdAndMemberId(Integer busId, Integer memberId, UnionOpportunityVO vo) throws Exception;

    //------------------------------------------------- count ---------------------------------------------------------

    /**
     * 根据商家id和接收方盟员身份id，统计商家推荐的、已接受状态的、且已支付的商机佣金总和
     *
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接收方盟员身份id
     * @return
     * @throws Exception
     */
    Double sumPaidBrokeragePriceByBusIdAndToMemberId(Integer busId, Integer toMemberId) throws Exception;

    /**
     * 根据商家id和推荐方盟员身份id，统计商家接收的、已接受状态的、且已支付的商机佣金总和
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @return
     * @throws Exception
     */
    Double sumPaidBrokeragePriceByBusIdAndFromMemberId(Integer busId, Integer fromMemberId) throws Exception;

    /**
     * 根据推荐方盟员身份id和接收方盟员身份id，统计推荐方推荐的、已接受状态的、且已支付的商机佣金总和
     *
     * @param fromMemberId {not null} 推荐方盟员身份id
     * @param toMemberId   {not null} 接收方盟员身份id
     * @return
     * @throws Exception
     */
    Double sumPaidBrokeragePriceByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

    //------------------------------------------------ boolean --------------------------------------------------------


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
     * @param encrypt
     * @param only
     */
    void payOpportunitySuccess(String encrypt, String only) throws Exception;


    /**
     * 查询在联盟下我与某盟员的佣金来往明细
     *
     * @param memberId   盟员id
     * @param myMemberId 我的盟员id
     * @return
     */
    List<Map<String, Object>> listPayDetailParticularByUnionIdAndMemberId(Integer memberId, Integer myMemberId) throws Exception;

    /**
     * 获取商机统计信息
     *
     * @param unionId
     * @param busId
     * @return
     */
    Map<String, Object> getStatisticData(Integer unionId, Integer busId) throws Exception;

}
