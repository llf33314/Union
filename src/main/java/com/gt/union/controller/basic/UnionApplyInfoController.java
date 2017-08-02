package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.basic.UnionApplyInfoConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员申请信息 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionApplyInfo")
public class UnionApplyInfoController {

	private Logger logger = Logger.getLogger(UnionApplyInfoController.class);

	@Autowired
	private IUnionApplyInfoService unionApplyInfoService;

	@Autowired
	private IUnionMemberService unionMemberService;

    /***
     * 售卡佣金分成管理.比例设置查询
     * @param request
     * @param page
     * @param unionId
     * @param listType
     * @return
     */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listSellDivideProportion(HttpServletRequest request, Page page
            , @RequestParam(name = "unionId", required = true)Integer unionId
            , @RequestParam(name = "listType", required = true)Integer listType) {
	    try {
	        BusUser busUser = SessionUtils.getLoginUser(request);
	        if (busUser == null) {
	            throw new Exception("UnionApplyInfoController.listSellDivideProportion():无法通过session获取用户的信息!");
            }
	        if (!this.unionMemberService.isUnionOwner(unionId, busUser.getId())) {
                throw new Exception("UnionApplyInfoController.listSellDivideProportion():当前请求人不是联盟的盟主(unionId=" + unionId + ")!");
            }
	        switch (listType) {
                case UnionApplyInfoConstant.LIST_TYPE_PROPORTION_PAGE:
                    Page result = this.unionApplyInfoService.pageSellDivideProportion(page, unionId);
                    return GTJsonResult.instanceSuccessMsg(result).toString();
                case UnionApplyInfoConstant.LIST_TYPE_PROPORTION_LIST:
                    List<Map<String, Object>> resultList = this.unionApplyInfoService.listSellDivideProportion(unionId);
                    return GTJsonResult.instanceSuccessMsg(resultList).toString();
                default:
                    throw new Exception("UnionApplyInfoController.listSellDivideProportion():不支持的查询类型listType(value=" + listType + ")!");
            }
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
	}

    /**
     * 售卡佣金分成管理.更新比例
     * @param request
     * @param unionId
     * @return
     */
	@RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateSellDivideProportion(HttpServletRequest request
            , @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestBody List<UnionApplyInfo> unionApplyInfoList) {
	    try {
	        BusUser busUser = SessionUtils.getLoginUser(request);
	        if (busUser == null) {
	            throw new Exception("UnionApplyInfoController.listSellDivideProportion():无法通过session获取用户的信息!");
            }
	        if (!this.unionMemberService.isUnionOwner(unionId, busUser.getId())) {
                throw new Exception("UnionApplyInfoController.listSellDivideProportion():当前请求人不是联盟的盟主(unionId=" + unionId + ")!");
            }
	        this.unionApplyInfoService.updateSellDivideProportion(unionApplyInfoList);
        } catch (Exception e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
	    return GTJsonResult.instanceSuccessMsg().toString();
    }

	/**
	 * 更新盟员信息
	 * @param request
	 * @param unionId
	 * @param unionApplyInfo
	 * @return
	 */
	@ApiOperation(value = "更新盟员信息", notes = "更新盟员信息", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "更新盟员信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateUnionApplyInfo(HttpServletRequest request,
									   @ApiParam(name="id", value = "盟员信息id", required = true) @PathVariable Integer id ,
									   @ApiParam(name="unionApplyInfo", value = "盟员信息", required = true) @RequestBody UnionApplyInfo unionApplyInfo ,
									   @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException("请使用主账号操作");
			}
			unionApplyInfo.setId(id);
			this.unionApplyInfoService.updateUnionApplyInfo(unionApplyInfo,busUser.getId(),unionId);
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg("更新盟员信息错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"更新成功").toString();
	}
}
