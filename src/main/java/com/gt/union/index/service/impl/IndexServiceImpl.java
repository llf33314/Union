package com.gt.union.index.service.impl;

import com.gt.union.brokerage.service.IUnionIncomeExpenseService;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.index.service.IIndexService;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
@Service
public class IndexServiceImpl implements IIndexService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Autowired
    private IUnionIncomeExpenseService unionIncomeExpenseService;

    /**
     * 商家联盟首页-默认未选定盟员身份
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> index(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = getCreateAndJoinUnionInfo(busId);

        //当前盟员相关信息
        UnionMember defaultCurrentUnionMember = (UnionMember)result.get("defaultCurrentUnionMember");
        if (defaultCurrentUnionMember != null) {
            result = getCurrentMemberInfo(result, defaultCurrentUnionMember);
        }

        return result;
    }

    /**
     * 商家联盟首页-选定盟员身份
     *
     * @param memberId {not null} 盟员id
     * @param busId   {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> indexByMemberId(Integer memberId, Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = getCreateAndJoinUnionInfo(busId);

        //当前盟员相关信息
        UnionMember currentUnionMember = this.unionMemberService.selectById(memberId);
        if (currentUnionMember != null) {
            result = getCurrentMemberInfo(result, currentUnionMember);
        }

        return result;
    }

    /**
     * 根据商家id，获取商家创建及加入的联盟和盟员身份信息
     * @param busId
     * @return
     * @throws Exception
     */
    private Map<String, Object> getCreateAndJoinUnionInfo(Integer busId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        // (1)获取我(商家)的具有盟主身份的盟员信息列表，以及我创建的联盟信息
        List<UnionMember> unionOwnerList = this.unionMemberService.listUnionMemberByBusIdAndIsUnionOwnerAndStatus(busId
                , MemberConstant.IS_UNION_OWNER_YES, MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT);
        UnionMember unionOwner = ListUtil.isNotEmpty(unionOwnerList) ? unionOwnerList.get(0) : null;
        UnionMain myCreateUnion = null;
        if (unionOwner != null) {
            myCreateUnion = this.unionMainService.getUnionMainById(unionOwner.getUnionId());
        }
        if (myCreateUnion != null) {
            result.put("myCreateUnionMemberId", unionOwner.getId()); //我创建的联盟具有盟主权限的盟员身份id
            result.put("myCreateUnionId", myCreateUnion.getId()); //我创建的联盟id
            result.put("myCreateUnionName", myCreateUnion.getName()); //我创建的联盟名称
            result.put("myCreateUnionImg", myCreateUnion.getImg()); //我创建的联盟图标
            result.put("myCreateUnionValidity", myCreateUnion.getUnionValidity()); //我创建的联盟有效期
        }

        // (2)获取我(商家)的具有非盟主身份的盟员信息列表，以及我加入的联盟信息
        List<UnionMember> unionNoOwnerList = this.unionMemberService.listUnionMemberByBusIdAndIsUnionOwnerAndStatus(busId
                , MemberConstant.IS_UNION_OWNER_NO, MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT);
        List<Map<String, Object>> myJoinUnionList = null;
        if (ListUtil.isNotEmpty(unionNoOwnerList)) {
            for (UnionMember unionNoOwner : unionNoOwnerList) {
                UnionMain myJoinUnion = this.unionMainService.getUnionMainById(unionNoOwner.getUnionId());
                if (myJoinUnion != null) {
                    Map<String, Object> myJoinUnionMap = new HashMap<>();
                    myJoinUnionMap.put("myJoinUnionMemberId", unionNoOwner.getId()); //我加入的联盟的盟员身份id
                    myJoinUnionMap.put("myJoinUnionId", myJoinUnion.getId()); //我加入的联盟id
                    myJoinUnionMap.put("myJoinUnionName", myJoinUnion.getName()); //我加入的联盟名称
                    myJoinUnionMap.put("myJoinUnionImg", myJoinUnion.getImg()); //我加入的联盟图标
                    myJoinUnionMap.put("myJoinUnionValidtiy", myJoinUnion.getUnionValidity()); //我加入的联盟有效期
                    myJoinUnionList.add(myJoinUnionMap);
                }
            }
        }
        result.put("myJoinUnionList", myJoinUnionList);

        UnionMember defaultCurrentUnionMember = unionOwner;
        if (defaultCurrentUnionMember == null && ListUtil.isNotEmpty(unionNoOwnerList)) {
            defaultCurrentUnionMember =  unionNoOwnerList.get(0);
        }
        result.put("defaultCurrentUnionMember", defaultCurrentUnionMember); //当前盟员身份
        return result;
    }

    /**
     * 拼接选定盟员的首页信息
     * @param result {not null} 结果信息
     * @param currentUnionMember {not null} 当前盟员信息
     * @return
     */
    private Map<String, Object> getCurrentMemberInfo(Map<String, Object> result, UnionMember currentUnionMember) throws Exception{
        Integer currentBusId = currentUnionMember.getBusId();
        Integer currentUnionId = currentUnionMember.getUnionId();
        result.put("currentUnionId", currentUnionId); //当前联盟id

        UnionMain currentUnionMain = this.unionMainService.getUnionMainById(currentUnionId);
        UnionMember currentUnionOwner = null;
        if (currentUnionMember.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
            currentUnionOwner = currentUnionMember;
        } else {
            currentUnionOwner = this.unionMemberService.getUnionOwnerByUnionId(currentUnionId);
        }
        // (1)拼接当前联盟信息
        if (currentUnionMain != null) {
            result.put("currentUnionCreatetime", currentUnionMain.getCreatetime()); //当前联盟创建时间
            result.put("currentUnionName", currentUnionMain.getName()); //当前联盟名称
            result.put("currentUnionImg", currentUnionMain.getImg()); //当前联盟图标
            result.put("currentUnionIllustration", currentUnionMain.getIllustration()); //当前联盟说明
            Integer currentUnionLimitMemberCount = currentUnionMain.getLimitMember();
            result.put("currentUnionLimitMemberCount", currentUnionLimitMemberCount); //当前联盟盟员总数上限
            Integer currentUnionMemberCount = this.unionMemberService.countUnionMemberByUnionIdAndStatus(currentUnionId, MemberConstant.STATUS_APPLY_IN);
            result.put("currentUnionMemberCount", currentUnionMemberCount); //当前联盟已加入盟员数
            Integer currentUnionSurplusMemberCount = null;
            if (currentUnionLimitMemberCount != null && currentUnionMemberCount != null) {
                currentUnionSurplusMemberCount = currentUnionLimitMemberCount.intValue() - currentUnionMemberCount.intValue();
            }
            result.put("currentUnionSurplusMemberCount", currentUnionSurplusMemberCount); //当前联盟剩余可加盟数
            Integer currentUnionIsIntegral = currentUnionMain.getIsIntegral();
            result.put("currentUnionIsIntegral", currentUnionIsIntegral); //当前联盟是否启用积分
            if (currentUnionIsIntegral == MainConstant.IS_INTEGRAL_YES) {
                Double currentUnionIntegralSum = this.unionCardIntegralService.getCardIntegralProfitByUnionId(currentUnionId);
                result.put("currentUnionIntegralSum", currentUnionIntegralSum); //当前联盟总积分数
            }
            Double currentUnionMoneyProfit = this.unionIncomeExpenseService.getMoneyProfitByUnionIdAndBusId(currentUnionId, currentBusId);
            result.put("currentUnionMoneyProfit", currentUnionMoneyProfit); //商家联盟净收入，即可提现金额

            //TODO 盟主权限转移记录
        }
        // (2)拼接当前盟员信息
        result.put("currentUnionMemberId", currentUnionMember.getId()); //当前盟员id
        result.put("currentUnionMemberIsUnionOwner", currentUnionMember.getIsUnionOwner()); //当前盟员是否是盟主
        result.put("currentUnionMemberEnterpriseName", currentUnionMember.getEnterpriseName()); //盟员名称

        // (3)拼接当前联盟盟主信息
        if (currentUnionOwner != null) {
            result.put("currentUnionOwnerEnterpriseName", currentUnionOwner.getEnterpriseName()); //盟主名称
        }

        return result;
    }


}
