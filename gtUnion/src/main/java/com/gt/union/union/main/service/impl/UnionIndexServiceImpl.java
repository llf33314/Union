package com.gt.union.union.main.service.impl;

import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.service.IUnionIndexService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.service.IUnionMainTransferService;
import com.gt.union.union.main.vo.IndexVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 服务实现类
 *
 * @author linweicong
 * @version 2017-12-04 08:38:07
 */
@Service
public class UnionIndexServiceImpl implements IUnionIndexService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionCardService unionCardService;

    @Autowired
    private IUnionMainTransferService unionMainTransferService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Override
    public IndexVO getIndexVOByBusId(Integer busId, Integer optUnionId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        IndexVO result = new IndexVO();

        // （1）获取创建的联盟
        List<UnionMember> busReadMemberList = unionMemberService.listReadByBusId(busId);
        List<UnionMember> busCreateMemberList = unionMemberService.filterByIsUnionOwner(busReadMemberList, MemberConstant.IS_UNION_OWNER_YES);
        if (ListUtil.isNotEmpty(busCreateMemberList)) {
            UnionMember busCreateMember = busCreateMemberList.get(0);
            UnionMain busCreateUnion = unionMainService.getById(busCreateMember.getUnionId());
            result.setMyCreateUnion(busCreateUnion);
        }

        // （2）获取加入的联盟
        List<UnionMember> busJoinMemberList = unionMemberService.filterByIsUnionOwner(busReadMemberList, MemberConstant.IS_UNION_OWNER_NO);
        if (ListUtil.isNotEmpty(busJoinMemberList)) {
            List<UnionMain> busJoinUnionList = new ArrayList<>();
            for (UnionMember member : busJoinMemberList) {
                UnionMain busJoinUnion = unionMainService.getById(member.getUnionId());
                busJoinUnionList.add(busJoinUnion);
            }
            result.setMyJoinUnionList(busJoinUnionList);
        }

        // （3）如果存在unionId，则当前联盟为指定union；
        // 否则，如果存在创建的联盟，则当前联盟为创建的联盟；
        // 如果不存在创建的联盟，则当前联盟为第一个加入的联盟
        UnionMain currentUnion = null;
        if (optUnionId != null) {
            boolean isFound = false;
            if (result.getMyCreateUnion() != null && optUnionId.equals(result.getMyCreateUnion().getId())) {
                currentUnion = result.getMyCreateUnion();
                isFound = true;
            }
            if (!isFound && ListUtil.isNotEmpty(result.getMyJoinUnionList())) {
                for (UnionMain joinUnion : result.getMyJoinUnionList()) {
                    if (optUnionId.equals(joinUnion.getId())) {
                        currentUnion = joinUnion;
                        break;
                    }
                }
            }
        } else {
            if (result.getMyCreateUnion() != null) {
                currentUnion = result.getMyCreateUnion();
            } else if (ListUtil.isNotEmpty(result.getMyJoinUnionList())) {
                currentUnion = result.getMyJoinUnionList().get(0);
            }
        }

        // （4）如果存在当前联盟，则获取商家对应的盟员信息、联盟盟主、联盟积分、联盟成员数、联盟剩余盟员数，以及联盟转移申请信息
        if (currentUnion != null) {
            result.setCurrentUnion(currentUnion);
            Integer currentUnionId = currentUnion.getId();

            UnionMember currentMember = unionMemberService.getReadByBusIdAndUnionId(busId, currentUnionId);
            result.setCurrentMember(currentMember);

            result.setOwnerMember(unionMemberService.getOwnerByUnionId(currentUnionId));

            Integer readMemberCount = unionMemberService.countReadByUnionId(currentUnionId);
            result.setMemberCount(readMemberCount);
            Integer memberLimit = currentUnion.getMemberLimit();
            memberLimit = memberLimit != null ? memberLimit : 0;
            result.setMemberSurplus(memberLimit - readMemberCount);

            if (UnionConstant.IS_INTEGRAL_YES == currentUnion.getIsIntegral()) {
                result.setIntegral(unionCardIntegralService.countIntegralByUnionId(currentUnionId));
            }

            UnionMainTransfer transfer = unionMainTransferService.getByUnionIdAndToMemberIdAndConfirmStatus(currentUnionId, currentMember.getId(), UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
            result.setUnionTransfer(transfer);
        }

        return result;
    }
}