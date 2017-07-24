package com.gt.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.gt.common.constant.CommonConst;
import com.gt.common.constant.Constants;
import com.gt.dao.user.AlipayUserMapper;
import com.gt.dao.user.BusFlowMapper;
import com.gt.dao.user.BusUserMapper;
import com.gt.dao.user.WxPublicUsersMapper;
import com.gt.entity.attendance.AEmployee;
import com.gt.entity.common.power.PowerMain;
import com.gt.entity.common.power.PowerUser;
import com.gt.entity.fangchan.FangchanUserAgent;
import com.gt.entity.member.Member;
import com.gt.entity.member.PublicParameterSet;
import com.gt.entity.user.AlipayUser;
import com.gt.entity.user.BusFlow;
import com.gt.entity.user.BusUser;
import com.gt.entity.user.WxPublicUsers;
import com.gt.service.common.dict.DictService;
import com.gt.service.common.power.PowerService;
import com.gt.service.user.wxpublicusers.WxPublicUserService;
import com.gt.util.ftp.ContinueFTP;
import com.gt.wx.api.ComponentAPI;
import com.gt.wx.api.MaterialAPI;
import com.gt.wx.api.SnsAPI;
import com.gt.wx.api.TicketAPI;
import com.gt.wx.api.TokenAPI;
import com.gt.wx.entity.Token;
import com.gt.wx.entity.UploadResult;
import com.gt.wx.entity.component.AuthorizerAccessToken;
import com.gt.wx.entity.component.ComponentAccessToken;
import com.gt.wx.util.JsUtil;
import com.gt.wx.util.WxCardSign;
import com.gt.wx.util.WxConstants;
import com.gt.wx.util.WxKit;
import com.gt.wx.util.pay.PayCommonUtil;

/**
 * 获取登录用户信息
 * 
 * @author Administrator
 * 
 */
public class CommonUtil {
	private static final org.apache.log4j.Logger log = Logger
			.getLogger(CommonUtil.class);

	public static String webRealPath;

	private static ComponentAPI componentAPI;

	@SuppressWarnings("unused")
	private static List<Map<String, Object>> dic = new ArrayList<Map<String, Object>>();

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(
			ApplicationContext applicationContext) {
		CommonUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取session中的登录用户
	 * 
	 * @param request
	 * @return
	 */
	public static BusUser getLoginUser(HttpServletRequest request) {
		try {
			return (BusUser) request.getSession().getAttribute(
					CommonConst.SESSION_BUSINESS_KEY);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};

	/**
	 * 获取session中的登录用户
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void setLoginUser(HttpServletRequest request, BusUser busUser) {
		try {
			Integer pid = busUser.getPid();
			if (pid == 0 || pid == null) {
				busUser.setDays(DateTimeKit.daysBetween(new Date(),
						busUser.getEndTime()));
				String jsonStr = JedisUtil.get("dict");
				Map<String, Map<String, String>> map = JSONObject
						.fromObject(jsonStr);
				busUser.setLevelDesc(map.get(Constants.USER_TYPE_CODE).get(
						busUser.getLevel() + ""));
				request.getSession().setAttribute(
						CommonConst.SESSION_BUSINESS_KEY, busUser);
			} else {
				request.getSession().setAttribute(
						CommonConst.SESSION_BUSINESS_KEY, busUser);
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	/**
	 * 设置用户所剩流量
	 * 
	 * @param request
	 * @param busId
	 */
	public static void setLoginUserFlow(HttpServletRequest request,
			Integer busId) {
		BusFlowMapper busFlowMapper = applicationContext
				.getBean(BusFlowMapper.class);
		List<BusFlow> busFlows = busFlowMapper.getBusFlowsByUserId(busId);
		request.getSession().setAttribute("busFlows", busFlows);
	}

	/**
	 * 返回json数据到客户端
	 * 
	 * @param response
	 * @param obj
	 * @throws IOException
	 */
	public static void write(HttpServletResponse response, Object obj)
			throws IOException {
		if (obj instanceof List || obj instanceof Object[]) {
			response.getWriter().print(JSONArray.toJSON(obj));
		} else if (obj instanceof Boolean || obj instanceof Integer
				|| obj instanceof String || obj instanceof Double) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", obj);
			response.getWriter().print(JSONObject.fromObject(result));
		} else {
			response.getWriter().print(JSONObject.fromObject(obj));
		}
		response.getWriter().flush();
		response.getWriter().close();
	}

	/**
	 * 获取图片存放域名
	 * 
	 * @param request
	 */
	public static void getImageHomePath(HttpServletRequest request) {
		request.setAttribute("resourceUrl", request.getServletContext()
				.getAttribute("resourceUrl"));
	}

	/**
	 * 获取session中的登录用户
	 * 
	 * @param request
	 * @return
	 */
	/**
	 * @param request
	 * @param busUser
	 */
	public static void setLoginUser(HttpSession session, BusUser busUser) {
		try {
			busUser.setDays(DateTimeKit.daysBetween(new Date(),
					busUser.getEndTime()));
			session.setAttribute(CommonConst.SESSION_BUSINESS_KEY, busUser);
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
	 * 设置商家会员信息
	 *  
	 * @param request
	 * @return
	 */
	public static void setLoginMember(HttpServletRequest request, Member member) {
		try {
			if (CommonUtil.isEmpty(member)||CommonUtil.isEmpty(member.getId())) {// 充值
				request.getSession().setAttribute("member", member);
			} else {
//				Integer publicId = member.getPublicId();
//				member.setPass(CommonUtil.isPassByBusUser(publicId));
				Integer busId = member.getBusid();
				member.setPass(CommonUtil.isPassByBusUserVer2(busId));
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		request.getSession().setAttribute("member", member);
	};

	/**
	 * 设置商家会员信息
	 * 
	 * @param request
	 * @return
	 */
	public static Member getLoginMember(HttpServletRequest request) {
		try {
			return (Member) request.getSession().getAttribute("member");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * 获取公众号下的会员，不是该公众号的会员返回null
	 * 
	 * @param request
	 * @return
	 */
	public static Member getMemberByPubUser(HttpServletRequest request,
			Integer publicId) {
		try {
			Member member = (Member) request.getSession()
					.getAttribute("member");
			if (member != null && member.getPublicId().equals(publicId)) {
				return member;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	};
	
	
	/**
	 * 获取商家的会员，不是该商家的会员返回null
	 * 
	 * @param request
	 * @return
	 */
	public static Member getMemberByBusId(HttpServletRequest request,
			Integer busId) {
		try {
			Member member = (Member) request.getSession()
					.getAttribute("member");
			if (isNotEmpty(member) && member.getBusid().equals(busId)) {
				return member;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 设置商家会员信息(bugger)
	 *  
	 * @param request
	 * @return
	 */
	public static void setLoginMember1(HttpServletRequest request, Member member) {
		try {
			if (CommonUtil.isEmpty(member.getId())) {// 充值
				request.getSession().setAttribute("member", member);
			} else {
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		request.getSession().setAttribute("member", member);
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
			return null;
		}
	};

	/**
	 * 获取session中的登录用户
	 * 
	 * @param request
	 * @return
	 */
	public static WxPublicUsers getLoginPbUser(HttpServletRequest request) {
		try {
			return (WxPublicUsers) request.getSession().getAttribute(
					CommonConst.SESSION_WXPUBLICUSERS_KEY);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};
	/**
	 * 获取session中的经纪人登录用户
	 * 
	 * @param request
	 * @return
	 */
	public static BusUser getLoginAgent(HttpServletRequest request) {
		try {
			return (BusUser) request.getSession().getAttribute("agent");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};

	/**
	 * 设置session中的经纪人登录用户
	 * 
	 * @param request
	 * @return
	 */
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
	 * 判断对象是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		boolean b = false;
		try {
			if (obj == null || "".equals(obj)) {
				b = true;
			} else {
				b = false;
			}
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 判断对象是否不为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		boolean b = false;
		try {
			if (obj == null || "".equals(obj)) {
				b = false;
			} else {
				b = true;
			}
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 转Integer
	 * 
	 * @param obj
	 */
	public static Integer toInteger(Object obj) {
		try {
			if (!isEmpty(obj)) {
				return Integer.parseInt(obj.toString());
			} else {
				throw new Exception("对象为空，转换失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转String
	 * 
	 * @param obj
	 */
	public static String toString(Object obj) {
		try {
			if (!isEmpty(obj)) {
				return obj.toString();
			} else {
				throw new Exception("对象为空，转换失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转Double
	 * 
	 * @param obj
	 */
	public static Double toDouble(Object obj) {
		try {
			if (!isEmpty(obj)) {
				return Double.parseDouble(obj.toString());
			} else {
				throw new Exception("对象为空，转换失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 校验是否是double数据
	 * 
	 */
	public static boolean isDouble(Object obj) {
		try {
			Double.parseDouble(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除正文多余的图片以及上传错误的图片
	 * 
	 * @param userName
	 * @param timetext
	 * @param urls
	 */
	public static void urlphone(String userName, String timetext, String urls) {
		String path = PropertiesUtil.getResImagePath()
				+ "/2/"
				+ userName
				+ "/"
				+ PropertiesUtil.IMAGE_FOLDER_TYPE_5
				+ "/"
				+ DateTimeKit.getDateTime(new Date(),
						DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD) + "/"
				+ timetext; // request.getSession().getServletContext().getRealPath("/")+"upload/editor/"+userName+"/";
		File dir = new File(path);
		// 如果文件名存在，继续
		if (dir.exists()) {
			File[] files = dir.listFiles();
			String url[] = urls.split(",");
			int num = 0;
			for (int i = 0; i < files.length; i++) {
				String fileurl = files[i].getName();
				String filename = files[i].getName().substring(0,
						files[i].getName().lastIndexOf('.'));
				num = 0;
				for (int j = 0; j < url.length; j++) {
					String urld = url[j];
					if (filename == urld || filename.equals(urld)) {
						num = 1;
					}
				}
				if (num == 0) {
					File file = new File(path + "/" + fileurl);
					file.delete();// 删除图片
				}
			}
			if (urls.equals("")) {
				File file = new File(path);
				file.delete();// 删除文件夹
			}
		}

	}

	/**
	 * 获取webcontent的值，截取，保存的值，去掉redis路劲的值
	 * 
	 * @param webcontent
	 */
	public static void webcontent(String webcontent, String userName) {
		String path = "/image/2/"
				+ userName
				+ "/"
				+ PropertiesUtil.IMAGE_FOLDER_TYPE_5
				+ "/"
				+ DateTimeKit.getDateTime(new Date(),
						DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD) + "/";
		String[] list = webcontent.split(path);
		for (int i = 1; i < list.length; i++) {
			String xwebcontet = list[i];
			String urlName = xwebcontet.substring(0, 32);
			String url = PropertiesUtil.getResourceUrl() + path + urlName
					+ ".jpg";
			JedisUtil.mapdel("web_content", url);
			String url1 = PropertiesUtil.getResourceUrl() + path + urlName
					+ ".gif";
			JedisUtil.mapdel("web_content", url1);
			String url2 = PropertiesUtil.getResourceUrl() + path + urlName
					+ ".png";
			JedisUtil.mapdel("web_content", url2);
			String url3 = PropertiesUtil.getResourceUrl() + path + urlName
					+ ".jpeg";
			JedisUtil.mapdel("web_content", url3);
		}
	}

	/**
	 * 图文保存，返回信息
	 * 
	 * @param multipartFile
	 * @param wxPublicUsers
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map fileUpload(MultipartFile multipartFile,
			WxPublicUsers wxPublicUsers) {
		Map map = new HashMap();
		String userName = wxPublicUsers.getUserName();
		String originalFilename = multipartFile.getOriginalFilename();
		// 后缀
		String suffix = originalFilename.substring(originalFilename
				.lastIndexOf("."));
		String phonejsp = originalFilename.substring(originalFilename
				.lastIndexOf(".") + 1);
		// 文件大小
		Integer Size = Integer
				.parseInt(String.valueOf(multipartFile.getSize()));
		// 判断上传图片是否是支持的格式

		String path = PropertiesUtil.getResImagePath()
				+ "/2/"
				+ userName
				+ "/"
				+ PropertiesUtil.IMAGE_FOLDER_TYPE_4
				+ "/"
				+ DateTimeKit.getDateTime(new Date(),
						DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD) + "/jietu/";

		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		Long time = System.currentTimeMillis();
		path += MD5Util.getMD5(time
				+ originalFilename.substring(0,
						originalFilename.lastIndexOf(".")))
				+ suffix;
		byte[] bytes;
		try {
			bytes = multipartFile.getBytes();
			InputStream is = new ByteArrayInputStream(bytes);
			BufferedImage bufimg = ImageIO.read(is);
			ImageIO.write(bufimg, phonejsp, new File(path));
			ContinueFTP myFtp = new ContinueFTP();
			try {
				myFtp.upload(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			is.close();
			String url = "/2/"
					+ userName
					+ "/"
					+ PropertiesUtil.IMAGE_FOLDER_TYPE_4
					+ "/"
					+ DateTimeKit.getDateTime(new Date(),
							DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD)
					+ "/jietu/"
					+ MD5Util.getMD5(time
							+ originalFilename.substring(0,
									originalFilename.lastIndexOf(".")))
					+ suffix;
			map.put("reTurn", "1");
			map.put("message", url);// 保存路径

		} catch (IOException e) {
			e.printStackTrace();
			map.put("reTurn", "0");
			map.put("message", "抛出异常");
		}
		return map;

	}
	
	
	
	/**
	 * 图文保存，返回信息
	 * 
	 * @param multipartFile
	 * @param wxPublicUsers
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map fileUploadByBusUser(MultipartFile multipartFile,
			BusUser busUser) {
		Map map = new HashMap();
		String userName = busUser.getId().toString();
		String originalFilename = multipartFile.getOriginalFilename();
		// 后缀
		String suffix = originalFilename.substring(originalFilename
				.lastIndexOf("."));
		String phonejsp = originalFilename.substring(originalFilename
				.lastIndexOf(".") + 1);
		// 文件大小
		Integer Size = Integer
				.parseInt(String.valueOf(multipartFile.getSize()));
		// 判断上传图片是否是支持的格式

		String path = PropertiesUtil.getResImagePath()
				+ "/2/"
				+ userName
				+ "/"
				+ PropertiesUtil.IMAGE_FOLDER_TYPE_4
				+ "/"
				+ DateTimeKit.getDateTime(new Date(),
						DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD) + "/jietu/";

		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		Long time = System.currentTimeMillis();
		path += MD5Util.getMD5(time
				+ originalFilename.substring(0,
						originalFilename.lastIndexOf(".")))
				+ suffix;
		byte[] bytes;
		try {
			bytes = multipartFile.getBytes();
			InputStream is = new ByteArrayInputStream(bytes);
			BufferedImage bufimg = ImageIO.read(is);
			ImageIO.write(bufimg, phonejsp, new File(path));
			ContinueFTP myFtp = new ContinueFTP();
			try {
				myFtp.upload(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			is.close();
			String url = "/image/2/"
					+ userName
					+ "/"
					+ PropertiesUtil.IMAGE_FOLDER_TYPE_4
					+ "/"
					+ DateTimeKit.getDateTime(new Date(),
							DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD)
					+ "/jietu/"
					+ MD5Util.getMD5(time
							+ originalFilename.substring(0,
									originalFilename.lastIndexOf(".")))
					+ suffix;
			map.put("reTurn", "1");
			map.put("message", url);// 保存路径

		} catch (IOException e) {
			e.printStackTrace();
			map.put("reTurn", "0");
			map.put("message", "抛出异常");
		}
		return map;

	}

	

	/**
	 * 根据网络图片URL返回信息
	 * 
	 * @param imgurl
	 * @return
	 */
	public static BufferedImage getBufferedImage(String imgurl) {
		URL url = null;
		InputStream is = null;
		BufferedImage img = null;
		try {
			url = new URL(imgurl);
			is = url.openStream();
			img = ImageIO.read(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}

	/**
	 * 处理webcontent数据，生成新的webcontent的数据
	 * 
	 * @param webcontent
	 * @param wxPublicUsers
	 * @return
	 * @throws Exception
	 */
	public static String newwebcontent(String webcontent,
			WxPublicUsers wxPublicUsers) throws Exception {
		String path = "<img";
		// 分组
		String[] list = webcontent.split(path);
		String userName = wxPublicUsers.getUserName();
		String newwebcontent;
		if (list.length > 1) {
			newwebcontent = list[0] + "<img ";
		} else {
			newwebcontent = list[0];
		}
		for (int i = 1; i < list.length; i++) {
			// 再次截取分组
			String xwebcontet = list[i].trim();// 去除空格
			String imagePatternStr = "src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
			Pattern imagePattern = Pattern.compile(imagePatternStr,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = imagePattern.matcher(xwebcontet);
			String imageFragmentURL = null;
			while (matcher.find()) {
				// img整个标签
				String imageFragment = matcher.group(0);
				imageFragmentURL = matcher.group(1);
				String imagePatternStr1 = "wx\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
				Pattern imagePattern1 = Pattern.compile(imagePatternStr1,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher1 = imagePattern1.matcher(imageFragment);
				String s = imageFragmentURL.substring(imageFragmentURL
						.lastIndexOf(".") + 1);
				if (matcher1.find() == false
						&& imageFragmentURL.substring(imageFragmentURL
								.lastIndexOf(".") + 1) != "gif") {
					String path1 = PropertiesUtil.getResImagePath()
							+ "/2/"
							+ userName
							+ "/"
							+ PropertiesUtil.IMAGE_FOLDER_TYPE_3
							+ "/"
							+ DateTimeKit.getDateTime(new Date(),
									DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD)
							+ "/"
							+ System.currentTimeMillis()
							+ imageFragmentURL.substring(
									imageFragmentURL.lastIndexOf("."),
									imageFragmentURL.length());
					File file = new File(PropertiesUtil.getResImagePath()
							+ "/2/"
							+ userName
							+ "/"
							+ PropertiesUtil.IMAGE_FOLDER_TYPE_3
							+ "/"
							+ DateTimeKit.getDateTime(new Date(),
									DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMMDD));
					if (!file.exists()) {
						file.mkdirs();
					}
					URLConnectionDownloader.download(imageFragmentURL, path1);

					ComponentAccessToken componentAccessToken = componentAPI
							.api_component_token();
					AuthorizerAccessToken token = componentAPI
							.api_authorizer_token(componentAccessToken
									.getComponent_access_token(),
									WxConstants.COMPONENT_APPID, wxPublicUsers
											.getAppid(), wxPublicUsers
											.getAuthRefreshToken());
					// String appid = "wx44cae927187fe954";
					// String appsecrect = "8554a16b85932df76393c08fb8bac757";
					// Token token = TokenAPI.token(appid, appsecrect);
					// UploadResult result = MaterialAPI.uploadMedia(
					// token.getAccess_token(), new File(path1));
					UploadResult result = MaterialAPI
							.uploadMedia(token.getAuthorizer_access_token(),
									new File(path1));

					if (i == list.length - 1) {
						newwebcontent += "wx=\"" + result.getUrl() + "\""
								+ list[i];
					} else {
						newwebcontent += "wx=\"" + result.getUrl() + "\""
								+ list[i] + "<img ";
					}
				} else {
					if (i != (list.length - 1)) {
						newwebcontent += list[i] + "<img ";
					} else {
						newwebcontent += list[i];
					}
				}

				break;
			}
		}
		return newwebcontent;
	}

	/**
	 * 是否为手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	public static String getOrderCode() {
		return "ED" + new Date().getTime();
	}

	/**
	 * 获取会员订单号
	 * 
	 * @return
	 */
	public static String getMEOrderCode() {
		return "ME" + new Date().getTime();
	}

	/**
	 * 设置用户选择字段信息
	 * 
	 * @param request
	 * @param menus
	 */
	public static void setBusDict(HttpServletRequest request,
			Map<String, Map<String, Object>> menus) {
		request.getSession().setAttribute("busDict", menus);
	}

	/**
	 * 获取用户选择字段信息
	 * 
	 * @param request
	 * @return d
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, Object>> getBusDict(
			HttpServletRequest request) {
		return (Map<String, Map<String, Object>>) request.getSession()
				.getAttribute("busDict");
	}

	private final static double PI = 3.14159265358979323; // 圆周率
	private final static double R = 6371229; // 地球的半径

	/**
	 * 获取两点间的距离
	 * 
	 * @param longt1
	 *            经度1
	 * @param lat1
	 *            纬度1
	 * @param longt2
	 *            经度2
	 * @param lat2
	 *            纬度2
	 * @return
	 */
	public static double getDistance(double longt1, double lat1, double longt2,
			double lat2) {
		double x, y, distance;
		x = (longt2 - longt1) * PI * R
				* Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		y = (lat2 - lat1) * PI * R / 180;
		distance = Math.hypot(x, y);
		if (distance > 0) {
			return distance;
		} else {
			return 0.0;
		}
	}

	public static SortedMap<String, String> getWxParams(
			WxPublicUsers publicUsers, HttpServletRequest request) {
		String appid = publicUsers.getAppid();
		if (isEmpty(componentAPI)) {
			componentAPI = CommonUtil.getApplicationContext().getBean(
					ComponentAPI.class);
		}
		ComponentAccessToken component_access_token = componentAPI
				.api_component_token();
		AuthorizerAccessToken token = componentAPI.api_authorizer_token(
				component_access_token.getComponent_access_token(),
				WxConstants.COMPONENT_APPID, appid,
				publicUsers.getAuthRefreshToken());
		String url = "http://"
				+ request.getServerName() // 服务器地址
				+ request.getContextPath() // 项目名称
				+ request.getServletPath() // 请求页面或其他地址
				+ (CommonUtil.isEmpty(request.getQueryString()) ? "" : "?"
						+ request.getQueryString()); // 参数
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("appid", appid);
		String timestamp = WxKit.create_timestamp();
		signParams.put("timestamp", timestamp);
		String nonceStr = PayCommonUtil.CreateNoncestr();
		String ticket = "";
		ticket = TicketAPI.ticketGetticket(appid,
				token.getAuthorizer_access_token(), "jsapi").getTicket();
		String signature = JsUtil.generateConfigSignature(nonceStr, ticket,
				timestamp, url);
		signParams.put("nonce_str", nonceStr);
		signParams.put("signature", signature);
		request.setAttribute("record", signParams);
		return signParams;
	}


	/**
	 * 获取cardExt
	 * 
	 * @param publicUsers
	 * @param request
	 */
	public static void getCardExt(WxPublicUsers publicUsers,
			HttpServletRequest request) {
		String appid = publicUsers.getAppid();
		if (isEmpty(componentAPI)) {
			componentAPI = CommonUtil.getApplicationContext().getBean(
					ComponentAPI.class);
		}
		ComponentAccessToken component_access_token = componentAPI
				.api_component_token();
		AuthorizerAccessToken token = componentAPI.api_authorizer_token(
				component_access_token.getComponent_access_token(),
				WxConstants.COMPONENT_APPID, appid,
				publicUsers.getAuthRefreshToken());
		String url = "http://"
				+ request.getServerName() // 服务器地址
				+ request.getContextPath() // 项目名称
				+ request.getServletPath() // 请求页面或其他地址
				+ (CommonUtil.isEmpty(request.getQueryString()) ? "" : "?"
						+ request.getQueryString()); // 参数

		// String appid="wx944f89a2edf41226";
		// String cardId="p7Sxgt91qb6c2JuhWEfqVQf0crL4";
		// Token token=TokenAPI.token("wx944f89a2edf41226",
		// "3cedf26a1aa767b0a07570f43d81005b");
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("appid", appid);
		String timestamp = WxKit.create_timestamp();
		String nonceStr = PayCommonUtil.CreateNoncestr();
		signParams.put("timestamp", timestamp);
		signParams.put("nonce_str", nonceStr);
		String ticket = "";
		ticket = TicketAPI.ticketGetticket(appid,
				token.getAuthorizer_access_token(), "wx_card").getTicket();
		String signature = JsUtil.generateConfigSignature(nonceStr, ticket,
				timestamp, url);
		signParams.put("signature", signature);
		SortedMap<String, String> cardExtObj = new TreeMap<String, String>();
		cardExtObj.put("nonce_str", nonceStr);
		cardExtObj.put("timestamp", timestamp);
		cardExtObj.put("signature", signature);
		String str = com.alibaba.fastjson.JSONObject.toJSONString(cardExtObj);
		signParams.put("cardExt", str);
		signParams.put("cardId", request.getParameter("cardId"));
		request.setAttribute("record", signParams);
	}

	/**
	 * 拉取适用卡券列表并获取用户选择信息JS参数
	 * 
	 * @param publicUsers
	 * @param request
	 */
	public static void getChooseCardParams(WxPublicUsers publicUsers,
			HttpServletRequest request) {

		String url = "http://"
				+ request.getServerName() // 服务器地址
				+ request.getContextPath() // 项目名称
				+ request.getServletPath() // 请求页面或其他地址
				+ (CommonUtil.isEmpty(request.getQueryString()) ? "" : "?"
						+ request.getQueryString()); // 参数
		String shopId = request.getParameter("shopId");
		String cardType = request.getParameter("cardType");
		// String cardId=request.getParameter("cardId");
		String appid = "wx944f89a2edf41226";
		String cardId = "p7Sxgt91qb6c2JuhWEfqVQf0crL4";
		Token token = TokenAPI.token("wx944f89a2edf41226",
				"3cedf26a1aa767b0a07570f43d81005b");
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("appid", appid);
		String timestamp = WxKit.create_timestamp();
		String nonceStr = PayCommonUtil.CreateNoncestr();
		signParams.put("timestamp", timestamp);
		signParams.put("nonce_str", nonceStr);
		String ticket = "";
		ticket = TicketAPI.ticketGetticket(appid, token.getAccess_token(),
				"wx_card").getTicket();
		String signature = JsUtil.generateConfigSignature(nonceStr, ticket,
				timestamp, url);
		WxCardSign signer = new WxCardSign();
		signer.AddData(ticket);// api_ticket
		signer.AddData(timestamp);// timestamp

		signParams.put("signature", signature);
		signParams.put("cardType", cardType);
		if (isNotEmpty(cardId)) {
			signer.AddData(cardId);// card_id
			signParams.put("cardId", cardId);
		}
		if (isNotEmpty(shopId)) {
			signParams.put("shopId", shopId);
		}
		if (isNotEmpty(request.getParameter("code"))) {
			signer.AddData(request.getParameter("code"));// code
		}
		if (isNotEmpty(request.getParameter("openid"))) {
			signer.AddData(request.getParameter("openid"));// openid
		}
		signer.AddData(nonceStr);// nonce_str
		signParams.put("cardSign", signer.GetSignature());
		request.setAttribute("wxCardParams", signParams);
	}

	/**
	 * 移除map中为空的项
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, Object> removeEmptyKey(Map<String, Object> map) {
		List<String> keyLs = new ArrayList<>();
		for (String key : map.keySet()) {
			if (isEmpty(map.get(key))) {
				keyLs.add(key);
			}
		}
		for (String string : keyLs) {
			map.remove(string);
		}
		return map;
	}

	/**
	 * 是否为正整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断手机营业商
	 * 
	 * @param phone_number
	 * @return flag 运营商 3：电信 1 移动 2 联通 0 未知号码
	 */
	public static int matchesPhoneNumber(String phone_number) {
		String cm = "^((13[4-9])|(147)|(15[0-2,7-9])|(18[2-3,7-8]))\\d{8}$";
		String cu = "^((13[0-2])|(145)|(15[5-6])|(186))\\d{8}$";
		String ct = "^((133)|(153)|(18[0,1,9]))\\d{8}$";

		int flag = 0;
		if (phone_number.matches(cm)) {
			flag = 1;
		} else if (phone_number.matches(cu)) {
			flag = 2;
		} else if (phone_number.matches(ct)) {
			flag = 3;
		} else {
			flag = 4;
		}
		return flag;
	}

	/**
	 * 获取IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * Object转成String
	 * 
	 * @param obj
	 * @return
	 */
	public static String getStr(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * 判断是否是日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (Exception e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}

	/**
	 * 格式化字符串
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(String format, Object... args) {
		String str = null;
		str = String.format(format, args);
		return str;
	}

	/**
	 * 两数相加
	 * 
	 * @return
	 */
	public static Double add(Double number1, Object number2) {
		Double number = toDouble(number2);
		if (isNotEmpty(number2)) {
			return number1 + number;
		} else {
			return number1;
		}
	}

	/**
	 * 转换一个xml格式的字符串到json格式
	 * 
	 * @param xml
	 *            xml格式的字符串
	 * @return 成功返回json 格式的字符串;失败反回null
	 */
	@SuppressWarnings("unchecked")
	public static String xml2JSON(String xml) {
		JSONObject obj = new JSONObject();
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(is);
			Element root = doc.getRootElement();
			obj.put(root.getName(), iterateElement(root));
			return obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 一个迭代方法
	 * 
	 * @param element
	 *            : org.jdom.Element
	 * @return java.util.Map 实例
	 */
	@SuppressWarnings("unchecked")
	private static Map iterateElement(Element element) {
		List jiedian = element.getChildren();
		Element et = null;
		Map obj = new HashMap();
		List list = null;
		for (int i = 0; i < jiedian.size(); i++) {
			list = new LinkedList();
			et = (Element) jiedian.get(i);
			if (et.getTextTrim().equals("")) {
				if (et.getChildren().size() == 0)
					continue;
				if (obj.containsKey(et.getName())) {
					list = (List) obj.get(et.getName());
				}
				list.add(iterateElement(et));
				obj.put(et.getName(), list);
			} else {
				if (obj.containsKey(et.getName())) {
					list = (List) obj.get(et.getName());
				}
				list.add(et.getTextTrim());
				obj.put(et.getName(), list);
			}
		}
		return obj;
	}

	/**
	 * 卡号加密
	 * 
	 * @param key
	 * @param cardNo
	 * @return
	 */
	public static String encryption(String key, String cardNo) {
		Charset charset = Charset.forName("UTF-8");
		byte[] b = cardNo.getBytes(charset);
		for (int i = 0, size = b.length; i < size; i++) {
			for (byte keyBytes0 : key.getBytes(charset)) {
				b[i] = (byte) (b[i] ^ keyBytes0);
			}
		}
		return new String(b);
	}

	/**
	 * 解密
	 * 
	 * @param key
	 * @param cardNo
	 * @return
	 */
	public static String decrypt(String key, String cardNo) {
		Charset charset = Charset.forName("UTF-8");
		byte[] e = cardNo.getBytes(charset);
		byte[] dee = e;
		for (int i = 0, size = e.length; i < size; i++) {
			for (byte keyBytes0 : key.getBytes(charset)) {
				e[i] = (byte) (dee[i] ^ keyBytes0);
			}
		}
		return new String(e);
	}

	public static String getpath(HttpServletRequest request) {
		String url = "http://"
				+ request.getServerName() // 服务器地址
				+ request.getContextPath() // 项目名称
				+ request.getServletPath() // 请求页面或其他地址
				+ (CommonUtil.isEmpty(request.getQueryString()) ? "" : "?"
						+ request.getQueryString()); // 参数
		return url;
	}

	/**
	 * 获取卡号 截取一位 是生成条形码13位
	 * 
	 * @return
	 */
	public static String getCode() {
		Long date = new Date().getTime();
		String cardNo = date.toString().substring(1);
		return cardNo;
	}

	/**
	 * 获取推荐码 6位
	 * 
	 * @return
	 */
	public static String getNominateCode() {
		StringBuffer buf = new StringBuffer(
				"a,b,c,d,e,f,g,h,i,g,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z");
		buf.append(",1,2,3,4,5,6,7,8,9,0");
		String[] arr = buf.toString().split(",");
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			Integer count = arr.length;
			int a = random.nextInt(count);
			sb.append(arr[a]);
		}
		return sb.toString();
	}

	/**
	 * 获取推荐码 6位
	 * 
	 * @return
	 */
	public static String getPhoneCode() {
		StringBuffer buf = new StringBuffer("1,2,3,4,5,6,7,8,9,0");
		String[] arr = buf.toString().split(",");
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			Integer count = arr.length;
			int a = random.nextInt(count);
			sb.append(arr[a]);
		}
		return sb.toString();
	}

	/**
	 * 获取4位随机码
	 * 
	 * @return
	 */
	public static String get4RandomCode() {
		StringBuffer buf = new StringBuffer("1,2,3,4,5,6,7,8,9,0");
		String[] arr = buf.toString().split(",");
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			Integer count = arr.length;
			int a = random.nextInt(count);
			sb.append(arr[a]);
		}
		return sb.toString();
	}

	/**
	 * 判断支付用户是否是特性用户
	 * 
	 * @param mch_id
	 * @return
	 */
	public static boolean isSpecial(String mch_id) {
		DictService dictService = getApplicationContext().getBean(
				DictService.class);
		SortedMap<String, Object> map = dictService.getDict("1096");
		return map.containsKey(mch_id);
	}

	public static String Blob2String(Object obj) {
		String string = null;
		try {
			if (obj == null || obj.equals("")) {
				return "";
			}
			byte[] bytes = (byte[]) obj;
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}

	/**
	 * 页面授权
	 * 
	 * @param response
	 * @param wxPublicUsers
	 * @param url
	 * @param snsapi_userinfo
	 *            false:只能获取openid,true:可获取头像、昵称等信息
	 * @param params
	 *            业务参数，如活动ID 可为空
	 * @throws Exception
	 */
	public static void userGrant(HttpServletResponse response,
			WxPublicUsers wxPublicUsers, String url, boolean snsapi_userinfo,
			Map<String, Object> params) throws Exception {
		StringBuffer urlStr = new StringBuffer(url + "?strTime="
				+ System.currentTimeMillis());
		String appid = "";
		if (isNotEmpty(params)) {
			for (Entry<String, Object> entry : params.entrySet()) {
				urlStr.append("&" + entry.getKey() + "=" + entry.getValue());
			}
		}
		if (wxPublicUsers.getServiceTypeInfo() == 2) {// 服务号
			appid = wxPublicUsers.getAppid();
		} else {
			appid = PropertiesUtil.getUserGrantCommonAppid();
		}
		response.sendRedirect(SnsAPI.connectOauth2Authorize(appid,
				urlStr.toString(), snsapi_userinfo, "",
				WxConstants.COMPONENT_APPID));
	}

	/**
	 * 判断权限
	 * 
	 * @param response
	 * @param wxPublicUsers
	 * @param url
	 * @param snsapi_userinfo
	 *            false:只能获取openid,true:可获取头像、昵称等信息
	 * @param params
	 *            业务参数，如活动ID 可为空
	 * @throws Exception
	 */
	public static void powerJudge(HttpServletRequest request,
			HttpServletResponse response, WxPublicUsers wxPublicUsers,
			Integer model) throws Exception {
		// PowerService
		// powerService=applicationContext.getBean(PowerService.class);
		// PowerMain
		// powerMain=powerService.findPowerMainByBusId(wxPublicUsers.getBusUserId());
		// if(CommonUtil.isNotEmpty(powerMain)&&CommonUtil.isNotEmpty(powerMain.getTempOptions())&&powerMain.getTempOptions().size()>0){//
		// boolean flag=false;
		// for (int i = 0; i < powerMain.getTempOptions().size(); i++) {
		// if(powerMain.getTempOptions().get(i)==model){
		// flag=true;
		// break;
		// }
		// }
		//
		// if(flag){
		// PowerUser busUser =getLoginPowerUser(request);
		// if(CommonUtil.isEmpty(busUser)||!busUser.getBusId().equals(wxPublicUsers.getBusUserId())){
		// String url=getpath(request);
		// request.setAttribute("url", url);
		// request.setAttribute("powerMain", powerMain);
		// List<Map<String, Object>>
		// fields=powerService.findFieldList(powerMain.getBusId());
		// request.setAttribute("fields", fields);
		// request.getRequestDispatcher("/jsp/merchants/power/phone_login.jsp")
		// .forward(request,response);
		// }
		// }
		// }

	}

	/**
	 * 判断权限
	 * 
	 * @param response
	 * @param wxPublicUsers
	 * @param url
	 * @param snsapi_userinfo
	 *            false:只能获取openid,true:可获取头像、昵称等信息
	 * @param params
	 *            业务参数，如活动ID 可为空
	 * @throws Exception
	 */
	public static String powerJudge(HttpServletRequest request,
			WxPublicUsers wxPublicUsers, Integer id) throws Exception {
		String url = "";
		PowerService powerService = applicationContext
				.getBean(PowerService.class);
		PowerMain powerMain = powerService.findPowerMainById(id);
		if (CommonUtil.isNotEmpty(powerMain)) {//
			request.setAttribute("powerMain", powerMain);
			if (powerMain.getPowerType() == 2) {
				List<Map<String, Object>> fields = powerService
						.findFieldList(powerMain.getBusId());
				request.setAttribute("fields", fields);
			}
			url = "merchants/power/phone_login";
		} else {
			url = "error/404";
		}
		return url;
	}

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
	 * 判断商家是否过期
	 * 
	 * @param public_id
	 * @return true:已过期 false:未过期
	 */
	@Deprecated 
	public static boolean isPassByBusUser(Integer public_id) {
		ApplicationContext context = CommonUtil.getApplicationContext();
		boolean isPass = false;
		WxPublicUserService wxPublicUserService = context
				.getBean(WxPublicUserService.class);
		WxPublicUsers wxPublicUsers = wxPublicUserService
				.selectquery(public_id);
		BusUserMapper busUserMapper = context.getBean(BusUserMapper.class);
		BusUser busUser = busUserMapper.selectByPrimaryKey(wxPublicUsers
				.getBusUserId());
		try {
			Integer days = DateTimeKit.daysBetween(new Date(),
					busUser.getEndTime());
			if (CommonUtil.isEmpty(days) || days < 0||busUser.getIs_binding()) {// 会员所对的商家已经过期
				isPass = true;
			}
		} catch (Exception e) {
			isPass = true;
		}
		return isPass;
	}
	
	/**
	 * 商家ID
	 * @param busId
	 * @return
	 */
	public static boolean isPassByBusUserVer2(Integer busId) {
		ApplicationContext context = CommonUtil.getApplicationContext();
		boolean isPass = false;
		BusUserMapper busUserMapper = context.getBean(BusUserMapper.class);
		BusUser busUser = busUserMapper.selectByPrimaryKey(busId);
		try {
			Integer days = DateTimeKit.daysBetween(new Date(),
					busUser.getEndTime());
			if (CommonUtil.isEmpty(busUser.getFixed_phone())&&(CommonUtil.isEmpty(days) || days < 0||busUser.getIs_binding())||(CommonUtil.isNotEmpty(busUser.getFixed_phone())&&busUser.getIs_binding())) {// 会员所对的商家已经过期
				isPass = true;
			}
		} catch (Exception e) {
			isPass = true;
		}
		return isPass;
	}

	/**
	 * 粉币计算
	 * 
	 * @param totalMoney
	 *            能抵抗消费金额
	 * @param fans_currency
	 *            粉币值
	 * @param ratio
	 *            比例 字典表1058 Map集合key为1
	 * @return
	 */
	public static Double currencyCount(Double totalMoney, Double fans_currency,
			Double ratio) {
		try {
			if (fans_currency < ratio * 10) {
				return 0.0;
			}
			Integer money = new Double(fans_currency / ratio / 10).intValue();
			if (isEmpty(totalMoney)) {
				return new Double(money * 10);
			} else {
				if (totalMoney >= money * 10) {
					return new Double(money * 10);
				} else {
					return totalMoney;
				}
			}
		} catch (Exception e) {
			log.error("计算粉币抵扣异常");
			e.printStackTrace();
		}
		return 0.0;
	}

	/**
	 * 积分计算
	 * 
	 * @param totalMoney
	 *            能抵抗消费金额
	 * @param integral
	 *            积分
	 * @param ps
	 * @return
	 */
	public static Double integralCount(Double totalMoney, Double integral,
			PublicParameterSet ps) {
		try {
			if (isEmpty(ps)) {
				return 0.0;
			}
			double startMoney = ps.getStartmoney();
			double integralratio = ps.getIntegralratio();
			double changMoney = ps.getChangemoney();
			Byte startType = ps.getStarttype();
			if ("0".equals(startType) || startType == 0) {
				// 积分启兑
				double integralNum = startMoney * integralratio;
				if (integral < integralNum) {
					return 0.0;
				}
			}
			if (isNotEmpty(totalMoney)) {
				// 订单金额小于订单启兑金额
				if (totalMoney < changMoney) {
					return 0.0;
				}
				Integer money = new Double(integral / integralratio).intValue();
				if (totalMoney >= money) {
					return new Double(money);
				} else {
					return totalMoney;
				}
			} else {
				Integer money = new Double(integral / integralratio).intValue();
				return new Double(money);
			}
		} catch (Exception e) {
			log.error("计算积分抵扣异常");
			e.printStackTrace();
		}
		return 0.0;
	}

	/**
	 * 绮夊竵璁＄畻
	 * 
	 * @param fans_currency
	 * @return
	 */
	public static List<Integer> currencyCount(Double fans_currency, Double ratio) {
		if (fans_currency < ratio * 10) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		list.add(0);
		int j = 0;
		for (double i = ratio * 10; i <= fans_currency;) {
			list.add((j + 1) * 10);
			i = i + ratio * 10;
			j++;
		}
		return list;
	}

	/**
	 * 绉垎璁＄畻
	 * 
	 * @param integral
	 * @param ps
	 * @return
	 */
	public static List<Double> integralCount(double integral,
			PublicParameterSet ps) {
		double startMoney = ps.getStartmoney();
		double integralratio = ps.getIntegralratio();
		double changMoney = ps.getChangemoney();
		Byte startType = ps.getStarttype();
		if ("0".equals(startType) || startType == 0) {
			double integralNum = startMoney * integralratio;
			if (integral < integralNum) {
				return null;
			}
		}
		if (integral < integralratio) {
			return null;
		}
		List<Double> list = new ArrayList<Double>();
		list.add(0.0);
		int j = 0;
		for (Double i = integralratio; i <= integral;) {
			list.add((j + 1) * changMoney);
			i = i + integralratio;
			j++;
		}
		return list;
	}

	public static String filterEmoji(String source) {
		if (source != null) {
			Pattern emoji = Pattern
					.compile(
							"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
							Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				return "未知用户";
			}
			return source;
		}
		return source;
	}

	/**
	 *  把字符中的表情替换成指定字符
	 * @param source
	 * @param newStr
	 * @return
	 */
	public static String replaceEmoji(String source,String newStr) {
		if (source != null) {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll(newStr);
				return source;
			}
			return source;
		}
		return source;
	}

	/**
	 * 自定义长度校验码
	 * 
	 * @return
	 */
	public static String getNominateCode(int length) {
		StringBuffer buf = new StringBuffer(
				"a,b,c,d,e,f,g,h,i,g,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z");
		buf.append(",1,2,3,4,5,6,7,8,9,0");
		String[] arr = buf.toString().split(",");
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			Integer count = arr.length;
			int a = random.nextInt(count);
			sb.append(arr[a]);
		}
		return sb.toString();
	}

	// 将字符串转换成二进制字符串，以空格相隔
	public static String StrToBinstr(String str) {
		char[] strChar = str.toCharArray();
		String result = "";
		for (int i = 0; i < strChar.length; i++) {
			result += Integer.toBinaryString(strChar[i]) + " ";
		}
		return result;
	}

	// 将初始二进制字符串转换成字符串数组，以空格相隔
	public static String[] StrToStrArray(String str) {
		return str.split(" ");
	}

	// 将二进制字符串转换成Unicode字符串
	public static String BinstrToStr(String binStr) {
		String[] tempStr = StrToStrArray(binStr);
		char[] tempChar = new char[tempStr.length];
		for (int i = 0; i < tempStr.length; i++) {
			tempChar[i] = BinstrToChar(tempStr[i]);
		}
		return String.valueOf(tempChar);
	}

	// 将二进制字符串转换为char
	public static char BinstrToChar(String binStr) {
		int[] temp = BinstrToIntArray(binStr);
		int sum = 0;
		for (int i = 0; i < temp.length; i++) {
			sum += temp[temp.length - 1 - i] << i;
		}
		return (char) sum;
	}

	// 将二进制字符串转换成int数组
	public static int[] BinstrToIntArray(String binStr) {
		char[] temp = binStr.toCharArray();
		int[] result = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			result[i] = temp[i] - 48;
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			System.out
					.println(URLEncoder
							.encode("fu/qLsJj/XWMPHCFImiasg1Xk+UohpaBsUh/v3nizre/aVG4BBFqDbPbptbhxeZO",
									"gb2312"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 保留2位小数（四舍五入）
	 * 
	 * @param d
	 * @return
	 */
	public static Double getDecimal_2(Double d) {
		BigDecimal bg = new BigDecimal(d);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	/**
	 * 转Integer
	 * 
	 * @param obj
	 */
	public static Integer parseInt(Object obj) throws Exception {
		if (!isEmpty(obj)) {
			return Integer.parseInt(obj.toString());
		} else {
			throw new Exception("对象为空，转换失败！");
		}
	}

	/**
	 * 转Double
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Double parseDouble(Object obj) throws Exception {
		if (!isEmpty(obj)) {
			return Double.parseDouble(obj.toString());
		} else {
			throw new Exception("对象为空，转换失败！");
		}
	}
	
	
	/**
	 * 判断浏览器
	 * @return 1:微信浏览器,99:其他浏览器
	 */
	public static Integer  judgeBrowser(HttpServletRequest request){
		Integer result=null;
		String ua = request.getHeader("user-agent")
				.toLowerCase();
		if (ua.indexOf("micromessenger") > 0) {// 微信-1
			result=1;
		}else{//其他 -99
			result=99;
		}
		return result;
	}
	
	
	/**
	 * 判断浏览器+判断是否有支付宝/公众号
	 * @return 1:微信浏览器,99:其他浏览器,-1:微信浏览器，但是没有公众号 -2:其他浏览器，但是没有支付宝
	 */
	public static Integer  judgeBrowser(HttpServletRequest request,Integer busId){
		Integer result=null;
		String ua = request.getHeader("user-agent")
				.toLowerCase();
		if (ua.indexOf("micromessenger") > 0) {// 微信-1
			WxPublicUsersMapper publicUsersMapper=applicationContext.getBean(WxPublicUsersMapper.class);
			WxPublicUsers wx=publicUsersMapper.selectByUserId(busId);
			if(CommonUtil.isEmpty(wx)){//没有公众号
				result=-1;
			}else{
				result=1;
			}
		}else{//其他 -99
			AlipayUserMapper alipayUserMapper=applicationContext.getBean(AlipayUserMapper.class);
			AlipayUser alipayUser=alipayUserMapper.selectByBusId(busId);
			if(CommonUtil.isEmpty(alipayUser)){//没有公众号
				result=-2;
			}else{
				result=99;
			}
		}
		return result;
	}
	
	
	/**
	 * 存入房产经纪人登陆的SESSION
	 * @param request
	 * @param obj
	 */
	public static void setAgentuser(HttpServletRequest request, FangchanUserAgent obj) {
		try {
			request.getSession().setAttribute("agentuser", obj);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获取房产经纪人的SESSION
	 * @param request
	 * @return
	 */
	public static FangchanUserAgent getAgentuser(HttpServletRequest request) {
		try {
			return (FangchanUserAgent) request.getSession().getAttribute(
					"agentuser");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};
	
	/**
	 * 存入房产业主登陆的SESSION
	 * @param request
	 * @param obj
	 */
	public static void setAgentyz(HttpServletRequest request, FangchanUserAgent obj) {
		try {
			request.getSession().setAttribute("yzuser", obj);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获取房产业主的SESSION
	 * @param request
	 * @return
	 */
	public static FangchanUserAgent getAgentyz(HttpServletRequest request) {
		try {
			return (FangchanUserAgent) request.getSession().getAttribute(
					"yzuser");
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	};
	
	
	public static String getBytes(String str){
		try {
			if(str.equals(new String(str.getBytes("iso8859-1"), "iso8859-1")))
			{
				str=new String(str.getBytes("iso8859-1"),"utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	
	/**
	 * url中文参数乱码
	 * @param str
	 * @return
	 */
	public static String UrlEncode(String str){

		try {
			return  URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
