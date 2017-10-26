package com.gt.union.main.service.impl;

import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.entity.UnionMainTransfer;
import com.gt.union.main.service.IIndexService;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.service.IUnionMainTransferService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页 服务实现类
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
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
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IUnionMainTransferService unionMainTransferService;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public Map<String, Object> index(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)获取我创建及加入的联盟信息，以及对应所拥有的盟员身份信息
        Map<String, Object> result = getCreateAndJoinUnionInfo(busId);

        //（2）当前盟员相关信息
        UnionMember defaultCurrentUnionMember = (UnionMember) result.get("defaultCurrentUnionMember");
        result.remove("defaultCurrentUnionMember");
        if (defaultCurrentUnionMember != null) {
            result = getCurrentMemberInfo(result, defaultCurrentUnionMember);
        }

        return result;
    }

    @Override
    public Map<String, Object> indexByMemberId(Integer memberId, Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)获取我创建及加入的联盟信息，以及对应所拥有的盟员身份信息
        Map<String, Object> result = getCreateAndJoinUnionInfo(busId);
        result.remove("defaultCurrentUnionMember");

        //(2)当前盟员相关信息
        UnionMember currentUnionMember = this.unionMemberService.selectById(memberId);
        if (currentUnionMember != null) {
            result = getCurrentMemberInfo(result, currentUnionMember);
        }

        return result;
    }

    private Map<String, Object> getCreateAndJoinUnionInfo(Integer busId) throws Exception {
        Map<String, Object> result = new HashMap<>(16);
        //(1)获取我(商家)的具有盟主身份的盟员信息，以及我创建的联盟信息
        UnionMember unionOwner = this.unionMemberService.getOwnerByBusId(busId);
        UnionMain myCreateUnion = null;
        if (unionOwner != null) {
            myCreateUnion = this.unionMainService.getById(unionOwner.getUnionId());
        }
        if (myCreateUnion != null) {
            //我创建的联盟具有盟主权限的盟员身份id
            result.put("myCreateUnionMemberId", unionOwner.getId());
            //我创建的联盟id
            result.put("myCreateUnionId", myCreateUnion.getId());
            //我创建的联盟名称
            result.put("myCreateUnionName", myCreateUnion.getName());
            //我创建的联盟图标
            result.put("myCreateUnionImg", myCreateUnion.getImg());
            //我创建的联盟有效期
            result.put("myCreateUnionValidity", myCreateUnion.getUnionValidity());
        }

        //(2)获取我(商家)的具有非盟主身份的盟员信息列表，以及我加入的联盟信息
        List<UnionMember> unionNotOwnerList = this.unionMemberService.listNotOwnerReadByBusId(busId);
        List<Map<String, Object>> myJoinUnionList = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionNotOwnerList)) {
            for (UnionMember unionNoOwner : unionNotOwnerList) {
                UnionMain myJoinUnion = this.unionMainService.getById(unionNoOwner.getUnionId());
                if (myJoinUnion != null) {
                    Map<String, Object> myJoinUnionMap = new HashMap<>(16);
                    //我加入的联盟的盟员身份id
                    myJoinUnionMap.put("myJoinUnionMemberId", unionNoOwner.getId());
                    //我加入的联盟id
                    myJoinUnionMap.put("myJoinUnionId", myJoinUnion.getId());
                    //我加入的联盟名称
                    myJoinUnionMap.put("myJoinUnionName", myJoinUnion.getName());
                    //我加入的联盟图标
                    myJoinUnionMap.put("myJoinUnionImg", myJoinUnion.getImg());
                    //我加入的联盟有效期
                    myJoinUnionMap.put("myJoinUnionValidity", myJoinUnion.getUnionValidity());
                    myJoinUnionList.add(myJoinUnionMap);
                }
            }
        }
        result.put("myJoinUnionList", myJoinUnionList);

        UnionMember defaultCurrentUnionMember = unionOwner;
        if (defaultCurrentUnionMember == null && ListUtil.isNotEmpty(unionNotOwnerList)) {
            defaultCurrentUnionMember = unionNotOwnerList.get(0);
        }
        //当前盟员身份
        result.put("defaultCurrentUnionMember", defaultCurrentUnionMember);
        return result;
    }

    private Map<String, Object> getCurrentMemberInfo(Map<String, Object> result, UnionMember currentUnionMember) throws Exception {
        Integer currentUnionId = currentUnionMember.getUnionId();
        //当前联盟id
        result.put("currentUnionId", currentUnionId);

        UnionMain currentUnionMain = this.unionMainService.getById(currentUnionId);
        UnionMember currentUnionOwner;
        if (currentUnionMember.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
            currentUnionOwner = currentUnionMember;
        } else {
            currentUnionOwner = this.unionMemberService.getOwnerByUnionId(currentUnionId);
        }
        // (1)拼接当前联盟信息
        if (currentUnionMain != null) {
            //当前联盟创建时间
            result.put("currentUnionCreatetime", currentUnionMain.getCreatetime());
            //当前联盟名称
            result.put("currentUnionName", currentUnionMain.getName());
            //当前联盟图标
            result.put("currentUnionImg", currentUnionMain.getImg());
            //当前联盟说明
            result.put("currentUnionIllustration", currentUnionMain.getIllustration());
            Integer currentUnionLimitMemberCount = currentUnionMain.getLimitMember();
            //当前联盟盟员总数上限
            result.put("currentUnionLimitMemberCount", currentUnionLimitMemberCount);
            Integer currentUnionMemberCount = this.unionMemberService.countReadByUnionId(currentUnionId);
            //当前联盟已加入盟员数
            result.put("currentUnionMemberCount", currentUnionMemberCount);
            Integer currentUnionSurplusMemberCount = null;
            if (currentUnionLimitMemberCount != null && currentUnionMemberCount != null) {
                currentUnionSurplusMemberCount = currentUnionLimitMemberCount - currentUnionMemberCount;
            }
            //当前联盟剩余可加盟数
            result.put("currentUnionSurplusMemberCount", currentUnionSurplusMemberCount);
            UnionMainCharge unionMainCharge = this.unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(
                    currentUnionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
            //当前联盟是否启用红卡
            result.put("isRedCardAvailable", unionMainCharge != null);

            Integer currentUnionIsIntegral = currentUnionMain.getIsIntegral();
            //当前联盟是否启用积分
            result.put("currentUnionIsIntegral", currentUnionIsIntegral);
            if (currentUnionIsIntegral == MainConstant.IS_INTEGRAL_YES) {
                Double currentUnionIntegralSum = this.unionCardIntegralService.getCardIntegralProfitByUnionId(currentUnionId);
                //当前联盟总积分数
                result.put("currentUnionIntegralSum", currentUnionIntegralSum);
            }
        }
        // (2)拼接当前盟员信息
        //当前盟员id
        result.put("currentUnionMemberId", currentUnionMember.getId());
        //当前盟员是否是盟主
        result.put("currentUnionMemberIsUnionOwner", currentUnionMember.getIsUnionOwner());
        //盟员名称
        result.put("currentUnionMemberEnterpriseName", currentUnionMember.getEnterpriseName());

        // (3)拼接当前联盟盟主信息
        if (currentUnionOwner != null) {
            //盟主名称
            result.put("currentUnionOwnerEnterpriseName", currentUnionOwner.getEnterpriseName());
        }
        // (4)拼接是否存在盟主权限转移邀请
        List<UnionMainTransfer> transferList = this.unionMainTransferService.listByToMemberId(currentUnionMember.getId());
        if (ListUtil.isNotEmpty(transferList)) {
            for (UnionMainTransfer transfer : transferList) {
                if (MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING == transfer.getConfirmStatus()) {
                    result.put("currentUnionMemberTransferId", transfer.getId());
                    break;
                }
            }
        } else {
            result.put("currentUnionMemberTransferId", null);
        }

        return result;
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

}
