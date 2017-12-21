package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.mapper.UnionMainTransferMapper;
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

import java.util.*;

/**
 * 联盟转移 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainTransferServiceImpl extends ServiceImpl<UnionMainTransferMapper, UnionMainTransfer> implements IUnionMainTransferService {
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

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMainTransfer getByUnionIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer toMemberId, Integer confirmStatus) throws Exception {
        if (unionId == null || toMemberId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = listByUnionId(unionId);
        result = filterByToMemberId(result, toMemberId);
        result = filterByConfirmStatus(result, confirmStatus);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMainTransfer getByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception {
        if (unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainTransfer> result = listByUnionId(unionId);
        result = filterByConfirmStatus(result, confirmStatus);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMainTransfer getByIdAndUnionIdAndConfirmStatus(Integer transferId, Integer unionId, Integer confirmStatus) throws Exception {
        if (transferId == null || unionId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMainTransfer result = getById(transferId);

        return result != null && unionId.equals(result.getUnionId()) && confirmStatus.equals(result.getConfirmStatus()) ? result : null;
    }


    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionTransferVO> listUnionTransferVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	按有转移id排第一，其他按时间顺序排序
        List<UnionTransferVO> result = new ArrayList<>();
        List<UnionMember> writeMemberList = unionMemberService.listWriteByUnionId(unionId);
        if (ListUtil.isNotEmpty(writeMemberList)) {
            for (UnionMember writeMember : writeMemberList) {
                if (member.getId().equals(writeMember.getId())) {
                    continue;
                }

                UnionTransferVO vo = new UnionTransferVO();
                vo.setMember(writeMember);

                UnionMainTransfer transfer = getByUnionIdAndToMemberIdAndConfirmStatus(unionId, writeMember.getId(), UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
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

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId) throws Exception {
        if (busId == null || unionId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断是否已存在其他转移申请:
        //   （2-1）如果是，则报错；
        //   （2-2）如果不是，则进行下一步；
        UnionMainTransfer transfer = getByUnionIdAndConfirmStatus(unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
        if (transfer != null) {
            throw new BusinessException("已存在联盟盟主权限转移申请，请撤消后再操作");
        }
        // （3）	判断toMemberId有效性和写权限；
        UnionMember toMember = unionMemberService.getWriteByIdAndUnionId(toMemberId, unionId);
        if (toMember == null) {
            throw new BusinessException("联盟盟主权限转移的目标盟员不存在，或处于退盟过渡期，或已退盟");
        }
        // （4）	判断toMember是否是另一个联盟的盟主：
        //   （4-1）如果是，则报错；
        //   （4-2）如果不是，则进行下一步；
        UnionMember toMemberOwner = unionMemberService.getOwnerByBusId(toMember.getBusId());
        if (toMemberOwner != null) {
            throw new BusinessException("联盟盟主权限转移的目标盟员已经是另一个联盟的盟主，无法同时成为多个联盟的盟主");
        }
        // （5）	判断toMember是否具有联盟基础服务（调接口）
        //   （5-1）如果不是，则报错；
        //   （5-2）如果是，则进行下一步；
        Map<String, Object> basicMap = busUserService.getUserUnionAuthority(busId);
        if (basicMap == null) {
            throw new BusinessException("联盟盟主权限转移的目标盟员不具有联盟基础服务");
        }
        Object objAuthority = basicMap.get("authority");
        Integer hasAuthority = objAuthority != null ? Integer.valueOf(objAuthority.toString()) : CommonConstant.COMMON_NO;
        if (CommonConstant.COMMON_YES != hasAuthority) {
            throw new BusinessException("联盟盟主权限转移的目标盟员不具有联盟基础服务");
        }
        // （6）	判断toMember是否具有联盟许可:
        //   （6-1）如果不是，则判断是否需要付费：
        //     （6-1-1）如果不是，则新增免费的联盟许可；
        //     （6-1-2）如果是，则报错；
        //   （6-2）如果是，则进行下一步；
        UnionMainPermit permit = unionMainPermitService.getValidByBusId(toMember.getBusId());
        if (permit == null) {
            Object objPay = basicMap.get("pay");
            Integer needPay = objPay != null ? Integer.valueOf(objPay.toString()) : CommonConstant.COMMON_YES;
            if (CommonConstant.COMMON_YES != needPay) {
                BusUser toMemberBusUser = busUserService.getBusUserById(toMember.getBusId());
                if (toMemberBusUser == null) {
                    throw new BusinessException("找不到联盟盟主权限转移目标盟员的商家信息");
                }
                UnionMainPackage unionPackage = unionMainPackageService.getByLevel(toMemberBusUser.getLevel());
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

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void revokeByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId) throws Exception {
        if (busId == null || transferId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断transferId有效性
        //   （2-1）如果transfer不存在，则直接返回成功；
        //   （2-2）如果transfer存在，则删除后直接返回成功。
        UnionMainTransfer transfer = getByIdAndUnionIdAndConfirmStatus(transferId, unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
        if (transfer != null) {
            removeById(transferId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAcceptByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId, Integer isAccept) throws Exception {
        if (busId == null || transferId == null || unionId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断transferId有效性
        UnionMainTransfer transfer = getByIdAndUnionIdAndConfirmStatus(transferId, unionId, UnionConstant.TRANSFER_CONFIRM_STATUS_PROCESS);
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
            UnionMember busOwnerMember = unionMemberService.getOwnerByBusId(busId);
            if (busOwnerMember != null) {
                throw new BusinessException("已经是盟主身份，无法同时成为多个联盟的盟主");
            }

            Map<String, Object> basicMap = busUserService.getUserUnionAuthority(busId);
            if (basicMap == null) {
                throw new BusinessException("不具有联盟基础服务");
            }
            Object objAuthority = basicMap.get("authority");
            Integer hasAuthority = objAuthority != null ? Integer.valueOf(objAuthority.toString()) : CommonConstant.COMMON_NO;
            if (CommonConstant.COMMON_YES != hasAuthority) {
                throw new BusinessException("不具有联盟基础服务");
            }
            UnionMainPermit permit = unionMainPermitService.getValidByBusId(busId);
            if (permit == null) {
                Object objPay = basicMap.get("pay");
                Integer needPay = objPay != null ? Integer.valueOf(objPay.toString()) : CommonConstant.COMMON_YES;
                if (CommonConstant.COMMON_YES != needPay) {
                    BusUser busUser = busUserService.getBusUserById(busId);
                    if (busUser == null) {
                        throw new BusinessException("找不到商家信息");
                    }
                    UnionMainPackage unionPackage = unionMainPackageService.getByLevel(busUser.getLevel());
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

            UnionMainPackage unionPackage = unionMainPackageService.getById(permit.getPackageId());
            if (unionPackage == null) {
                throw new BusinessException("找不到套餐信息");
            }

            UnionMember newOwner = new UnionMember();
            newOwner.setId(member.getId());
            newOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES);

            UnionMember oldOwner = unionMemberService.getOwnerByUnionId(unionId);
            if (oldOwner == null) {
                throw new BusinessException("找不到盟主信息");
            }
            oldOwner.setId(oldOwner.getId());
            oldOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO);

            Integer readMemberCount = unionMemberService.countReadByUnionId(unionId);
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

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************\

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

    //***************************************** Object As a Service - get **********************************************

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
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        return result;
    }

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
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

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
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainTransfer newUnionMainTransfer) throws Exception {
        if (newUnionMainTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainTransfer);
        removeCache(newUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainTransfer> newUnionMainTransferList) throws Exception {
        if (newUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainTransferList);
        removeCache(newUnionMainTransferList);
    }

    //***************************************** Object As a Service - remove *******************************************

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
        updateById(removeUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainTransfer> unionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer unionMainTransfer = getById(id);
            unionMainTransferList.add(unionMainTransfer);
        }
        removeCache(unionMainTransferList);
        // (2)remove in db logically
        List<UnionMainTransfer> removeUnionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer removeUnionMainTransfer = new UnionMainTransfer();
            removeUnionMainTransfer.setId(id);
            removeUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainTransferList.add(removeUnionMainTransfer);
        }
        updateBatchById(removeUnionMainTransferList);
    }

    //***************************************** Object As a Service - update *******************************************

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
        updateById(updateUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainTransfer> updateUnionMainTransferList) throws Exception {
        if (updateUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainTransfer updateUnionMainTransfer : updateUnionMainTransferList) {
            idList.add(updateUnionMainTransfer.getId());
        }
        List<UnionMainTransfer> unionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer unionMainTransfer = getById(id);
            unionMainTransferList.add(unionMainTransfer);
        }
        removeCache(unionMainTransferList);
        // (2)update db
        updateBatchById(updateUnionMainTransferList);
    }

    //***************************************** Object As a Service - cache support ************************************

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
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getUnionInKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainTransferList);
        }
    }

    private void removeCache(UnionMainTransfer unionMainTransfer) {
        if (unionMainTransfer == null) {
            return;
        }
        Integer id = unionMainTransfer.getId();
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainTransfer.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionMainTransfer.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionMainTransfer.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
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

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainTransfer> unionMainTransferList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer unionId = unionMainTransfer.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer fromMemberId = unionMainTransfer.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer toMemberId = unionMainTransfer.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }


}