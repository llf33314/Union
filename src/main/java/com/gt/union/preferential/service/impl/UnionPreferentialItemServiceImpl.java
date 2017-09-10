package com.gt.union.preferential.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.constant.ChargeConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.mapper.UnionPreferentialItemMapper;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠服务项 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionPreferentialItemServiceImpl extends ServiceImpl<UnionPreferentialItemMapper, UnionPreferentialItem> implements IUnionPreferentialItemService {


    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;


    @Override
    public List<Map<String, Object>> listByProjectIdWidthStatus(Integer projectId, Integer verifyStatus) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId)
                .eq("verify_status", verifyStatus);
        entityWrapper.setSqlSelect(" id, name, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");
        return this.selectMaps(entityWrapper);
    }

    @Override
    public Page listByUnionId(Page page, final Integer unionId, final Integer busId) throws Exception {
        UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        final UnionPreferentialProject project = unionPreferentialProjectService.getByMemberId(member.getId());
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder("");
                sbSqlSegment.append(" WHERE project_id = ").append(project.getId())
                            .append(" ORDER BY id DESC ");
                return sbSqlSegment.toString();
            };

        };
        wrapper.setSqlSelect(" id, name, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public void save(Integer unionId, Integer busId, String name) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException( CommonConstant.PARAM_ERROR);
        }
        UnionMain main = unionMainService.getUnionMainById(unionId);
        unionMainService.checkUnionMainValid(main);
        //TODO 添加优惠服务  检验盟员身份
       UnionMainCharge unionMainCharge =  unionMainChargeService.getByUnionIdAndType(main.getId(), ChargeConstant.RED_CARD_TYPE);
       if(unionMainCharge == null || unionMainCharge.getIsAvailable().equals(ChargeConstant.AVAILABLE_NO)){
           throw new BusinessException("联盟未开启红卡，不可添加优惠项目");
       }
        if (StringUtil.isEmpty(name)) {
            throw new ParamException("优惠项目名称不能为空");
        }
        if(StringUtil.getStringLength(name) > 50){
            throw new ParamException("优惠项目名称长度不可超过50字");
        }
        UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        if(member == null){
            throw new BusinessException("您");
        }
        EntityWrapper entityWrapper = new EntityWrapper<UnionPreferentialItem>();
        entityWrapper.eq("del_status",0);
        entityWrapper.eq("union_member_id",member.getId());
        int serviceCount = this.selectCount(entityWrapper);
        if(serviceCount == CommonConstant.MAX_PREFERENIAL_COUNT){
            throw new BusinessException("优惠项目已达上限");
        }
        UnionPreferentialProject project= unionPreferentialProjectService.getByMemberId(member.getId());
        UnionPreferentialItem item = new UnionPreferentialItem();
        item.setCreatetime(new Date());
        item.setDelStatus(CommonConstant.DEL_STATUS_NO);
        item.setName(name);
        item.setProjectId(project.getId());
        if(member.getIsNuionOwner() == 1){//盟主
            item.setVerifyStatus(2);
        }else {
            item.setVerifyStatus(0);
        }
        if(CommonUtil.isEmpty(project)){
            project = new UnionPreferentialProject();
            project.setCreatetime(new Date());
            project.setMemberId(member.getId());
            project.setIllustration("");
            project.setModifytime(new Date());
            project.setDelStatus(CommonConstant.DEL_STATUS_NO);
            unionPreferentialProjectService.insert(project);
        }
        item.setProjectId(project.getId());
        this.insert(item);
    }

    @Override
    public void delete(Integer unionId, Integer busId, String ids) throws Exception {

    }

    @Override
    public void addVerify(Integer unionId, Integer busId, Integer id) throws Exception {

    }

    @Override
    public void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception {

    }
}
