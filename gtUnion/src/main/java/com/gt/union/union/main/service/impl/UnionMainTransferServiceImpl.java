package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.client.user.bean.UserUnionAuthority;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.dao.IUnionMainTransferDao;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.service.IUnionMainPackageService;
import com.gt.union.union.main.service.IUnionMainPermitService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.service.IUnionMainTransferService;
import com.gt.union.union.main.util.UnionMainTransferCacheUtil;
import com.gt.union.union.main.vo.UnionTransferVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 联盟转移 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainTransferServiceImpl implements IUnionMainTransferService {
    @Autowired
    private IUnionMainTransferDao unionMainTransferDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

    @Autowired
    private IBusUserService busUserService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionMainTransfer getValidByUnionIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer toMemberId, Integer confirmStatus) throws Exception {
        if (unionId == null || toMemberId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = listValidByUnionIdAndConfirmStatus(unionId, confirmStatus);
        result = filterByToMemberId(result, toMemberId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMainTransfer getValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception {
        if (unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = listValidByUnionIdAndConfirmStatus(unionId, confirmStatus);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMainTransfer getValidByIdAndUnionIdAndConfirmStatus(Integer transferId, Integer unionId, Integer confirmStatus) throws Exception {
        if (transferId == null || unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMainTransfer result = getValidById(transferId);

        return result != null && unionId.equals(result.getUnionId()) && confirmStatus.equals(result.getConfirmStatus()) ? result : null;
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionMainTransfer> listValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception {
        if (unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = listValidByUnionId(unionId);
        result = filterByConfirmStatus(result, confirmStatus);

        return result;
    }

    @Override
    public List<UnionTransferVO> listUnionTransferVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	按有转移id排第一，其他按时间顺序排序
        List<UnionTransferVO> result = new ArrayList<>();
        List<UnionMember> writeMemberList = unionMemberService.listValidWriteByUnionId(unionId);
        if (ListUtil.isNotEmpty(writeMemberList)) {
            Integer memberId = member.getId();
            for (UnionMember writeMember : writeMemberList) {
                if (memberId.equals(writeMember.getId())) {
                    continue;
                }

                UnionTransferVO vo = new UnionTransferVO();
                vo.setMember(writeMember);

                UnionMainTransfer transfer = getValidByUnionIdAndToMemberIdAndConfirmStatus(unionId, writeMember.getId(), UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
                vo.setUnionTransfer(transfer);

                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<UnionTransferVO>() {
            @Override
            public int compare(UnionTransferVO o1, UnionTransferVO o2) {
                if (o1.getUnionTransfer() != null) {
                    return -1;
                }
                if (o2.getUnionTransfer() != null) {
                    return 1;
                }
                return o1.getMember().getCreateTime().compareTo(o2.getMember().getCreateTime());
            }
        });

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId) throws Exception {
        if (busId == null || unionId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	判断是否已存在其他转移申请:
        //   （2-1）如果是，则报错；
        //   （2-2）如果不是，则进行下一步；
        if (existValidByUnionIdAndConfirmStatus(unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS)) {
            throw new BusinessException("已存在联盟盟主权限转移申请，请撤消后再操作");
        }
        // （3）	判断toMemberId有效性和写权限；
        UnionMember toMember = unionMemberService.getValidWriteByIdAndUnionId(toMemberId, unionId);
        if (toMember == null) {
            throw new BusinessException("联盟盟主权限转移的目标盟员不存在，或处于退盟过渡期，或已退盟");
        }
        // （4）	判断toMember是否是另一个联盟的盟主：
        //   （4-1）如果是，则报错；
        //   （4-2）如果不是，则进行下一步；
        UnionMember toMemberOwner = unionMemberService.getValidOwnerByBusId(toMember.getBusId());
        if (toMemberOwner != null) {
            throw new BusinessException("联盟盟主权限转移的目标盟员已经是另一个联盟的盟主，无法同时成为多个联盟的盟主");
        }
        // （5）	判断toMember是否具有联盟基础服务（调接口）
        //   （5-1）如果不是，则报错；
        //   （5-2）如果是，则进行下一步；
        UserUnionAuthority authority = busUserService.getUserUnionAuthority(busId);
        if (authority == null || !authority.getAuthority()) {
            throw new BusinessException("联盟盟主权限转移的目标盟员不具有联盟基础服务");
        }
        // （6）	判断toMember是否具有联盟许可:
        //   （6-1）如果不是，则判断是否需要付费：
        //     （6-1-1）如果不是，则新增免费的联盟许可；
        //     （6-1-2）如果是，则报错；
        //   （6-2）如果是，则进行下一步；
        UnionMainPermit permit = unionMainPermitService.getValidByBusId(toMember.getBusId());
        if (permit == null) {
            if (!authority.getPay()) {
                BusUser toMemberBusUser = busUserService.getBusUserById(toMember.getBusId());
                if (toMemberBusUser == null) {
                    throw new BusinessException("找不到联盟盟主权限转移目标盟员的商家信息");
                }
                UnionMainPackage unionPackage = unionMainPackageService.getValidByLevel(toMemberBusUser.getLevel());
                if (unionPackage == null) {
                    throw new BusinessException("找不到商家等级为" + toMemberBusUser.getLevel() + "的联盟套餐");
                }
                permit = new UnionMainPermit();
                permit.setDelStatus(CommonConstant.COMMON_NO);
                permit.setCreateTime(DateUtil.getCurrentDate());
                permit.setBusId(toMember.getBusId());
                permit.setValidity(toMemberBusUser.getEndTime());
                permit.setPackageId(unionPackage.getId());
                unionMainPermitService.save(permit);
            } else {
                throw new BusinessException("联盟盟主权限转移的目标盟员不具有联盟盟主服务");
            }
        }
        // （7）保存转移申请信息
        UnionMainTransfer saveTransfer = new UnionMainTransfer();
        saveTransfer.setDelStatus(CommonConstant.COMMON_NO);
        saveTransfer.setCreateTime(DateUtil.getCurrentDate());
        saveTransfer.setUnionId(unionId);
        saveTransfer.setFromMemberId(member.getId());
        saveTransfer.setToMemberId(toMemberId);
        saveTransfer.setConfirmStatus(UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
        save(saveTransfer);
    }

    //********************************************* Base On Business - remove ******************************************

    @Override
    public void removeByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId) throws Exception {
        if (busId == null || transferId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	判断transferId有效性
        //   （2-1）如果transfer不存在，则直接返回成功；
        //   （2-2）如果transfer存在，则删除后直接返回成功。
        UnionMainTransfer transfer = getValidByIdAndUnionIdAndConfirmStatus(transferId, unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
        if (transfer != null) {
            removeById(transferId);
        }
    }

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId, Integer isAccept) throws Exception {
        if (busId == null || transferId == null || unionId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断transferId有效性
        UnionMainTransfer transfer = getValidByIdAndUnionIdAndConfirmStatus(transferId, unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
        if (transfer == null) {
            throw new BusinessException("盟主权限转移申请不存在或已处理");
        }
        // （3）	如果拒绝，则直接更新，并返回；
        // （4）如果接受，则：
        //   （4-1）要求不是盟主；
        //   （4-2）要求具有盟主基础服务（调接口）；
        //   （4-3）要求具有有效的联盟盟主服务(如果没有且不用付费时，自动生成)；
        //   （4-4）事务操作
        if (CommonConstant.COMMON_YES != isAccept) {
            UnionMainTransfer updateTransfer = new UnionMainTransfer();
            updateTransfer.setId(transferId);
            updateTransfer.setConfirmStatus(UnionConstant.TRANSFER_CONFIRM_STATUS_REJECT);

            update(updateTransfer);
        } else {
            UnionMember busOwnerMember = unionMemberService.getValidOwnerByBusId(busId);
            if (busOwnerMember != null) {
                throw new BusinessException("已经是盟主身份，无法同时成为多个联盟的盟主");
            }

            UserUnionAuthority authority = busUserService.getUserUnionAuthority(busId);
            if (authority == null || !authority.getAuthority()) {
                throw new BusinessException("不具有联盟基础服务");
            }
            UnionMainPermit permit = unionMainPermitService.getValidByBusId(busId);
            if (permit == null) {
                if (!authority.getPay()) {
                    BusUser busUser = busUserService.getBusUserById(busId);
                    if (busUser == null) {
                        throw new BusinessException("找不到商家信息");
                    }
                    UnionMainPackage unionPackage = unionMainPackageService.getValidByLevel(busUser.getLevel());
                    if (unionPackage == null) {
                        throw new BusinessException("找不到套餐信息");
                    }
                    permit = new UnionMainPermit();
                    permit.setDelStatus(CommonConstant.COMMON_NO);
                    permit.setCreateTime(DateUtil.getCurrentDate());
                    permit.setBusId(busId);
                    permit.setPackageId(unionPackage.getId());
                    permit.setValidity(busUser.getEndTime());
                    unionMainPermitService.save(permit);
                } else {
                    throw new BusinessException("不具有联盟许可");
                }
            }

            UnionMainPackage unionPackage = unionMainPackageService.getValidById(permit.getPackageId());
            if (unionPackage == null) {
                throw new BusinessException("找不到套餐信息");
            }

            UnionMember newOwner = new UnionMember();
            newOwner.setId(member.getId());
            newOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES);

            UnionMember oldOwner = unionMemberService.getValidOwnerByUnionId(unionId);
            if (oldOwner == null) {
                throw new BusinessException("找不到盟主信息");
            }
            oldOwner.setId(oldOwner.getId());
            oldOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO);

            Integer readMemberCount = unionMemberService.countValidReadByUnionId(unionId);
            UnionMain updateUnion = new UnionMain();
            updateUnion.setId(unionId);
            if (unionPackage.getNumber() > readMemberCount) {
                updateUnion.setMemberLimit(unionPackage.getNumber());
            }
            updateUnion.setValidity(permit.getValidity());

            UnionMainTransfer updateTransfer = new UnionMainTransfer();
            updateTransfer.setId(transferId);
            updateTransfer.setConfirmStatus(UnionConstant.TRANSFER_CONFIRM_STATUS_ACCEPT);

            // 事务操作
            unionMemberService.update(oldOwner);
            unionMemberService.update(newOwner);
            unionMainService.update(updateUnion);
            update(updateTransfer);
        }
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public boolean existValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception {
        if (unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMainTransfer result = getValidByUnionIdAndConfirmStatus(unionId, confirmStatus);

        return result != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMainTransfer> filterByDelStatus(List<UnionMainTransfer> unionMainTransferList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainTransferList)) {
            for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                if (delStatus.equals(unionMainTransfer.getDelStatus())) {
                    result.add(unionMainTransfer);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionMainTransfer> filterByToMemberId(List<UnionMainTransfer> transferList, Integer toMemberId) throws Exception {
        if (transferList == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = new ArrayList<>();
        for (UnionMainTransfer transfer : transferList) {
            if (toMemberId.equals(transfer.getToMemberId())) {
                result.add(transfer);
            }
        }

        return result;
    }

    @Override
    public List<UnionMainTransfer> filterByConfirmStatus(List<UnionMainTransfer> transferList, Integer confirmStatus) throws Exception {
        if (transferList == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = new ArrayList<>();
        for (UnionMainTransfer transfer : transferList) {
            if (confirmStatus.equals(transfer.getConfirmStatus())) {
                result.add(transfer);
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMainTransfer getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer result;
        // (1)cache
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        result = unionMainTransferDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMainTransfer getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMainTransfer getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMainTransfer> unionMainTransferList) throws Exception {
        if (unionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainTransferList)) {
            for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                result.add(unionMainTransfer.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setCache(result, fromMemberId, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String validFromMemberIdKey = UnionMainTransferCacheUtil.getValidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(validFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setValidCache(result, fromMemberId, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String invalidFromMemberIdKey = UnionMainTransferCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(invalidFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setInvalidCache(result, fromMemberId, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setCache(result, toMemberId, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String validToMemberIdKey = UnionMainTransferCacheUtil.getValidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(validToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setValidCache(result, toMemberId, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String invalidToMemberIdKey = UnionMainTransferCacheUtil.getInvalidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(invalidToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setInvalidCache(result, toMemberId, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String unionIdKey = UnionMainTransferCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String validUnionIdKey = UnionMainTransferCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String invalidUnionIdKey = UnionMainTransferCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMainTransferDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMainTransfer> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainTransferDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainTransfer newUnionMainTransfer) throws Exception {
        if (newUnionMainTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainTransferDao.insert(newUnionMainTransfer);
        removeCache(newUnionMainTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainTransfer> newUnionMainTransferList) throws Exception {
        if (newUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainTransferDao.insertBatch(newUnionMainTransferList);
        removeCache(newUnionMainTransferList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainTransfer unionMainTransfer = getById(id);
        removeCache(unionMainTransfer);
        // (2)remove in db logically
        UnionMainTransfer removeUnionMainTransfer = new UnionMainTransfer();
        removeUnionMainTransfer.setId(id);
        removeUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMainTransferDao.updateById(removeUnionMainTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainTransfer> unionMainTransferList = listByIdList(idList);
        removeCache(unionMainTransferList);
        // (2)remove in db logically
        List<UnionMainTransfer> removeUnionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer removeUnionMainTransfer = new UnionMainTransfer();
            removeUnionMainTransfer.setId(id);
            removeUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainTransferList.add(removeUnionMainTransfer);
        }
        unionMainTransferDao.updateBatchById(removeUnionMainTransferList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainTransfer updateUnionMainTransfer) throws Exception {
        if (updateUnionMainTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainTransfer.getId();
        UnionMainTransfer unionMainTransfer = getById(id);
        removeCache(unionMainTransfer);
        // (2)update db
        unionMainTransferDao.updateById(updateUnionMainTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainTransfer> updateUnionMainTransferList) throws Exception {
        if (updateUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainTransferList);
        List<UnionMainTransfer> unionMainTransferList = listByIdList(idList);
        removeCache(unionMainTransferList);
        // (2)update db
        unionMainTransferDao.updateBatchById(updateUnionMainTransferList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMainTransfer newUnionMainTransfer, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainTransfer);
    }

    private void setCache(List<UnionMainTransfer> newUnionMainTransferList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainTransferList);
        }
    }

    private void setValidCache(List<UnionMainTransfer> newUnionMainTransferList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                validForeignIdKey = UnionMainTransferCacheUtil.getValidFromMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                validForeignIdKey = UnionMainTransferCacheUtil.getValidToMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMainTransferCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainTransferList);
        }
    }

    private void setInvalidCache(List<UnionMainTransfer> newUnionMainTransferList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                invalidForeignIdKey = UnionMainTransferCacheUtil.getInvalidFromMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                invalidForeignIdKey = UnionMainTransferCacheUtil.getInvalidToMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMainTransferCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainTransferList);
        }
    }

    private void removeCache(UnionMainTransfer unionMainTransfer) {
        if (unionMainTransfer == null) {
            return;
        }
        Integer id = unionMainTransfer.getId();
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer fromMemberId = unionMainTransfer.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);

            String validFromMemberIdKey = UnionMainTransferCacheUtil.getValidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(validFromMemberIdKey);

            String invalidFromMemberIdKey = UnionMainTransferCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(invalidFromMemberIdKey);
        }

        Integer toMemberId = unionMainTransfer.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);

            String validToMemberIdKey = UnionMainTransferCacheUtil.getValidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(validToMemberIdKey);

            String invalidToMemberIdKey = UnionMainTransferCacheUtil.getInvalidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(invalidToMemberIdKey);
        }

        Integer unionId = unionMainTransfer.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainTransferCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMainTransferCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMainTransferCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionMainTransfer> unionMainTransferList) {
        if (ListUtil.isEmpty(unionMainTransferList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
            idList.add(unionMainTransfer.getId());
        }
        List<String> idKeyList = UnionMainTransferCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMainTransfer> unionMainTransferList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer fromMemberId = unionMainTransfer.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);

                        String validFromMemberIdKey = UnionMainTransferCacheUtil.getValidFromMemberIdKey(fromMemberId);
                        result.add(validFromMemberIdKey);

                        String invalidFromMemberIdKey = UnionMainTransferCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
                        result.add(invalidFromMemberIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer toMemberId = unionMainTransfer.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);

                        String validToMemberIdKey = UnionMainTransferCacheUtil.getValidToMemberIdKey(toMemberId);
                        result.add(validToMemberIdKey);

                        String invalidToMemberIdKey = UnionMainTransferCacheUtil.getInvalidToMemberIdKey(toMemberId);
                        result.add(invalidToMemberIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer unionId = unionMainTransfer.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainTransferCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMainTransferCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMainTransferCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}