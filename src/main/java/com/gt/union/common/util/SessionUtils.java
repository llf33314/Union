package com.gt.util;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import com.gt.common.constant.CommonConst;
import com.gt.entity.attendance.AEmployee;
import com.gt.entity.common.power.PowerUser;
import com.gt.entity.member.Member;
import com.gt.entity.user.BusFlow;
import com.gt.entity.user.BusUser;
import com.gt.entity.user.WxPublicUsers;

public class SessionUtils {
	private static final org.apache.log4j.Logger log = Logger
			.getLogger(SessionUtils.class);


	//获取用户bus_user   SESSION的值
	public static BusUser getLoginUser(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(CommonConst.SESSION_BUSINESS_KEY);
			if(obj != null){
				return (BusUser) obj;
			}else{
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};
	//存入 用户bus_user  的值
	@SuppressWarnings("unchecked")
	public static void setLoginUser(HttpServletRequest request, BusUser busUser) {
		try {
			request.getSession().setAttribute(
					CommonConst.SESSION_BUSINESS_KEY, busUser);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * 设置session中的t_wx_public_user微信信息
	 * 
	 * @param request
	 * @return
	 */
	public static void setLoginPbUser(HttpServletRequest request,
			WxPublicUsers wxPublicUsers) {
		try {
			request.getSession().setAttribute(
					CommonConst.SESSION_WXPUBLICUSERS_KEY, wxPublicUsers);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * 获取session中的t_wx_public_user登录用户
	 * 
	 * @param request
	 * @return
	 */
	public static WxPublicUsers getLoginPbUser(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(CommonConst.SESSION_WXPUBLICUSERS_KEY);
			if(obj != null){
				return (WxPublicUsers) obj;
			}else{
				return null;
			}
			
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};

	/**
	 * 设置session中的商家member会员信息
	 * 
	 * @param request
	 * @return
	 */
	public static void setLoginMember(HttpServletRequest request,Member member) {
		try {
			request.getSession().setAttribute(
					"member", member);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * 获取session中的商家会员信息
	 * 
	 * @param request
	 * @return
	 */
	public static Member getLoginMember(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute("member");
			if(obj != null){
				return (Member) obj;
			}else{
				return null;
			}
		
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	};

	@SuppressWarnings("unchecked")
	public static void setLoginAgent(HttpServletRequest request, BusUser busUser) {
		try {
			request.getSession().setAttribute("agent", busUser);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};
	/**
	 * 获取session中的经纪人登录用户
	 * 
	 * @param request
	 * @return
	 */
	public static BusUser getLoginAgent(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute("agent");
			if(obj != null){
				return (BusUser) obj;
			}else{
				return null;
			}
		
			
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};

	/**
	 * 设置session中的权限用户
	 * 
	 * @param request
	 * @return
	 */
	public static void setLoginPowerUser(HttpServletRequest request,
			PowerUser busUser) {
		try {
			request.getSession().setAttribute("session_power_user", busUser);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * 获取session中的权限用户
	 * 
	 * @param request
	 * @return
	 */
	public static PowerUser getLoginPowerUser(HttpServletRequest request) {
		try {
			return (PowerUser) request.getSession().getAttribute(
					"session_power_user");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};
	/**
	 * 设置用户所剩流量
	 * 
	 * @param request
	 * @param busId
	 */
	public static void setLoginUserFlow(HttpServletRequest request,
			List<BusFlow> busFlows) {		
		request.getSession().setAttribute("busFlows", busFlows);
	}

	/**
	 * 获取用户所剩流量
	 * 
	 * @param request
	 * @param busId
	 */
	public static List<BusFlow> getLoginUserFlow(HttpServletRequest request){
		try {
			return  (List<BusFlow>) request.getSession().getAttribute(
					"busFlows");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置session中的微信信息
	 * 
	 * @param request
	 * @return
	 */
	public static void setLoginSignUser(HttpServletRequest request,
			AEmployee employee) {
		try {
			request.getSession().setAttribute("signUser", employee);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};
	/**
	 * 设置session中的微信信息
	 * 
	 * @param request
	 * @return
	 */
	public static AEmployee getLoginSignUser(HttpServletRequest request) {
		try {
			return (AEmployee) request.getSession().getAttribute("signUser");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	};
	/**
	 * 是否关注
	 * @param request
	 * @return
	 */
	public static boolean getIsfollow(HttpServletRequest request) {
		try {
			return (boolean) request.getSession().getAttribute("isfollow");
		}catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return false;
	}

	public static void setIsfollow(HttpServletRequest request,boolean isfollow) {
		try {
			request.getSession().setAttribute("isfollow", isfollow);
		}
		catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 物业——将业主id存入Session
	 * @param request
	 * @return
	 */
	public static Integer getOwnerId(HttpServletRequest request) {
		try {
			return (Integer) request.getSession().getAttribute("ownerId");
		}catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 物业——将业主id从Session中取出
	 * @param request
	 * @param ownerId
	 */
	public static void setOwnerId(HttpServletRequest request,Integer ownerId) {
		try {
			request.getSession().setAttribute("ownerId", ownerId);
		}
		catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 物业——将小区id存入Session
	 * @param request
	 * @return
	 */
	public static Integer getManageId(HttpServletRequest request) {
		try {
			return (Integer) request.getSession().getAttribute("manageId");
		}catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 物业——将小区id从Session中取出
	 * @param request
	 * @param manageId
	 */
	public static void setManageId(HttpServletRequest request,Integer manageId) {
		try {
			request.getSession().setAttribute("manageId", manageId);
		}
		catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 样子——将店铺id存入Session
	 * @param request
	 * @return
	 */
	public static Integer getLookMainId(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute("lookMainId");
			if(obj != null){
				return CommonUtil.toInteger(obj);
			}else{
				return null;
			}
		}catch (Exception e) {
			log.info("getLookMainId:err",e);
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 样子——将店铺id从Session中取出
	 * @param request
	 * @param lookMainId
	 */
	public static void setLookMainId(HttpServletRequest request,Object lookMainId) {
		try {
			request.getSession().setAttribute("lookMainId",lookMainId);

		}catch (Exception e) {
			log.info("setLookMainId:err",e);
			e.printStackTrace();
		}
	}

	/**
	 * 美容——将店铺id存入Session
	 * @param request
	 * @return
	 */
	public static Integer getFacialMainId(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute("facialMainId");
			if(obj != null){
				return CommonUtil.toInteger(obj);
			}else{
				return null;
			}
		}catch (Exception e) {
			log.info("getFacialMainId:err",e);
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 美容——将店铺id从Session中取出
	 * @param request
	 * @param facialMainId
	 */
	public static void setFacialMainId(HttpServletRequest request, Integer facialMainId) {
		try {
			request.getSession().setAttribute("facialMainId",facialMainId);

		}catch (Exception e) {
			log.info("setFacialMainId:err",e);
			e.printStackTrace();
		}
	}
}
