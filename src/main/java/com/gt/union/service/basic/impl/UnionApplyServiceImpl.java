package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.DictService;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.*;
import com.gt.union.entity.common.BusUser;
import com.gt.union.mapper.basic.UnionApplyMapper;
import com.gt.union.service.basic.*;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.basic.UnionApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String LIST_UNIONID_APPLYSTATUS = "UnionApplyServiceImpl.listByUnionIdAndApplyStatus()";
    private static final String GET_UNION_APPLY_INFO = "UnionApplyServiceImpl.getUnionApplyInfo()";
    private static final String UPDATE_UNIONID_APPLYSTATUS = "UnionApplyServiceImpl.updateByUnionIdAndApplyStatus()";
    private static final String SAVE = "UnionApplyServiceImpl.save()";
    private static final String UNION_APPLY_MSG = "UnionApplyServiceImpl.getUnionApplyMsgInfo()";

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionApplyInfoService unionApplyInfoService;

    @Autowired
    private IUnionRootService unionRootService;

    @Autowired
    private IUnionInfoDictService unionInfoDictService;

    @Autowired
    private BusUserService busUserService;

    @Override
    public Page listByUnionIdAndApplyStatus(Page page, final Integer unionId, final Integer applyStatus, final String enterpriseName, final String directorPhone) throws Exception{
        if (page == null) {
            throw new ParamException(LIST_UNIONID_APPLYSTATUS, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_UNIONID_APPLYSTATUS, "参数union为空", ExceptionConstant.PARAM_ERROR);
        }
        if (applyStatus == null) {
            throw new ParamException(LIST_UNIONID_APPLYSTATUS, "参数applyStatus为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" a");
                sbSqlSegment.append(" LEFT JOIN t_union_apply_info i ON i.union_apply_id = a.id ")
                            .append(" LEFT JOIN t_union_apply a2 ON a2.apply_bus_id = a.recommend_bus_id ")
                            .append("    AND a2.union_id = a.union_id ")
                            .append("    AND a2.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                            .append("    AND a2.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                            .append(" LEFT JOIN t_union_apply_info i2 ON i2.union_apply_id = a2.id ")
                            .append(" WHERE a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                            .append("    AND a.union_id = ").append(unionId)
                            .append("    AND a.apply_status = ").append(applyStatus);
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
            throw new ParamException(GET_UNION_APPLY_INFO, "参数busId或unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionApplyInfo info = null;
        String unionApplyInfoKey = RedisKeyUtil.getUnionApplyInfoKey(unionId,busId);
        if ( redisCacheUtil.exists( unionApplyInfoKey) ) {
            // 1.1 存在则从redis 读取
            Object obj = redisCacheUtil.get(unionApplyInfoKey);
            if(CommonUtil.isNotEmpty(obj)){
                return JSON.parseObject(obj.toString(),UnionApplyInfo.class);
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper<UnionApplyInfo>();
        StringBuilder sqlExists = new StringBuilder();
        sqlExists.append("SELECT t1.id, t1.union_id, t1.apply_bus_id, t1.del_status, t1.apply_status FROM t_union_apply t1 WHERE t1.id = t_union_apply_info.union_apply_id")
                .append(" AND t1.union_id = ").append(unionId)
                .append(" AND t1.apply_bus_id = ").append(busId)
                .append(" AND t1.del_status = ").append(0)
                .append(" AND t1.apply_status = ").append(1);
        entityWrapper.exists(sqlExists.toString());
        info = unionApplyInfoService.selectOne(entityWrapper);
        // 写入 Redis 操作
        if(CommonUtil.isNotEmpty(info)){
            redisCacheUtil.set(unionApplyInfoKey, JSON.toJSONString(info));
        }
        return info;
    }

	@Override
	public UnionApply getUnionApply(Integer busId, Integer unionId) {
        EntityWrapper<UnionApply> entityWrapper = new EntityWrapper<UnionApply>();
        entityWrapper.eq("union_id",unionId)
                .eq("apply_bus_id",busId)
                .eq("del_status",UnionApplyConstant.DEL_STATUS_NO);
        UnionApply apply = this.selectOne(entityWrapper);
		return apply;
	}

	@Transactional(rollbackFor = Exception.class)
    @Override
    public void updateByUnionIdAndApplyStatus(Integer busId, final Integer id, Integer unionId, Integer applyStatus) throws Exception{
        if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(id) || CommonUtil.isEmpty(unionId) || CommonUtil.isEmpty(applyStatus)){
            throw new ParamException(UPDATE_UNIONID_APPLYSTATUS, "参数...错误", ExceptionConstant.PARAM_ERROR);
        }
        if(!unionRootService.checkUnionMainValid(unionId)){
            throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        if(!unionRootService.isUnionOwner(unionId, busId)){
            throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "没有盟主权限");
        }
        UnionApply unionApply = this.selectById(id);
        if(unionApply == null || unionApply.getDelStatus() == 1){
            throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "该申请不存在");
        }
        if(unionApply.getApplyStatus() != 0){
            throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "已审核");
        }
        if(applyStatus == 1){//通过
            //判断审核的账号有效期
            if(!unionRootService.checkBusUserValid(unionApply.getApplyBusId())){
                throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "该盟员账号已过期");
            }
            //盟员数达上限
            UnionMain main = unionMainService.getById(unionId);
            if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
                throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "联盟成员已达上限");
            }
            //商家加盟数上限
            int count = unionMemberService.getUnionMemberCount(unionApply.getApplyBusId());
            if(count == CommonConstant.MAX_UNION_APPLY){
                throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", "该商家加盟已达上限");
            }

            UnionApply apply = new UnionApply();
            apply.setId(id);
            apply.setApplyStatus(1);
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
            String unionMainKey = RedisKeyUtil.getUnionMainKey(main.getId());
            redisCacheUtil.set(unionMainKey,JSON.toJSONString(main));
            String unionMemberBusIdKey = RedisKeyUtil.getUnionMemberBusIdKey(main.getId(),unionMember.getBusId());
            redisCacheUtil.set(unionMemberBusIdKey,JSON.toJSONString(unionMember));
        }else if(applyStatus == 2){//不通过
            //删除信息
            UnionApply apply = new UnionApply();
            apply.setId(id);
            apply.setDelStatus(1);
            this.updateById(apply);
            String infoKey = RedisKeyUtil.getUnionApplyInfoKey(unionId,unionApply.getApplyBusId());
            redisCacheUtil.remove(infoKey);
        }
    }

    @Override
    public Map<String, Object> getUnionApplyMsgInfo(String redisKey) throws Exception{
        if(StringUtil.isEmpty(redisKey)){
            throw new ParamException(UNION_APPLY_MSG, "参数redisKey为空", "参数redisKey为空");
        }
        Object data = redisCacheUtil.get(redisKey);
        if(data == null){
            throw new ParamException(UNION_APPLY_MSG, "", "redis失效");
        }
        Map<String,Object> result = JSON.parseObject(data.toString(), Map.class);
        Map<String,Object> obj = new HashMap<String,Object>();
        obj.put("reqdata",result);
        return obj;
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> save(Integer busId, UnionApplyVO vo) throws Exception{
        Map<String,Object> data = new HashMap<String,Object>();
        Integer unionId = vo.getUnionId();
        if(!unionRootService.checkUnionMainValid(unionId)){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        Integer applyType = vo.getApplyType();
        UnionMain main = unionMainService.getById(unionId);
        if(applyType == 1){//自由申请
            saveApply(busId,vo,main);
        }else if(applyType == 2){//推荐加盟
            saveRecommendApply(busId,vo,main);
        }
        return data;
	}

    /**
     * 申请
     * @param busId
     * @param vo
     * @return
     */
	private Map<String, Object> saveApply(Integer busId, UnionApplyVO vo, UnionMain main) throws Exception{
        Map<String,Object> data = new HashMap<String,Object>();
        //1、判断是否加入了该盟
        UnionMember unionMember = unionMemberService.getByUnionIdAndBusId(busId,main.getId());
        if(unionMember != null){
            throw new BusinessException(SAVE, "", "您已加入该盟");
        }
        //2、判断是否申请了
        UnionApply unionApply = this.getUnionApply(busId,main.getId());
        if(unionApply != null){
            throw new BusinessException(SAVE, "", "您已申请加入该盟");
        }
        //3、判断盟员数是否达上限
        if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_NUM_MAX_MSG);
        }
        //4、判断加盟数是否达上限
        if(unionMemberService.getUnionMemberCount(busId) == CommonConstant.MAX_UNION_APPLY){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_MEMBER_NUM_MAX_MSG);
        }
        List<UnionInfoDict> list = unionInfoDictService.getUnionInfoDict(main.getId());
        checkUnionApplyInfo(list,vo);//校验数据
        UnionApply apply = saveUnionApply(busId,main.getId(),vo.getApplyType());//创建apply
        UnionApplyInfo info = unionApplyInfoService.saveUnionApplyInfo(vo, apply.getId());//创建applyInfo
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
        String infoKey = RedisKeyUtil.getUnionApplyInfoKey(main.getId(),busId);
        redisCacheUtil.set(infoKey,JSON.toJSONString(info));
        return data;
    }


    /**
     * 保存推荐加盟
     * @param busId
     * @param vo
     * @param main
     * @return
     * @throws Exception
     */
    private Map<String, Object> saveRecommendApply(Integer busId, UnionApplyVO vo, UnionMain main) throws Exception{
        Map<String,Object> data = new HashMap<String,Object>();
        //1、判断联盟是否可推荐加盟
        if(main.getJoinType() != 2){
            throw new BusinessException(SAVE, "", "联盟不支持推荐加盟");
        }
        //当前用户
        UnionMember userMember = unionMemberService.getByUnionIdAndBusId(busId,main.getId());
        if(!unionRootService.hasUnionMemberAuthority(userMember)){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
        }
        String userName = vo.getUserName();
        if(StringUtil.isEmpty(userName)){
            throw new ParamException(SAVE, "参数错误", ExceptionConstant.PARAM_ERROR);
        }
        BusUser user = busUserService.getBusUserByName(userName);
        if(CommonUtil.isEmpty(user)){
            throw new ParamException(SAVE, "账号不存在", "您推荐的盟员账号不存在");
        }
        if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
            throw new BusinessException(SAVE, "推荐子账号", "请推荐主账号");
        }
        if(!unionRootService.checkBusUserValid(user)){
            throw new BusinessException(SAVE, "被推荐账号无效", "您推荐的盟员账号已过期");
        }
        UnionMember unionMember = unionMemberService.getByUnionIdAndBusId(user.getId(),main.getId());
        //1、判断是否加入了该盟
        if(unionMember != null){
            throw new BusinessException(SAVE, "", "已加入本联盟");
        }
        //2、判断是否申请了
        UnionApply unionApply = this.getUnionApply(user.getId(),main.getId());
        if(unionApply != null){
            throw new BusinessException(SAVE, "", "已申请加入本联盟");
        }
        //3、判断盟员数是否达上限
        if(main.getUnionMemberNum().equals(main.getUnionTotalMember())){
            throw new BusinessException(SAVE, "", CommonConstant.UNION_NUM_MAX_MSG);
        }
        //4、判断加盟数是否达上限
        if(unionMemberService.getUnionMemberCount(user.getId()) == CommonConstant.MAX_UNION_APPLY){
            throw new BusinessException(SAVE, "", "您推荐的账号加入的联盟已达上限");
        }
        List<UnionInfoDict> list = unionInfoDictService.getUnionInfoDict(main.getId());
        checkUnionApplyInfo(list,vo);//校验数据
        UnionApply apply = saveUnionApply(user.getId(),main.getId(),vo.getApplyType());
        UnionApplyInfo info = unionApplyInfoService.saveUnionApplyInfo(vo, apply.getId());
        boolean isOwner = unionRootService.isUnionOwner(main.getId(),user.getId());//联盟盟主
        if(isOwner){
            UnionMember member = new UnionMember();
            member.setBusId(user.getId());
            member.setCreatetime(new Date());
            member.setUnionId(main.getId());
            member.setDelStatus(0);
            member.setIsNuionOwner(0);
            member.setOutStaus(0);
            String unionSign = createUnionSignByUnionId(main.getId());
            member.setUnionIDSign(unionSign);
            unionMemberService.insert(member);
            apply.setUnionMemberId(member.getId());
            this.updateById(apply);
            main.setUnionMemberNum(CommonUtil.isEmpty(main.getUnionMemberNum()) ? 1 : main.getUnionMemberNum() + 1);
            unionMainService.updateById(main);
            String unionMainKey = RedisKeyUtil.getUnionMainKey(main.getId());
            String unionMemberKey = RedisKeyUtil.getUnionMemberBusIdKey(main.getId(),user.getId());
            this.redisCacheUtil.set(unionMainKey, JSON.toJSONString(main));
            this.redisCacheUtil.set(unionMemberKey, JSON.toJSONString(member));
        }
        String infoKey = RedisKeyUtil.getUnionApplyInfoKey(main.getId(),user.getId());
        this.redisCacheUtil.set(infoKey, JSON.toJSONString(info));
        return data;
    }


    /**
     * 校验必填内容是否为空
     * @param list
     * @param vo
     * @return
     * @throws BaseException
     */
	private boolean checkUnionApplyInfo(List<UnionInfoDict> list, UnionApplyVO vo) throws BaseException{
        for(UnionInfoDict dict : list){
            if(dict.getItemKey().equals("directorName")){
                if(StringUtil.isEmpty(vo.getDirectorName())){
                    throw new ParamException(SAVE,"参数错误","负责人内容不能为空");
                }
            }
            if(dict.getItemKey().equals("directorEmail")){
                if(StringUtil.isEmpty(vo.getDirectorName())){
                    throw new ParamException(SAVE,"参数错误","邮箱内容不能为空");
                }
            }
            if(dict.getItemKey().equals("applyReason")){
                if(StringUtil.isEmpty(vo.getDirectorName())){
                    throw new ParamException(SAVE,"参数错误","申请推荐理由不能为空");
                }
            }
        }
        return true;
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
        apply.setBusConfirmStatus(2);
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
