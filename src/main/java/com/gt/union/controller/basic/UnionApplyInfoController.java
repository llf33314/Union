package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.vo.UnionApplyInfoVO;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.service.common.IUnionValidateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private static final String GET_ID = "UnionApplyInfoController.getById()";
    private static final String UPDATE_ID = "UnionApplyInfoController.updateById()";
    private static final String LIST_SELLDIVIDEPROPORTION_PAGE = "UnionApplyInfoController.listBySellDivideProportionInPage()";
    private static final String LIST_SELLDIVIDEPROPORTION_LIST = "UnionApplyInfoController.listBySellDivideProportionInList()";
    private static final String UPDATE_SELLDIVIDEPROPORTION_ID = "UnionApplyInfoController.updateSellDivideProportionById()";
	private Logger logger = Logger.getLogger(UnionApplyInfoController.class);

	@Autowired
	private IUnionApplyInfoService unionApplyInfoService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
    private IUnionRootService unionRootService;

	@Autowired
	private IUnionValidateService unionValidateService;

	/**
	 * 获取编辑盟员信息
	 * @param request
	 * @param id
	 * @param unionId
	 * @return
	 */
	@ApiOperation(value = "获取编辑盟员信息", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "1", description = "获取编辑盟员信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getById(HttpServletRequest request
        , @ApiParam(name="id", value = "盟员信息id", required = true) @PathVariable("id") Integer id
        , @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId) {
		try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException(GET_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String,Object> data = this.unionApplyInfoService.getMapById(id, unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(data).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("",e);
            return GTJsonResult.instanceErrorMsg(GET_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	/**
	 * 更新盟员信息
	 * @param request
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "更新盟员信息", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "更新盟员信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateById(HttpServletRequest request
        , @ApiParam(name="id", value = "盟员信息id", required = true) @PathVariable Integer id
        , @ApiParam(name="unionApplyInfo", value = "盟员信息", required = true) @RequestBody @Valid UnionApplyInfoVO vo, BindingResult result) {
		try {
			this.unionValidateService.checkBindingResult(result);
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException(UPDATE_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			vo.setId(id);
			this.unionApplyInfoService.updateById(vo,busUser.getId());
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	/***
	 * 售卡佣金分成管理-比例设置查询-分页
	 * @param request
	 * @param page
	 * @param unionId
	 * @return
	 */
	@ApiOperation(value = "售卡佣金分成管理-比例设置查询-分页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/sellDivideProportion/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listBySellDivideProportionInPage(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = true)@RequestParam(name = "unionId", required = true)Integer unionId) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if (!this.unionRootService.isUnionOwner(unionId, busUser.getId())) {
				throw new BusinessException(LIST_SELLDIVIDEPROPORTION_PAGE, "", "当前请求人不是联盟的盟主");
			}
            Page result = this.unionApplyInfoService.listBySellDivideProportionInPage(page, unionId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
			logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_SELLDIVIDEPROPORTION_PAGE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

    /***
     * 售卡佣金分成管理-比例设置查询-列表
     * @param request
     * @param page
     * @param unionId
     * @return
     */
    @ApiOperation(value = "售卡佣金分成管理-比例设置查询-列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/sellDivideProportion/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listBySellDivideProportionInList(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)@RequestParam(name = "unionId", required = true)Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (!this.unionRootService.isUnionOwner(unionId, busUser.getId())) {
                throw new BusinessException(LIST_SELLDIVIDEPROPORTION_LIST, "", "当前请求人不是联盟的盟主");
            }
            List<Map<String,Object>> result = this.unionApplyInfoService.listBySellDivideProportionInList(unionId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_SELLDIVIDEPROPORTION_LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

	/**
	 * 售卡佣金分成管理-更新比例
	 * @param request
	 * @param unionId
	 * @return
	 */
	@ApiOperation(value = "售卡佣金分成管理-更新比例", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/sellDivideProportion", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateSellDivideProportionById(HttpServletRequest request
			, @ApiParam(name = "unionId", value = "联盟id", required = true)
             @RequestParam(name = "unionId", required = true) Integer unionId
			, @ApiParam(name = "unionApplyInfoList", value = "更新的比例实体，只需填写id和sellDivideProportion即可", required = true)
             @RequestBody List<UnionApplyInfo> unionApplyInfoList) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if (!this.unionRootService.isUnionOwner(unionId, busUser.getId())) {
				throw new BusinessException(UPDATE_SELLDIVIDEPROPORTION_ID, "", "当前请求人不是联盟的盟主");
			}
			this.unionApplyInfoService.updateBySellDivideProportion(unionApplyInfoList);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_SELLDIVIDEPROPORTION_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

}
