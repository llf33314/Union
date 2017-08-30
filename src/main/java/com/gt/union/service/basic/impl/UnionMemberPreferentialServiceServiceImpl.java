package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialManagerConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.basic.UnionMemberPreferentialManager;
import com.gt.union.entity.basic.UnionMemberPreferentialService;
import com.gt.union.entity.consume.UnionConsumeServiceRecord;
import com.gt.union.mapper.basic.UnionMemberPreferentialServiceMapper;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberPreferentialManagerService;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.service.consume.IUnionConsumeServiceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * <p>
 * 盟员优惠服务项 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMemberPreferentialServiceServiceImpl extends ServiceImpl<UnionMemberPreferentialServiceMapper, UnionMemberPreferentialService> implements IUnionMemberPreferentialServiceService {

    private static final String PAGE_PREFERENTIAL_SERVICE_MANAGERID = "UnionMemberPreferentialServiceServiceImpl.pagePreferentialServiceByManagerId()";
    private static final String SAVE = "UnionMemberPreferentialServiceServiceImpl.save()";
    private static final String DELETE = "UnionMemberPreferentialServiceServiceImpl.delete()";
    private static final String VERIFY = "UnionMemberPreferentialServiceServiceImpl.verify()";
    private static final String ADD_SERVICE_VERIFY = "UnionMemberPreferentialServiceServiceImpl.addServiceVerify()";

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMemberPreferentialManagerService unionMemberPreferentialManagerService;

    @Autowired
    private IUnionConsumeServiceRecordService unionConsumeServiceRecordService;

    @Autowired
    private IUnionRootService unionRootService;

    @Autowired
    private IUnionMainService unionMainService;

    @Override
    public List<Map<String, Object>> listPreferentialServiceByManagerId(final Integer managerId, final Integer verifyStatus) throws Exception {
        if (managerId == null) {
            throw new ParamException(PAGE_PREFERENTIAL_SERVICE_MANAGERID, "参数managerId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(PAGE_PREFERENTIAL_SERVICE_MANAGERID, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                .eq("manager_id", managerId)
                .eq("verify_status", verifyStatus);

        entityWrapper.setSqlSelect(" id id, service_name serviceName, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");
        return this.selectMaps(entityWrapper);
    }

	@Override
	public Page listMyByUnionId(Page page, final Integer unionId, final Integer busId) throws Exception{
        final UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" s");
                sbSqlSegment.append(" LEFT JOIN t_union_member_preferential_manager m ON s.manager_id = m.id ")
                        .append(" WHERE m.union_id=").append(unionId)
                        .append("    AND m.member_id = ").append(member.getId())
                        .append("    AND m.del_status = ").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append("    AND s.del_status = ").append(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO);
                sbSqlSegment.append(" ORDER BY s.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" s.id id ")
                .append(", s.service_name serviceName ")
                .append(", s.verify_status verifystatus ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Integer unionId, Integer busId, String serviceName) throws Exception{
        if (unionId == null) {
            throw new ParamException(SAVE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(SAVE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMain main = unionMainService.getById(unionId);
        if(!unionRootService.checkUnionMainValid(main)){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        if(!unionRootService.hasUnionMemberAuthority(unionId,busId)){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
        }
        if(CommonUtil.isEmpty(main.getRedCardOpend()) || main.getRedCardOpend().intValue() == UnionMainConstant.RED_CARD_NON_OPEN){
            throw new BusinessException(SAVE, "", "联盟未开启红卡，不可保存");
        }
        if (StringUtil.isEmpty(serviceName)) {
            throw new ParamException(SAVE, "优惠项目名称为空", "优惠项目名称不能为空");
        }
        if(StringUtil.getStringLength(serviceName) > UnionMemberPreferentialServiceConstant.SERVICE_NAME_MAX_LENGTH){
            throw new ParamException(SAVE, "优惠项目名称长度超过10字", "优惠项目名称长度不可超过10字");
        }
        UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        EntityWrapper entityWrapper = new EntityWrapper<UnionMemberPreferentialService>();
        entityWrapper.eq("del_status",UnionMemberPreferentialServiceConstant.DEL_STATUS_NO);
        entityWrapper.eq("union_member_id",member.getId());
        int serviceCount = this.selectCount(entityWrapper);
        if(serviceCount == CommonConstant.MAX_PREFERENIAL_COUNT){
            throw new BusinessException(SAVE,"优惠项目已达上限","优惠项目已达上限");
        }
        UnionMemberPreferentialManager manager = unionMemberPreferentialManagerService.getManagerByUnionId(unionId,busId);
        UnionMemberPreferentialService service = new UnionMemberPreferentialService();
        service.setCreatetime(new Date());
        service.setDelStatus(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO);
        service.setServiceName(serviceName);
        service.setUnionMemberId(member.getId());
        if(member.getIsNuionOwner() == UnionMemberConstant.IS_UNION_OWNER_YES){//盟主
            service.setVerifyStatus(UnionMemberPreferentialServiceConstant.VERIFY_STATUS_PASS);
        }else {
            service.setVerifyStatus(UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT);
        }
        if(CommonUtil.isEmpty(manager)){
            manager = new UnionMemberPreferentialManager();
            manager.setCreatetime(new Date());
            manager.setMemberId(member.getId());
            manager.setUnionId(unionId);
            manager.setVerifyStatus(UnionMemberPreferentialManagerConstant.VERIFY_PASS_STATUS);
            manager.setPreferentialIllustration("");
            manager.setLastModifyTime(new Date());
            manager.setDelStatus(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO);
            unionMemberPreferentialManagerService.insert(manager);
        }
        service.setManagerId(manager.getId());
        this.insert(service);
    }

    @Override
    public void delete(Integer unionId, String ids) throws Exception{
        if (unionId == null) {
            throw new ParamException(DELETE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (StringUtil.isEmpty(ids)) {
            throw new ParamException(DELETE, "参数ids为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMain main = unionMainService.getById(unionId);
        if(CommonUtil.isEmpty(main.getRedCardOpend()) || main.getRedCardOpend().intValue() == 0){
            throw new BusinessException(SAVE, "", "联盟未开启红卡，不可删除");
        }
        String[] list = ids.split(",");
        if(list.length == 0){
            throw new ParamException(DELETE, ids, ExceptionConstant.PARAM_ERROR);
        }
        List<Integer> serviceIds = new ArrayList<Integer>();
        if(list.length > 1){//批量删除
            for(String id : list){
                if(StringUtil.isEmpty(id)){
                    throw new ParamException(DELETE, ids, ExceptionConstant.PARAM_ERROR);
                }
                serviceIds.add(CommonUtil.toInteger(id));
            }
            delBatch(serviceIds);//批量删除

        }else {
            if(StringUtil.isEmpty(list[0])){
                throw new ParamException(DELETE, ids, ExceptionConstant.PARAM_ERROR);
            }
            Integer id = CommonUtil.toInteger(list[0]);
            serviceIds.add(id);
            delBatch(serviceIds);//删除一个
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void delBatch(List<Integer> serviceIds) throws Exception{
        List<UnionMemberPreferentialService> upList = new ArrayList<UnionMemberPreferentialService>();//更新列表
        List<UnionConsumeServiceRecord> records = unionConsumeServiceRecordService.getListGrouyByServiceId(serviceIds);
        if(records.size()>0){
            for(UnionConsumeServiceRecord record : records){
                UnionMemberPreferentialService service = new UnionMemberPreferentialService();
                service.setDelStatus(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO);
                service.setId(record.getServiceId());
                upList.add(service);
                Iterator<Integer> it = serviceIds.iterator();
                while (it.hasNext()){
                    Integer id = it.next();
                    if(id.equals(record.getServiceId())){
                        it.remove();
                        break;
                    }
                }
            }

            if(upList.size() > 0){
                if(!this.updateBatchById(upList)){
                    throw new BusinessException(DELETE,"删除失败","删除失败");
                }
            }
            if(serviceIds.size() > 0){
                if(!this.deleteBatchIds(serviceIds)){
                    throw new BusinessException(DELETE,"删除失败","删除失败");
                }
            }
        }else {
            if(!this.deleteBatchIds(serviceIds)){
                throw new BusinessException(DELETE,"删除失败","删除失败");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addServiceVerify(Integer unionId, Integer id) throws Exception{
        if (unionId == null) {
            throw new ParamException(ADD_SERVICE_VERIFY, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (id == null) {
            throw new ParamException(ADD_SERVICE_VERIFY, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMain main = unionMainService.getById(unionId);
        if(CommonUtil.isEmpty(main.getRedCardOpend()) || main.getRedCardOpend().intValue() == UnionMainConstant.RED_CARD_NON_OPEN){
            throw new BusinessException(SAVE, "", "联盟未开启红卡");
        }
        EntityWrapper entityWrapper = new EntityWrapper<UnionMemberPreferentialService>();
        entityWrapper.eq("del_status",UnionMemberPreferentialServiceConstant.DEL_STATUS_NO);
        entityWrapper.eq("id",id);
        UnionMemberPreferentialService service = this.selectOne(entityWrapper);
        if(CommonUtil.isEmpty(service)){
           throw new BusinessException(ADD_SERVICE_VERIFY,"优惠项目不存在","优惠项目不存在");
        }
        if(service.getVerifyStatus() != UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT){
            throw new BusinessException(ADD_SERVICE_VERIFY,"优惠项目已提交","优惠项目已提交");
        }
        service.setVerifyStatus(UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK);
        this.updateById(service);
    }

    @Override
    public void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception{
        if (unionId == null) {
            throw new ParamException(VERIFY, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(VERIFY, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (StringUtil.isEmpty(ids)) {
            throw new ParamException(VERIFY, "参数ids为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(VERIFY, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMain main = unionMainService.getById(unionId);
        if(!unionRootService.checkUnionMainValid(main)){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        if(!this.unionRootService.isUnionOwner(unionId,busId)){
            throw new BusinessException(VERIFY, "您不是盟主，没有权限审核", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
        }
        if(CommonUtil.isEmpty(main.getRedCardOpend()) || main.getRedCardOpend().intValue() == UnionMainConstant.RED_CARD_NON_OPEN){
            throw new BusinessException(SAVE, "", "联盟未开启红卡");
        }
        String[] list = ids.split(",");
        if(list.length == 0){
            throw new ParamException(VERIFY, ids, ExceptionConstant.PARAM_ERROR);
        }
        List<Integer> serviceIds = new ArrayList<Integer>();
        if(list.length > 1) {//批量审核
            for(String id : list){
                if(StringUtil.isEmpty(id)){
                    throw new ParamException(VERIFY, ids, ExceptionConstant.PARAM_ERROR);
                }
                serviceIds.add(CommonUtil.toInteger(id));
            }
            verifyBatch(serviceIds,verifyStatus);
        }else {//单审核
            Integer id = CommonUtil.toInteger(list[0]);
            serviceIds.add(id);
            verifyBatch(serviceIds,verifyStatus);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    private void verifyBatch(List<Integer> serviceIds, Integer verifyStatus)throws Exception{
        List<UnionMemberPreferentialService> services = this.selectBatchIds(serviceIds);
        for(UnionMemberPreferentialService service : services){
            if(CommonUtil.isEmpty(service)){
                throw new BusinessException(VERIFY, "优惠项目不存在","优惠项目不存在");
            }
            if(service.getDelStatus() == UnionMemberPreferentialServiceConstant.DEL_STATUS_NO){
                throw new BusinessException(VERIFY, "优惠项目不存在","优惠项目不存在");
            }
            if(service.getVerifyStatus() == UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT){
                throw new BusinessException(VERIFY, "优惠项目未提交","优惠项目未提交");
            }
            if(service.getVerifyStatus() != UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK){
                throw new BusinessException(VERIFY, "优惠项目已审核","优惠项目已审核");
            }
            service.setVerifyStatus(verifyStatus);
        }
        this.updateBatchById(services);
    }


}
