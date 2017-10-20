package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.entity.UnionMainTransfer;
import com.gt.union.main.mapper.UnionMainTransferMapper;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.service.IUnionMainTransferService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟转移 服务实现类
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@Service
public class UnionMainTransferServiceImpl extends ServiceImpl<UnionMainTransferMapper, UnionMainTransfer> implements IUnionMainTransferService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public UnionMainTransfer getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer fromMemberId
            , Integer toMemberId, Integer confirmStatus) throws Exception {
        if (unionId == null || fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> transferList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(transferList)) {
            for (UnionMainTransfer transfer : transferList) {
                if (!fromMemberId.equals(transfer.getFromMemberId())) {
                    continue;
                }
                if (!toMemberId.equals(transfer.getToMemberId())) {
                    continue;
                }
                if (!confirmStatus.equals(transfer.getConfirmStatus())) {
                    continue;
                }
                return transfer;
            }
        }
        return null;
    }

    @Override
    public UnionMainTransfer getByIdAndToMemberIdAndConfirmStatus(Integer transferId, Integer toMemberId, Integer confirmStatus) throws Exception {
        if (transferId == null || toMemberId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer transfer = this.getById(transferId);
        if (!toMemberId.equals(transfer.getToMemberId()) || !confirmStatus.equals(transfer.getConfirmStatus())) {
            return null;
        }
        return transfer;
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public Page pageMapByBusIdAndFromMemberId(Page page, Integer busId, Integer fromMemberId) throws Exception {
        if (page == null || busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }

        return this.unionMemberService.pageTransferMapByUnionOwner(page, unionOwner);
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    @Override
    public void saveByBusIdAndFromMemberIdAndToMemberId(Integer busId, Integer fromMemberId, Integer toMemberId) throws Exception {
        if (busId == null || fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember fromMember = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (fromMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(fromMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(fromMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主身份
        if (!fromMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断是否已转移记录
        List<UnionMainTransfer> transferList = this.listByFromMemberId(fromMemberId);
        if (ListUtil.isNotEmpty(transferList)) {
            throw new BusinessException("已存在转移申请，请撤销申请后再提交");
        }
        Integer unionId = fromMember.getUnionId();
        //(6)判断目标对象是否在同一个联盟
        UnionMember toMember = this.unionMemberService.getById(toMemberId);
        if (toMember == null) {
            throw new BusinessException("目标对象不存在或已过期");
        }
        if (!toMember.getUnionId().equals(unionId)) {
            throw new BusinessException("不在同一个联盟，无法操作");
        }
        //(7)判断目标对象的状态
        if (toMember.getStatus() == MemberConstant.STATUS_APPLY_IN) {
            throw new BusinessException("目标对象尚未入盟");
        }
        if (toMember.getStatus() == MemberConstant.STATUS_OUTING) {
            throw new BusinessException("目标对象正处于退盟过渡期");
        }
        //(8)判断目标对象是否是盟主
        if (this.unionMemberService.isUnionOwner(toMember.getBusId())) {
            throw new BusinessException("目标对象已经是另一个联盟的盟主");
        }
        //(9)判断目标对象是否具有盟主服务许可
        if (!this.unionMainPermitService.hasUnionMainPermit(toMember.getBusId())) {
            throw new BusinessException("目标对象不具有盟主服务许可");
        }
        //(10)判断是否已存在转移申请
        UnionMainTransfer transfer = this.getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(unionId
                , fromMemberId, toMemberId, MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
        if (transfer == null) {
            UnionMainTransfer saveTransfer = new UnionMainTransfer();
            //删除状态
            saveTransfer.setDelStatus(CommonConstant.DEL_STATUS_NO);
            //联盟id
            saveTransfer.setUnionId(unionId);
            //创建时间
            saveTransfer.setCreatetime(DateUtil.getCurrentDate());
            //转移盟主权限的盟员身份id
            saveTransfer.setFromMemberId(fromMemberId);
            //目标盟员身份id
            saveTransfer.setToMemberId(toMemberId);
            //确认状态
            saveTransfer.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
            this.save(saveTransfer);
        }
    }

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByTransferIdAndBusIdAndToMemberId(Integer transferId, Integer busId, Integer toMemberId, Integer isOK) throws Exception {
        if (transferId == null || busId == null || toMemberId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.unionMemberService.getByIdAndBusId(toMemberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(member.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断转移记录
        UnionMainTransfer transfer = this.getByIdAndToMemberIdAndConfirmStatus(transferId, toMemberId, MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
        if (transfer == null) {
            throw new BusinessException("转移记录不存在或已处理");
        }
        //(5)判断转移者、被转移者及转移记录上的联盟id是否一致
        Integer unionId = transfer.getUnionId();
        UnionMember fromMember = this.unionMemberService.getById(transfer.getFromMemberId());
        UnionMember toMember = this.unionMemberService.getByIdAndBusId(toMemberId, busId);
        if (!unionId.equals(fromMember.getUnionId()) || !unionId.equals(toMember.getUnionId())) {
            throw new BusinessException("转移者、被转移者及转移记录上的联盟信息不一致");
        }
        switch (isOK) {
            case CommonConstant.COMMON_NO:
                //拒绝
                //(6)更新转移申请
                UnionMainTransfer updateTransfer = new UnionMainTransfer();
                //转移申请id
                updateTransfer.setId(transfer.getId());
                //转移申请状态改为拒绝
                updateTransfer.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_NO);
                //(7)事务化操作
                //更新转移申请
                this.update(updateTransfer);
                break;
            case CommonConstant.COMMON_YES:
                //接受
                //(6)判断转移者是否仍然是盟主
                if (fromMember.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
                    throw new BusinessException("盟主权限已转移他人");
                }
                //(7)判断转移者、被转移者的状态
                if (fromMember.getStatus() != MemberConstant.STATUS_IN || toMember.getStatus() != MemberConstant.STATUS_IN) {
                    throw new BusinessException("转移者或被转移者的状态已发生改变");
                }
                //(8)判断被转移者是否已经是盟主
                if (this.unionMemberService.isUnionOwner(busId)) {
                    throw new BusinessException("已是盟主，无法接受");
                }
                //(9)判断是否具有盟主服务许可
                if (!this.unionMainPermitService.hasUnionMainPermit(busId)) {
                    throw new BusinessException("不具有盟主服务许可或已过期");
                }
                //(10)更新的联盟信息
                UnionMain updateUnion = new UnionMain();
                //联盟id
                updateUnion.setId(unionId);
                UnionMainPermit permit = this.unionMainPermitService.getByBusId(busId);
                if (permit != null) {
                    //联盟有效期
                    updateUnion.setUnionValidity(permit.getValidity());
                } else {
                    //联盟有效期
                    updateUnion.setUnionValidity(DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT));
                }
                Integer limitMember = this.unionMainService.getLimitMemberByBusId(busId);
                //联盟成员总数上限
                updateUnion.setLimitMember(limitMember);
                //(11)更新的转移者信息
                UnionMember updateFromMember = new UnionMember();
                //转移者的id
                updateFromMember.setId(fromMember.getId());
                //转移者盟主身份取消
                updateFromMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO);
                //(12)更新的被转移者信息
                UnionMember updateToMember = new UnionMember();
                //被转移者的id
                updateToMember.setId(toMember.getId());
                //被转移者添加盟主身份
                updateToMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES);
                //(13)更新转移申请
                UnionMainTransfer updateTransfer2 = new UnionMainTransfer();
                //转移申请id
                updateTransfer2.setId(transferId);
                //转移申请状态改为接受
                updateTransfer2.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_YES);
                //(14)事务化操作
                //更新联盟信息
                this.unionMainService.update(updateUnion);
                //更新转移者信息
                this.unionMemberService.update(updateFromMember);
                //更新被转移者
                this.unionMemberService.update(updateToMember);
                //更新转移申请
                this.update(updateTransfer2);
                break;
            default:
                throw new BusinessException("无法识别的更新状态");
        }
    }

    @Override
    public void revokeByIdAndBusIdAndFromMemberId(Integer transferId, Integer busId, Integer fromMemberId) throws Exception {
        if (transferId == null || busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断转移申请状态
        UnionMainTransfer transfer = this.getById(transferId);
        if (transfer == null) {
            throw new BusinessException("未找到盟主权限转移信息");
        }
        if (!transfer.getFromMemberId().equals(fromMemberId)) {
            throw new BusinessException("无法撤回他人的盟主权限转移申请");
        }
        if (!transfer.getConfirmStatus().equals(MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING)) {
            throw new BusinessException("该盟主权限转移申请已处理，无法撤回");
        }
        //(6)将申请置为无效
        UnionMainTransfer updateTransfer = new UnionMainTransfer();
        //转移id
        updateTransfer.setId(transferId);
        //删除状态
        updateTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.update(updateTransfer);
    }

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    @Override
    public UnionMainTransfer getById(Integer transferId) throws Exception {
        if (transferId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer result;
        //(1)cache
        String transferIdKey = RedisKeyUtil.getTransferIdKey(transferId);
        if (this.redisCacheUtil.exists(transferIdKey)) {
            String tempStr = this.redisCacheUtil.get(transferIdKey);
            result = JSONArray.parseObject(tempStr, UnionMainTransfer.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", transferId);
        result = this.selectOne(entityWrapper);
        setCache(result, transferId);
        return result;
    }

    //******************************************* Object As a Service - list *******************************************

    @Override
    public List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        //(1)get in cache
        String unionIdKey = RedisKeyUtil.getTransferUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = this.selectList(entityWrapper);
        setCache(result, unionId, MainConstant.REDIS_KEY_TRANSFER_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        //(1)get in cache
        String fromMemberIdKey = RedisKeyUtil.getTransferFromMemberIdKey(fromMemberId);
        if (this.redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, fromMemberId, MainConstant.REDIS_KEY_TRANSFER_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        //(1)get in cache
        String toMemberIdKey = RedisKeyUtil.getTransferToMemberIdKey(toMemberId);
        if (this.redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, toMemberId, MainConstant.REDIS_KEY_TRANSFER_TO_MEMBER_ID);
        return result;
    }

    //******************************************* Object As a Service - save *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainTransfer newTransfer) throws Exception {
        if (newTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newTransfer);
        this.removeCache(newTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainTransfer> newTransferList) throws Exception {
        if (newTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newTransferList);
        this.removeCache(newTransferList);
    }

    //******************************************* Object As a Service - remove *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer transferId) throws Exception {
        if (transferId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMainTransfer transfer = this.getById(transferId);
        removeCache(transfer);
        //(2)remove in db logically
        UnionMainTransfer removeTransfer = new UnionMainTransfer();
        removeTransfer.setId(transferId);
        removeTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> transferIdList) throws Exception {
        if (transferIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMainTransfer> transferList = new ArrayList<>();
        for (Integer transferId : transferIdList) {
            UnionMainTransfer transfer = this.getById(transferId);
            transferList.add(transfer);
        }
        removeCache(transferList);
        //(2)remove in db logically
        List<UnionMainTransfer> removeTransferList = new ArrayList<>();
        for (Integer transferId : transferIdList) {
            UnionMainTransfer removeTransfer = new UnionMainTransfer();
            removeTransfer.setId(transferId);
            removeTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeTransferList.add(removeTransfer);
        }
        this.updateBatchById(removeTransferList);
    }

    //******************************************* Object As a Service - update *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainTransfer updateTransfer) throws Exception {
        if (updateTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer transferId = updateTransfer.getId();
        UnionMainTransfer transfer = this.getById(transferId);
        removeCache(transfer);
        //(2)update db
        this.updateById(updateTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainTransfer> updateTransferList) throws Exception {
        if (updateTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> transferIdList = new ArrayList<>();
        for (UnionMainTransfer updateTransfer : updateTransferList) {
            transferIdList.add(updateTransfer.getId());
        }
        List<UnionMainTransfer> transferList = new ArrayList<>();
        for (Integer transferId : transferIdList) {
            UnionMainTransfer transfer = this.getById(transferId);
            transferList.add(transfer);
        }
        removeCache(transferList);
        //(2)update db
        this.updateBatchById(updateTransferList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainTransfer newTransfer, Integer transferId) {
        if (transferId == null) {
            return; //do nothing,just in case
        }
        String transferIdKey = RedisKeyUtil.getTransferIdKey(transferId);
        this.redisCacheUtil.set(transferIdKey, newTransfer);
    }

    private void setCache(List<UnionMainTransfer> newTransferList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_TRANSFER_UNION_ID:
                foreignIdKey = RedisKeyUtil.getTransferUnionIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newTransferList);
                break;
            case MainConstant.REDIS_KEY_TRANSFER_FROM_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getTransferFromMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newTransferList);
                break;
            case MainConstant.REDIS_KEY_TRANSFER_TO_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getTransferToMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newTransferList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMainTransfer transfer) {
        if (transfer == null) {
            return;
        }
        Integer transferId = transfer.getId();
        String transferIdKey = RedisKeyUtil.getTransferIdKey(transferId);
        this.redisCacheUtil.remove(transferIdKey);
        Integer unionId = transfer.getUnionId();
        if (unionId != null) {
            String unionIdKey = RedisKeyUtil.getTransferUnionIdKey(unionId);
            this.redisCacheUtil.remove(unionIdKey);
        }
        Integer fromMemberId = transfer.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = RedisKeyUtil.getTransferFromMemberIdKey(fromMemberId);
            this.redisCacheUtil.remove(fromMemberIdKey);
        }
        Integer toMemberId = transfer.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = RedisKeyUtil.getTransferToMemberIdKey(toMemberId);
            this.redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionMainTransfer> transferList) {
        if (ListUtil.isEmpty(transferList)) {
            return;
        }
        List<Integer> transferIdList = new ArrayList<>();
        for (UnionMainTransfer transfer : transferList) {
            transferIdList.add(transfer.getId());
        }
        List<String> transferIdKeyList = RedisKeyUtil.getTransferIdKey(transferIdList);
        this.redisCacheUtil.remove(transferIdKeyList);
        List<String> unionIdKeyList = getForeignIdKeyList(transferList, MainConstant.REDIS_KEY_TRANSFER_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            this.redisCacheUtil.remove(unionIdKeyList);
        }
        List<String> fromMemberIdKeyList = getForeignIdKeyList(transferList, MainConstant.REDIS_KEY_TRANSFER_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            this.redisCacheUtil.remove(fromMemberIdKeyList);
        }
        List<String> toMemberIdKeyList = getForeignIdKeyList(transferList, MainConstant.REDIS_KEY_TRANSFER_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            this.redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainTransfer> transferList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_TRANSFER_UNION_ID:
                for (UnionMainTransfer transfer : transferList) {
                    Integer unionId = transfer.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = RedisKeyUtil.getTransferUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case MainConstant.REDIS_KEY_TRANSFER_FROM_MEMBER_ID:
                for (UnionMainTransfer transfer : transferList) {
                    Integer fromMemberId = transfer.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = RedisKeyUtil.getTransferFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case MainConstant.REDIS_KEY_TRANSFER_TO_MEMBER_ID:
                for (UnionMainTransfer transfer : transferList) {
                    Integer toMemberId = transfer.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = RedisKeyUtil.getTransferToMemberIdKey(toMemberId);
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
