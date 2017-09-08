package com.gt.union.index.service.impl;

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

    @Override
    public Map<String, Object> index(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = new HashMap<>();
        // (1)获取我(商家)的具有盟主身份的盟员信息列表，以及我创建的联盟信息
        List<UnionMember> unionOwnerList = this.unionMemberService.listUnionMemberByBusIdAndIsUnionOwnerAndStatus(busId
            , MemberConstant.IS_UNION_OWNER_YES, MemberConstant.STATUS_APPLY_IN);
        UnionMember unionOwner = ListUtil.isNotEmpty(unionOwnerList) ? unionOwnerList.get(0) : null;
        UnionMain myCreateUnion = null;
        if (unionOwner != null) {
            myCreateUnion = this.unionMainService.getUnionMainById(unionOwner.getUnionId());
        }
        result.put("myCreateUnion", myCreateUnion); //我创建的联盟

        // (2)获取我(商家)的具有非盟主身份的盟员信息列表，以及我加入的联盟信息
        List<UnionMember> unionNoOwnerList = this.unionMemberService.listUnionMemberByBusIdAndIsUnionOwnerAndStatus(busId
            , MemberConstant.IS_UNION_OWNER_NO, MemberConstant.STATUS_APPLY_IN);
        List<UnionMain> myJoinUnionList = null;
        if (ListUtil.isNotEmpty(unionNoOwnerList)) {
            for (UnionMember unionNoOwner : unionNoOwnerList) {
                UnionMain myJoinUnion = this.unionMainService.getUnionMainById(unionNoOwner.getUnionId());
                myJoinUnionList.add(myJoinUnion);
            }
        }
        result.put("myJoinUnionList", myJoinUnionList); //我加入的联盟列表

        // (3)当前盟员、联盟，以及盟主信息
        UnionMember currentUnionMember = unionOwner;
        if (currentUnionMember == null && ListUtil.isNotEmpty(unionNoOwnerList)) {
            currentUnionMember =  unionNoOwnerList.get(0);
            result = appendIndexResult(result, currentUnionMember);
        }

        return result;
    }

    /**
     * 拼接首页信息
     * @param result
     * @param currentUnionMember
     * @return
     */
    private Map<String, Object> appendIndexResult(Map<String, Object> result, UnionMember currentUnionMember) throws Exception{
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
            Integer isIntegral = currentUnionMain.getIsIntegral();
            result.put("isIntegral", isIntegral);
            if (isIntegral == MainConstant.IS_INTEGRAL_YES) {
                //TODO
            }

        }

        return result;
    }


}
