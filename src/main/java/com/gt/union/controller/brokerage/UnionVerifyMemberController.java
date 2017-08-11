package com.gt.union.controller.brokerage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.common.util.SmsUtil;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
	//TODO 方法标签名RESTFUL化
	private static final String UNION_VERIFY_MEMBER = "UnionVerifyMemberController.unionVerifyMember()";
    private static final String DEL_UNION_VERIFY_MEMBER = "UnionVerifyMemberController.delUnionVerifyMember()";
    private static final String SAVE_UNION_VERIFY_MEMBER = "UnionVerifyMemberController.saveUnionVerifyMember()";
    private static final String GET_PHONE_CODE = "UnionVerifyMemberController.getPhoneCode()";
	private Logger logger = Logger.getLogger(UnionVerifyMemberController.class);

	@Autowired
	private IUnionVerifyMemberService unionVerifyMemberService;



	/**
	 * 获取商家的佣金平台管理员列表
	 * @param page
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取商家的佣金平台管理员列表", notes = "获取商家的佣金平台管理员列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionVerifyMember(Page<UnionVerifyMember> page, HttpServletRequest request){
		try {
            BusUser user = SessionUtils.getLoginUser(request); //TODO user没有校验是否为空
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            EntityWrapper<UnionVerifyMember> wrapper = new EntityWrapper<UnionVerifyMember>();
            wrapper.eq("bus_id", busId);
            wrapper.eq("del_status", 0);
            page = unionVerifyMemberService.selectPage(page, wrapper);
            return GTJsonResult.instanceSuccessMsg(page).toString();
//        } catch (BaseException e) {
//            logger.error("", e);
//            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UNION_VERIFY_MEMBER, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	/**
	 * 删除佣金平台管理员
	 * @param request
	 * @param id
	 * @return
	 */
	@SysLogAnnotation(op_function = "4", description = "删除佣金平台管理员")
	@ApiOperation(value = "删除佣金平台管理员", notes = "删除佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String delUnionVerifyMember(HttpServletRequest request, @ApiParam(name="id", value = "管理员id", required = true) @PathVariable("id") Integer id){
		try {
            unionVerifyMemberService.delUnionVerifyMember(id);
            return GTJsonResult.instanceSuccessMsg().toString();
//        } catch (BaseException e) {
//            logger.error("", e);
//            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(DEL_UNION_VERIFY_MEMBER, e.getMessage(),ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


	/**
	 * 保存佣金平台管理员
	 * @param request
	 * @param unionVerifyMember
	 * @return
	 */
	@SysLogAnnotation(op_function = "2", description = "保存佣金平台管理员")
	@ApiOperation(value = "保存佣金平台管理员", notes = "保存佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionVerifyMember(HttpServletRequest request, @ApiParam(name="unionVerifyMember", value = "平台管理员信息", required = true) @RequestBody UnionVerifyMember unionVerifyMember){
		try{
            BusUser user = SessionUtils.getLoginUser(request);//TODO user没有校验是否为空
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionVerifyMember.setBusId(busId);
			unionVerifyMember.setCreatetime(new Date());
			unionVerifyMember.setDelStatus(0);
			unionVerifyMemberService.saveUnionVerifyMember(unionVerifyMember);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE_UNION_VERIFY_MEMBER, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	/**
	 *	获取佣金平台管理员验证码
	 * @param request
	 * @param response
	 * @param phone
	 */
	@SysLogAnnotation(op_function = "1", description = "获取佣金平台管理员验证码")
	@ApiOperation(value = "获取佣金平台管理员验证码", notes = "获取佣金平台管理员验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phoneCode", produces = "application/json;charset=UTF-8")
	public String getPhoneCode(HttpServletRequest request, HttpServletResponse response,
							   @ApiParam(name="phone", value = "手机号", required = true) @RequestParam String phone) {
		try {
            BusUser user = SessionUtils.getLoginUser(request);//TODO user没有校验是否为空
            //生成验证码
            String code = RandomKit.getRandomString(6, 0);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            if (CommonUtil.isNotEmpty(phone)) {
                //TODO 发送短信接口
                SmsUtil.sendMsg(phone, CommonConstant.SMS_UNION_MODEL, busId, "佣金平台管理员验证码:" + code.toString(), user.getName());
            }
            return GTJsonResult.instanceSuccessMsg(code).toString();
//        } catch (BaseException e) {
//            logger.error("", e);
//            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_PHONE_CODE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


}
