package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.mapper.basic.UnionApplyMapper;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.vo.basic.UnionApplyVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 联盟成员申请推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionApplyServiceImpl extends ServiceImpl<UnionApplyMapper, UnionApply> implements IUnionApplyService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionApplyInfoService unionApplyInfoService;

    @Override
    public Page listUncheckedApply(Page page, final Integer unionId, final String enterpriseName, final String directorPhone) throws Exception{
        if (page == null) {
            throw new Exception("UnionApplyServiceImpl.pageUnionApplyVO()：参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionApplyServiceImpl.pageUnionApplyVO()：参数union不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" a");
                sbSqlSegment.append(" LEFT JOIN t_union_apply_info i ON i.union_apply_id = a.id ")
                            .append(" LEFT JOIN t_union_apply a2 ON a2.apply_bus_id = a.recommend_bus_id AND a2.union_id = ")
                            .append(unionId)
                            .append(" LEFT JOIN t_union_apply_info i2 ON i2.union_apply_id = a2.id ")
                            .append(" WHERE ")
                            .append(" a.union_id = " ).append(unionId)
                            .append(" AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                            .append(" AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_UNCHECKED);

                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE '%").append(enterpriseName.trim()).append("%' ");
                }
                if (StringUtil.isNotEmpty(directorPhone)) {
                    sbSqlSegment.append(" AND i.director_phone LIKE '%").append(directorPhone.trim()).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY a.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" a.id id, a.union_id unionId")
                .append(", DATE_FORMAT(a.createtime, '%Y-%m-%d %T') createtime ")
                .append(", a.apply_status applyStatus ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", i.director_name directorName ")
                .append(", i.director_phone directorPhone ")
                .append(", i.director_email directorEmail ")
                .append(", i.apply_reason applyReason ")
                .append(", i2.enterprise_name recommendBusName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }



    @Override
    public UnionApplyInfo getUnionApplyInfo(final Integer busId, final Integer unionId) throws Exception{

        if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(unionId)){
            throw new ParameterException("参数错误");
        }
        UnionApplyInfo info = null;
        if ( redisCacheUtil.exists( "unionApplyInfo:" + unionId + ":" + busId ) ) {
            // 1.1 存在则从redis 读取
            redisCacheUtil.removePattern("*Union:*");
//            info = JSON.parseObject(redisCacheUtil.get("unionApplyInfo:" + unionId + ":" + busId ).toString(),UnionApplyInfo.class);
        } else {
            Wrapper wrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" t1");
                    sbSqlSegment.append(" WHERE EXISTS  ")
                            .append("( SELECT t2.* from t_union_apply t2")
                            .append(" WHERE t1.union_apply_id = t2.id " )
                            .append(" AND t2.union_id = " ).append(unionId)
                            .append(" AND t2.del_status = " ).append(UnionApplyConstant.DEL_STATUS_NO)
                            .append(" AND t2.apply_bus_id = " ).append(busId)
                            .append(" AND t2.apply_status = " ).append(UnionApplyConstant.APPLY_STATUS_PASS)
                            .append(")");
                    return sbSqlSegment.toString();
                };

            };
            StringBuilder sbSqlSelect = new StringBuilder();
            sbSqlSelect.append(" t1.id , t1.union_apply_id unionApplyId, t1.apply_reason applyReason, t1.enterprise_name enterpriseName, t1.director_name, ")
                    .append("t1.director_phone directorPhone, t1.director_email directorEmail, t1.bus_address busAddress, t1.notify_phone notifyPhone, ")
                    .append("t1.address_longitude addressLongitude, t1.address_latitude addressLatitude, t1.address_provience_id addressProvienceId, ")
                    .append("t1.address_city_id addressCityId, t1.address_district_id addressDistrictId, t1.integral_proportion integralProportion, ")
                    .append("t1.is_member_out_advice isMemberOutAdvice");
            wrapper.setSqlSelect(sbSqlSelect.toString());
            info = unionApplyInfoService.selectOne(wrapper);
            // 写入 Redis 操作
            if(CommonUtil.isNotEmpty(info)){
                System.out.printf(JSON.toJSONString(info));
                redisCacheUtil.set("unionApplyInfo:" + unionId + ":" + busId, JSON.toJSONString(info));
            }
        }
        return info;
    }

	@Override
	public UnionApply getUnionApply(Integer busId, Integer unionId) {
        EntityWrapper<UnionApply> entityWrapper = new EntityWrapper<UnionApply>();
        entityWrapper.eq("union_id",unionId)
                .eq("bus_id",busId)
                .eq("del_status",UnionApplyConstant.DEL_STATUS_NO);
        UnionApply apply = this.selectOne(entityWrapper);
		return apply;
	}

	@Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUnionApplyVerify(Integer busId, final Integer id, Integer unionId, Integer applyStatus) throws Exception{
        if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(id) || CommonUtil.isEmpty(unionId) || CommonUtil.isEmpty(applyStatus)){
            throw new ParameterException("参数错误");
        }
        if(!unionMemberService.isUnionOwner(unionId, busId)){
            throw new BusinessException("没有盟主权限");
        }
        UnionApply unionApply = this.selectById(id);
        if(unionApply == null || unionApply.getDelStatus() == 1){
            throw new BusinessException("该申请不存在");
        }
        if(unionApply.getApplyStatus() != 0){
            throw new BusinessException("该申请已审核");
        }
        if(applyStatus == 1){//通过
            //TODO 判断对方的商家账号是否过期
            //盟员数达上限
            UnionMain main = unionMainService.getUnionMain(unionId);
            if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
                throw new BusinessException("联盟成员已达上限");
            }
            //商家加盟数上限
            int count = unionMemberService.getUnionMemberCount(unionApply.getApplyBusId());
            if(count == CommonConstant.MAX_UNION_APPLY){
                throw new BusinessException("该商家加盟已达上限");
            }

            UnionApply apply = new UnionApply();
            apply.setId(id);
            apply.setApplyStatus(1);
            if(CommonUtil.isNotEmpty(unionApply.getRecommendBusId())){
                apply.setBusConfirmStatus(2);
            }
            UnionMember unionMember = new UnionMember();
            unionMember.setBusId(unionApply.getApplyBusId());
            unionMember.setCreatetime(new Date());
            unionMember.setUnionId(unionId);
            unionMember.setDelStatus(0);
            unionMember.setIsNuionOwner(0);
            unionMember.setOutStaus(0);
            String unionSign = createUnionSignByUnionId(unionId);
            unionMember.setUnionIDSign(unionSign);
            unionMemberService.insert(unionMember);
            apply.setUnionMemberId(unionMember.getId());
            this.updateById(apply);
            main.setUnionMemberNum(CommonUtil.isEmpty(main.getUnionMemberNum()) ? 1 : main.getUnionMemberNum() + 1);
            unionMainService.updateById(main);
        }else if(applyStatus == 2){//不通过
            //删除信息
            this.deleteById(id);
            Wrapper wrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" t1");
                    sbSqlSegment.append(" WHERE union_apply_id = ").append(id);
                    return sbSqlSegment.toString();
                };

            };
            unionApplyInfoService.delete(wrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> addUnionApply(Integer busId, Integer unionId, UnionApplyVO vo, Integer applyType) throws Exception{
        Map<String,Object> data = new HashMap<String,Object>();
        if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(unionId) || CommonUtil.isEmpty(vo) || CommonUtil.isEmpty(applyType)){
            throw new ParameterException("参数错误");
        }
        UnionMain main = unionMainService.getUnionMain(unionId);
        if(applyType == 1){//自由申请
            //1、判断是否加入了该盟
            UnionMember unionMember = unionMemberService.getUnionMember(busId,unionId);
            if(unionMember != null){
                throw new BusinessException("您已加入该盟");
            }
            //2、判断是否申请了
            UnionApply unionApply = this.getUnionApply(busId,unionId);
            if(unionApply != null){
                throw new BusinessException("您已申请加入该盟");
            }
            //3、判断盟员数是否达上限
            if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
                throw new BusinessException("联盟成员已达上限");
            }
            //4、判断加盟数是否达上限
            if(unionMemberService.getUnionMemberCount(busId) == CommonConstant.MAX_UNION_APPLY){
                throw new BusinessException("您加入的联盟已达上限");
            }
            UnionApply apply = saveUnionApply(busId,unionId,applyType);
            UnionApplyInfo unionApplyInfo = vo.getUnionApplyInfo();
            UnionApplyInfo info = unionApplyInfoService.saveUnionApplyInfo(unionApplyInfo, apply.getId());
            //申请成功后，发送短信让盟主审核
            UnionApplyInfo mainInfo = this.getUnionApplyInfo(main.getBusId(),main.getId());
            String nowTime = "apply:"+System.currentTimeMillis();
            HashMap<String, Object> redisMap = new HashMap<>();
            redisMap.put("busId",main.getBusId());
            redisMap.put("mobiles",mainInfo.getNotifyPhone());
            String content = "\""+info.getEnterpriseName()+"\" 申请加入 \"" + main.getUnionName() +"\"，请到入盟审核处查看并处理。";//短信内容
            redisMap.put("content",content);
            redisMap.put("model",CommonConstant.SMS_UNION_MODEL);
            redisCacheUtil.set(nowTime, JSON.toJSONString(redisMap),60l);
            data.put("redisKey",nowTime);
        }else if(applyType == 2){//推荐加盟
            //1、判断联盟是否可推荐加盟
            if(main.getJoinType() != 2){
                throw new BusinessException("联盟不支持推荐加盟");
            }
            //当前用户
            UnionMember userMember = unionMemberService.getUnionMember(busId,unionId);
            if(userMember == null){
                throw new BusinessException("您已退盟，不可推荐");
            }
            if(userMember.getOutStaus() == 2){
                throw new BusinessException("您处于退盟过渡期，不可推荐");
            }
            //TODO 2、判断推荐人的账号
            String userName = vo.getUserName();
            UnionMember unionMember = unionMemberService.getUnionMember(busId,unionId);
            //1、判断是否加入了该盟
            if(unionMember != null){
                throw new BusinessException("已加入本联盟");
            }
            //2、判断是否申请了
            UnionApply unionApply = this.getUnionApply(busId,unionId);
            if(unionApply != null){
                throw new BusinessException("已申请加入本联盟");
            }
            //3、判断盟员数是否达上限
            if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
                throw new BusinessException("联盟成员已达上限");
            }
            //4、判断加盟数是否达上限
            if(unionMemberService.getUnionMemberCount(busId) == CommonConstant.MAX_UNION_APPLY){
                throw new BusinessException("您加入的联盟已达上限");
            }
            UnionApply apply = saveUnionApply(busId,unionId,applyType);
            UnionApplyInfo unionApplyInfo = vo.getUnionApplyInfo();
            UnionApplyInfo info = unionApplyInfoService.saveUnionApplyInfo(unionApplyInfo, apply.getId());
            boolean isOwner = unionMemberService.isUnionOwner(unionId,busId);//联盟盟主
            if(isOwner){
                UnionMember member = new UnionMember();
                member.setBusId(busId);
                member.setCreatetime(new Date());
                member.setUnionId(unionId);
                member.setDelStatus(0);
                member.setIsNuionOwner(0);
                member.setOutStaus(0);
                String unionSign = createUnionSignByUnionId(unionId);
                member.setUnionIDSign(unionSign);
                unionMemberService.insert(member);
                apply.setUnionMemberId(member.getId());
                this.updateById(apply);
                main.setUnionMemberNum(CommonUtil.isEmpty(main.getUnionMemberNum()) ? 1 : main.getUnionMemberNum() + 1);
                unionMainService.updateById(main);
            }
        }
        return data;
	}

    /**
     * 保存UnionApply
     * @param busId
     * @param unionId
     * @param applyType
     * @return
     */
	private UnionApply saveUnionApply(Integer busId, Integer unionId, Integer applyType){
        UnionApply apply = new UnionApply();
        apply.setApplyStatus(0);
        apply.setBusConfirmStatus(0);
        apply.setApplyBusId(busId);
        apply.setCreatetime(new Date());
        apply.setDelStatus(0);
        apply.setUnionId(unionId);
        apply.setApplyType(applyType);
        this.insert(apply);
        return apply;
    }


	/**
     * 根据联盟id获取联盟商家标识
     * @param unionId
     * @return
     * @throws BaseException
     */
    private String createUnionSignByUnionId(Integer unionId) throws BaseException {
        int count = 0;
        String unionSign = "";
        EntityWrapper entityWrapper = new EntityWrapper<UnionMember>();
        do{
            unionSign  = getUnionSign(8);
            entityWrapper.eq("union_ID_sign",unionSign);
            entityWrapper.eq("union_id",unionId);
            count = unionMemberService.selectCount(entityWrapper);
        }while(count > 0);
        return unionSign;
    }


    /**
     * 获取联盟标识
     * @param length
     * @return
     */
    public String getUnionSign(int length){
        String letterAndFigureBase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(letterAndFigureBase.length());
            sb.append(letterAndFigureBase.charAt(number));
        }
        return sb.toString();
    }
}
