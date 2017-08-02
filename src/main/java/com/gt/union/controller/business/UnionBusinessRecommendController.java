package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.vo.business.UnionBusinessRecommendFormVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟商家商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBusinessRecommend")
public class UnionBusinessRecommendController {

	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

	/**
	 * 获取盟员的联盟卡列表  TODO 多状态查询 排序
	 * @param page
	 * @param request
	 * @return
	 */
	/*@RequestMapping(value = "", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String unionBusinessRecommend(Page page, HttpServletRequest request, @RequestParam(name = "unionId", required = true) Integer unionId
			, @RequestParam(name = "phone", required = false) String phone
			, @RequestParam(name = "isAcceptance", required = false) Integer isAcceptance){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			UnionBusinessRecommendVO vo = new UnionBusinessRecommendVO();
			vo.setUnionId(unionId);
			vo.setBusId(busId);
			vo.setPhone(phone);
			vo.setIsAcceptance(isAcceptance);
			page = unionBusinessRecommendService.selectUnionBusinessRecommendList(page,vo);
		}catch (BaseException e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取盟员联盟卡列表错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取商家的佣金平台管理员成功").toString();
	}*/

    /**
     * 商机信息查询：
     * （1）当listType=LIST_TYPE_TO_ME时，查询我的商机信息；
     * （2）当listType=LIST_TYPE_FROM_ME时，查询我推荐的商机信息.
     * @param request
     * @param page
     * @param listType
     * @param unionId
     * @return
     */
    @ApiOperation(value = "商机信息查询"
            , notes = "根据listType进行分类查询"
            , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listUnionBusinessRecommend(HttpServletRequest request, Page page
            , @ApiParam(name = "listType", value = "查询类型：值为1时，查询我的商机信息；值为2时，查询我推荐的商机信息", required = true)
             @RequestParam(name = "listType", required = true) Integer listType
            , @ApiParam(name = "unionId", value = "所属联盟id", required = false)
             @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAcceptance", value = "状态，当勾选多个时用英文字符的逗号拼接，如=1,2")
             @RequestParam(name = "isAcceptance", required = false) String isAcceptance) {
        Page result = null;
        try {
            /*BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionBusinessRecommendController.listUnionBusinessRecommend()：无法通过session获取用户的信息!");
            }*/
            switch (listType) {
                case UnionBusinessRecommendConstant.LIST_TYPE_TO_ME:
                    result = this.unionBusinessRecommendService.listUnionBusinessRecommendToMe(page, 33, unionId, isAcceptance);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_FROM_ME:
                    result = this.unionBusinessRecommendService.listUnionBusinessRecommendFromMe(page, 33, unionId, isAcceptance);
                    break;
                default:
                    throw new Exception("UnionBusinessRecommendController.listUnionBusinessRecommend():不支持的查询类型listType(value=" + listType + ")!");
            }
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
	}


	/**
	 * 审核
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String verifyUnionBusinessRecommend(HttpServletRequest request,@PathVariable("id") Integer id, @RequestBody Integer isAcceptance){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			UnionBusinessRecommend recommend = new UnionBusinessRecommend();
			recommend.setId(id);
			recommend.setIsAcceptance(isAcceptance);
			unionBusinessRecommendService.updateVerifyRecommend(recommend);
			if(isAcceptance == 1){
				//接受
				return GTJsonResult.instanceSuccessMsg(null,null, "商机接受成功").toString();
			}else if(isAcceptance == 2){
				//拒绝
				return GTJsonResult.instanceSuccessMsg(null,null,"商机拒绝成功").toString();
			}
			return GTJsonResult.instanceErrorMsg("审核失败").toString();
		}catch (BaseException e){
			logger.error("审核商机消息错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("审核商机消息错误"+e.getMessage());
			if(isAcceptance == 1){
				//接受
				return GTJsonResult.instanceErrorMsg("商机拒绝失败").toString();
			}else if(isAcceptance == 2){
				//拒绝
				return GTJsonResult.instanceErrorMsg("商机拒绝失败").toString();
			}
			return GTJsonResult.instanceErrorMsg("审核失败").toString();
		}
	}

	/**
	 * 添加商机推荐
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionBusinessRecommend(HttpServletRequest request, @RequestBody UnionBusinessRecommendFormVO vo){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			vo.setBusId(busId);
			unionBusinessRecommendService.saveUnionBusinessRecommend(vo);
		}catch (BaseException e){
			logger.error("添加商机推荐错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("添加商机推荐错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("添加失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"添加成功").toString();
	}

}
