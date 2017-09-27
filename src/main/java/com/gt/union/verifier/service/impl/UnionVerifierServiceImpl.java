package com.gt.union.verifier.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public void save(UnionVerifier unionVerifier) throws Exception {
        EntityWrapper<UnionVerifier> memberEntityWrapper = new EntityWrapper<UnionVerifier>();
        memberEntityWrapper.eq("del_status",0);
        memberEntityWrapper.eq("phone",unionVerifier.getPhone());
        int count = this.selectCount(memberEntityWrapper);
        if(count > 0){
            throw new ParamException("您输入的手机号码已存在，请重新输入");
        }

        if (StringUtil.getStringLength(unionVerifier.getName()) > 10) {
            throw new BusinessException( "姓名不能超过10个字");
        }
        EntityWrapper<UnionVerifier> memberWrapper = new EntityWrapper<UnionVerifier>();
        memberWrapper.eq("del_status",0);
        memberWrapper.eq("bus_id",unionVerifier.getBusId());
        memberWrapper.eq("name",unionVerifier.getName());
        count = this.selectCount(memberEntityWrapper);
        if(count > 0){
            throw new ParamException("您输入的姓名已存在，请重新输入");
        }
        Object obj = redisCacheUtil.get("verifier:"+unionVerifier.getPhone());
        if(CommonUtil.isEmpty(obj)){
            throw new BusinessException("验证码已失效");
        }
        if(!unionVerifier.getCode().equals(obj)){
            throw new BusinessException("验证码有误");
        }
        UnionVerifier verifier = new UnionVerifier();
        verifier.setDelStatus(CommonConstant.DEL_STATUS_NO);
        verifier.setCreatetime(new Date());
        verifier.setBusId(unionVerifier.getBusId());
        verifier.setName(unionVerifier.getName());
        verifier.setPhone(unionVerifier.getPhone());
        this.insert(verifier);
        String phoneKey = RedisKeyUtil.getVerifyPhoneKey(unionVerifier.getPhone());
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


}
