package com.gt.union.controller.brokerage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.amqp.entity.PhoneMessage;
import com.gt.union.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.brokerage.UnionVerifyMemberConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
import com.gt.union.service.common.IUnionValidateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 联盟佣金平台管理人员 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionVerifyMember")
public class UnionVerifyMemberController {
	private static final String LIST = "UnionVerifyMemberController.list()";
    private static final String DELETE_ID = "UnionVerifyMemberController.deleteById()";
    private static final String SAVE = "UnionVerifyMemberController.save()";
    private static final String GET_CODE_PHONE = "UnionVerifyMemberController.getCodeByPhone()";
	private Logger logger = Logger.getLogger(UnionVerifyMemberController.class);

	@Autowired
	private IUnionVerifyMemberService unionVerifyMemberService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionValidateService unionValidateService;

	@ApiOperation(value = "获取商家的佣金平台管理员列表", notes = "获取商家的佣金平台管理员列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String list(Page<UnionVerifyMember> page, HttpServletRequest request){
		try {
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
           	Page result = unionVerifyMemberService.list(page,busId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	@SysLogAnnotation(op_function = "4", description = "删除佣金平台管理员")
	@ApiOperation(value = "删除佣金平台管理员", notes = "删除佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String deleteById(HttpServletRequest request, @ApiParam(name="id", value = "管理员id", required = true) @PathVariable("id") Integer id){
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			if(user.getPid() != null && user.getPid() != 0){
				throw new BusinessException(DELETE_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
            unionVerifyMemberService.deleteById(id);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(DELETE_ID, e.getMessage(),ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	@SysLogAnnotation(op_function = "2", description = "保存佣金平台管理员")
	@ApiOperation(value = "保存佣金平台管理员", notes = "保存佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String save(HttpServletRequest request
		, @ApiParam(name="unionVerifyMember", value = "平台管理员信息", required = true)
        @Valid @RequestBody UnionVerifyMember unionVerifyMember, BindingResult result){
		try{
			this.unionValidateService.checkBindingResult(result);
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			unionVerifyMember.setBusId(busId);
			unionVerifyMemberService.save(unionVerifyMember);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
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
					return GTJsonResult.instanceErrorMsg(GET_CODE_PHONE, "发送失败", "发送失败").toString();
				}
				redisCacheUtil.set("verifyMember:"+phone , code, 300l);
				return GTJsonResult.instanceSuccessMsg(code).toString();
			}
			return GTJsonResult.instanceErrorMsg(GET_CODE_PHONE, "电话号码为空", "手机号不能为空").toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_CODE_PHONE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


}
