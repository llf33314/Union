package com.gt.union.api.controller;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-13 14:50
 **/
@Api(description = "用户信息Api")
@RestController
@RequestMapping("/api/user")
public class BusUserApiController {

	private Logger logger = LoggerFactory.getLogger(BusUserApiController.class);

	@Autowired
	private IBusUserService busUserService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private SocketService socketService;

	@ApiOperation(value = "获取商家公众号信息 如果为null，表示没有公众号信息，有则返回商家公众号id", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/wxPublicUser", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult getWxPublicUserByBusId(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			busId = busUser.getPid();
		}
		WxPublicUsers publicUsers = busUserService.getWxPublicUserByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(publicUsers == null ? null : publicUsers.getId());
	}


	@ApiOperation(value = "获取商家公众号二维码链接", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/wxPublicUserQRCode/{publicId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult getWxPublicUserQRCode(HttpServletRequest request,
					  @ApiParam(value = "商家公众号id", name = "publicId", required = true)
					  @PathVariable(value = "publicId") Integer publicId) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		Integer extendId = busId;
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			busId = busUser.getPid();
		}
		String socketKey = PropertiesUtil.getSocketKey() + "qrCode_" + extendId;
		String qrCodeUrl = busUserService.getWxPublicUserQRCode(publicId, busId, extendId);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("qrCodeUrl", qrCodeUrl);
		data.put("qrCodeSocketKey", socketKey);
		return GtJsonResult.instanceSuccessMsg(data);
	}

	@ApiOperation(value = "下载公众号二维码链接", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "qrCodeUrl", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void downloadWXQrCode(HttpServletRequest request,HttpServletResponse response,
								  @ApiParam(value = "公众号二维码链接", name = "url", required = true)
								  @RequestParam(value = "url") String url) throws Exception {
		response.sendRedirect(PropertiesUtil.getMemberUrl() + "/addMember/downQcode.do?url=" + url);
	}

	/**
	 * 关注推送回调
	 * @param request
	 * @param param
	 * @throws Exception
	 */
	@RequestMapping(value = "/79B4DE7C/followCallback", produces = "application/json;charset=UTF-8")
	public void wxPublicUserFollowCallback(HttpServletRequest request,
											  @RequestBody Map<String,Object> param) throws Exception {
		Member member = memberService.getById(CommonUtil.toInteger(param.get("memberId")));
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("nickName", StringUtil.isEmpty(member.getNickname()) ? "未知用户" : member.getNickname());
		data.put("memberId",member.getId());
		data.put("headurl",member.getHeadimgurl());
		data.put("time", DateTimeKit.format(new Date(),"yyyy-MM-dd HH:mm"));
		logger.info("关注回调----------" + JSON.toJSONString(data));
		socketService.socketCommonSendMessage(PropertiesUtil.getSocketKey() + "qrCode_" + CommonUtil.toInteger(param.get("externalId")), JSON.toJSONString(data),"");
	}
}
