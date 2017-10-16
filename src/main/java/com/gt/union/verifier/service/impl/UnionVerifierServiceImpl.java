package com.gt.union.verifier.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.verifier.entity.UnionVerifier;
import com.gt.union.verifier.mapper.UnionVerifierMapper;
import com.gt.union.verifier.service.IUnionVerifierService;
import com.gt.union.verifier.vo.UnionVerifierVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * <p>
 * 联盟平台管理人员 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionVerifierServiceImpl extends ServiceImpl<UnionVerifierMapper, UnionVerifier> implements IUnionVerifierService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public void deleteById(Integer id) throws Exception{
        if(id == null){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionVerifier unionVerifier = new UnionVerifier();
        unionVerifier.setId(id);
        unionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(unionVerifier);
    }

    @Override
    public void save(UnionVerifierVO unionVerifier) throws Exception {
        UnionVerifier phoneVerify = this.getByPhone(unionVerifier.getPhone());
        if(phoneVerify != null){
            throw new ParamException("您输入的手机号码已存在，请重新输入");
        }
        int count = this.selectCountByBusIdAndName(unionVerifier.getBusId(),unionVerifier.getName());
        if(count > 0){
            throw new ParamException("您输入的姓名已存在，请重新输入");
        }
        String phoneKey = RedisKeyUtil.getVerifyPhoneKey(unionVerifier.getPhone());
        String obj = redisCacheUtil.get(phoneKey);
        if(CommonUtil.isEmpty(obj)){
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        if(!unionVerifier.getCode().equals(JSON.parse(obj))){
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        UnionVerifier verifier = new UnionVerifier();
        verifier.setDelStatus(CommonConstant.DEL_STATUS_NO);
        verifier.setCreatetime(new Date());
        verifier.setBusId(unionVerifier.getBusId());
        verifier.setName(unionVerifier.getName());
        verifier.setPhone(unionVerifier.getPhone());
        this.insert(verifier);

        redisCacheUtil.remove(phoneKey);
    }

    @Override
    public Page list(Page page, Integer busId) throws Exception{
        if(busId == null){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id",busId);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.setSqlSelect("id AS id,bus_id AS busId,createtime,del_status AS delStatus,third_member_id AS thirdMemberId,phone,`name`");
        return this.selectPage(page,entityWrapper);
    }

	@Override
	public UnionVerifier getByPhone(String phone) {
        EntityWrapper<UnionVerifier> wrapper = new EntityWrapper<UnionVerifier>();
        wrapper.eq("del_status",0);
        wrapper.eq("phone",phone);
		return this.selectOne(wrapper);
	}

    @Override
    public int selectCountByBusIdAndName(Integer busId, String name) {
        EntityWrapper<UnionVerifier> memberWrapper = new EntityWrapper<UnionVerifier>();
        memberWrapper.eq("del_status",0);
        memberWrapper.eq("bus_id",busId);
        memberWrapper.eq("name",name);
        return this.selectCount(memberWrapper);
    }

    @Override
    public boolean checkUnionVerifier(String phone, String name, Integer busId) throws Exception{
        if(StringUtil.isEmpty(name)){
            throw new ParamException("姓名不能为空");
        }
        if(StringUtil.getStringLength(name) > 10){
            throw new ParamException("姓名不可超过10字");
        }
        if(StringUtil.isEmpty(phone)){
            throw new ParamException("验证码不能为空");
        }
        boolean isMatch = Pattern.matches("^1[3|4|5|6|7|8][0-9][0-9]{8}$", phone);
        if(!isMatch){
            throw new ParamException("手机号有误");
        }
        UnionVerifier phoneVerify = this.getByPhone(phone);
        if(phoneVerify != null){
            throw new ParamException("您输入的手机号码已存在，请重新输入");
        }
        int count = this.selectCountByBusIdAndName(busId,name);
        if(count > 0){
            throw new ParamException("您输入的姓名已存在，请重新输入");
        }
        return true;
    }


}
