package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 联盟转移 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainTransferServiceImpl extends ServiceImpl<UnionMainTransferMapper, UnionMainTransfer> implements IUnionMainTransferService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id、转移盟主权限的盟员身份id、目标盟员身份id和确认状态，获取盟主权限转移记录
     *
     * @param unionId       {not null} 联盟id
     * @param fromMemberId  {not null} 转移盟主权限的盟员身份id
     * @param toMemberId    {not null} 目标盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainTransfer getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer fromMemberId
            , Integer toMemberId, Integer confirmStatus) throws Exception {
        if (unionId == null || fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("from_member_id", fromMemberId)
                .eq("to_member_id", toMemberId)
                .eq("confirm_status", confirmStatus);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据转移申请id，获取转移申请对象
     *
     * @param transferId {not null} 转移申请id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainTransfer getById(Integer transferId) throws Exception {
        if (transferId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", transferId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据转移申请id、接受者盟员身份id和确认状态，获取转移申请信息
     *
     * @param transferId    {not null} 转移申请id
     * @param toMemberId    {not null} 接受者盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainTransfer getByIdAndToMemberIdAndConfirmStatus(Integer transferId, Integer toMemberId, Integer confirmStatus) throws Exception {
        if (transferId == null || toMemberId == null || confirmStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", transferId)
                .eq("to_member_id", toMemberId)
                .eq("confirm_status", confirmStatus);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据转移者的盟主身份id，获取盟主权限转移申请列表信息
     *
     * @param fromMemberId {not null} 转移者的盟主身份id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId)
                .eq("confirm_status", MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id和转移者盟员身份id，分页获取盟主服务转移申请列表
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Page pageMapByBusIdAndFromMemberId(Page page, Integer busId, Integer fromMemberId) throws Exception {
        if (page == null || busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember ownerMember = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (ownerMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(ownerMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(ownerMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }

        return this.unionMemberService.pageTransferMapByUnionOwner(page, ownerMember);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据转移申请id、商家id、接受者盟员身份id和是否同意，更新盟主服务权限转移信息
     *
     * @param transferId {not null} 转移申请id
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接受者盟员身份id
     * @param isOK       {not null} 是否同意，1为是，0为否
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStatusByTransferIdAndBusIdAndToMemberId(Integer transferId, Integer busId, Integer toMemberId, Integer isOK) throws Exception {
        if (transferId == null || busId == null || toMemberId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(toMemberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断转移记录
        UnionMainTransfer unionMainTransfer = this.getByIdAndToMemberIdAndConfirmStatus(transferId, toMemberId, MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
        if (unionMainTransfer == null) {
            throw new BusinessException("转移记录不存在或已处理");
        }
        //(5)判断转移者、被转移者及转移记录上的联盟id是否一致
        Integer unionId = unionMainTransfer.getUnionId();
        UnionMember fromUnionMember = this.unionMemberService.getById(unionMainTransfer.getFromMemberId());
        UnionMember toUnionMember = this.unionMemberService.getByIdAndBusId(toMemberId, busId);
        if (!unionId.equals(fromUnionMember.getUnionId()) || !unionId.equals(toUnionMember.getUnionId())) {
            throw new BusinessException("转移者、被转移者及转移记录上的联盟信息不一致");
        }
        switch (isOK) {
            case CommonConstant.COMMON_NO: //拒绝
                //(6)更新转移申请
                UnionMainTransfer updateTransfer = new UnionMainTransfer();
                updateTransfer.setId(unionMainTransfer.getId()); //转移申请id
                updateTransfer.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_NO); //转移申请状态改为拒绝
                //(7)事务化操作
                this.updateById(updateTransfer);//更新转移申请
                break;
            case CommonConstant.COMMON_YES: //接受
                //(6)判断转移者是否仍然是盟主
                if (fromUnionMember.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
                    throw new BusinessException("盟主权限已转移他人");
                }
                //(7)判断转移者、被转移者的状态
                if (fromUnionMember.getStatus() != MemberConstant.STATUS_IN || toUnionMember.getStatus() != MemberConstant.STATUS_IN) {
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
                UnionMain updateUnionMain = new UnionMain();
                updateUnionMain.setId(unionId); //联盟id
                UnionMainPermit unionMainPermit = this.unionMainPermitService.getByBusId(busId);
                if (unionMainPermit != null) {
                    updateUnionMain.setUnionValidity(unionMainPermit.getValidity()); //联盟有效期
                } else {
                    updateUnionMain.setUnionValidity(DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT)); //联盟有效期
                }
                Integer limitMember = this.unionMainService.getLimitMemberByBusId(busId);
                updateUnionMain.setLimitMember(limitMember); //联盟成员总数上限
                //(11)更新的转移者信息
                UnionMember updateFromMember = new UnionMember();
                updateFromMember.setId(fromUnionMember.getId()); //转移者的id
                updateFromMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //转移者盟主身份取消
                //(12)更新的被转移者信息
                UnionMember updateToMember = new UnionMember();
                updateToMember.setId(toUnionMember.getId()); //被转移者的id
                updateToMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES); //被转移者添加盟主身份
                //(13)更新转移申请
                UnionMainTransfer updateTransfer2 = new UnionMainTransfer();
                updateTransfer2.setId(unionMainTransfer.getId()); //转移申请id
                updateTransfer2.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_YES); //转移申请状态改为接受
                //(14)事务化操作
                this.unionMainService.updateById(updateUnionMain);//更新联盟信息
                this.unionMemberService.updateById(updateFromMember);//更新转移者信息
                this.unionMemberService.updateById(updateToMember); //更新被转移者
                this.updateById(updateTransfer2);//更新转移申请
                break;
            default:
                throw new BusinessException("无法识别的更新状态");
        }
    }

    /**
     * 根据转移申请id、商家id和转移者的盟员身份id， 撤回盟主服务转移申请
     *
     * @param transferId   {not null} 转移申请id
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者的盟员身份id
     * @throws Exception
     */
    @Override
    public void revokeByIdAndBusIdAndFromMemberId(Integer transferId, Integer busId, Integer fromMemberId) throws Exception {
        if (transferId == null || busId == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember ownerMember = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (ownerMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(ownerMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(ownerMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
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
        updateTransfer.setId(transferId); //转移id
        updateTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
        this.updateById(updateTransfer);
    }

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id、转移盟主权限的盟员身份id和目标盟员身份id，保存盟主权限转移信息
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移盟主权限的盟员身份id
     * @param toMemberId   {not null} 目标盟员身份id
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(fromMember.getUnionId());
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
        UnionMainTransfer unionMainTransfer = this.getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(unionId
                , fromMemberId, toMemberId, MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING);
        if (unionMainTransfer == null) {
            UnionMainTransfer saveUnionMainTransfer = new UnionMainTransfer();
            saveUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveUnionMainTransfer.setUnionId(unionId); //联盟id
            saveUnionMainTransfer.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveUnionMainTransfer.setFromMemberId(fromMemberId); //转移盟主权限的盟员身份id
            saveUnionMainTransfer.setToMemberId(toMemberId); //目标盟员身份id
            saveUnionMainTransfer.setConfirmStatus(MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING); //确认状态
            this.insert(saveUnionMainTransfer);
        }
    }

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
