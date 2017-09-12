package com.gt.union.verifier.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ParamValidatorUtil;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.verifier.entity.UnionVerifier;
import com.gt.union.verifier.service.IUnionVerifierService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 联盟平台管理人员 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionVerifier")
public class UnionVerifierController {

    private Logger logger = Logger.getLogger(UnionVerifierController.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionVerifierService unionVerifierService;

    @Autowired
    private SmsService smsService;

    @ApiOperation(value = "获取商家的佣金平台管理员列表", notes = "获取商家的佣金平台管理员列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String list(Page<UnionVerifier> page, HttpServletRequest request){
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            Page result = unionVerifierService.list(page,busId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @SysLogAnnotation(op_function = "4", description = "删除佣金平台管理员")
    @ApiOperation(value = "删除佣金平台管理员", notes = "删除佣金平台管理员", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public String deleteById(HttpServletRequest request, @ApiParam(name="id", value = "管理员id", required = true) @PathVariable("id") Integer id){
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            if(user.getPid() != null && user.getPid() != 0){
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            unionVerifierService.deleteById(id);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @SysLogAnnotation(op_function = "2", description = "保存佣金平台管理员")
    @ApiOperation(value = "保存佣金平台管理员", notes = "保存佣金平台管理员", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request
            , @ApiParam(name="unionVerifyMember", value = "平台管理员信息", required = true)
                       @Valid @RequestBody UnionVerifier unionVerifyMember, BindingResult result){
        try{
            ParamValidatorUtil.checkBindingResult(result);
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            unionVerifyMember.setBusId(busId);
            unionVerifierService.save(unionVerifyMember);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @SysLogAnnotation(op_function = "1", description = "获取佣金平台管理员验证码")
    @ApiOperation(value = "获取佣金平台管理员验证码", notes = "获取佣金平台管理员验证码", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/phone/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public String getCodeByPhone(HttpServletRequest request, HttpServletResponse response
            , @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            //生成验证码
            String code = RandomKit.getRandomString(6, 0);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            if (CommonUtil.isNotEmpty(phone)) {
                PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"佣金平台管理员验证码:" + code);
                Map param = new HashMap<String,Object>();
                param.put("reqdata",phoneMessage);
                if(smsService.sendSms(param) == 0){
                    return GTJsonResult.instanceErrorMsg("发送失败").toString();
                }
                redisCacheUtil.set("verifyMember:"+phone , code, 300l);
                return GTJsonResult.instanceSuccessMsg(code).toString();
            }
            return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }
}
