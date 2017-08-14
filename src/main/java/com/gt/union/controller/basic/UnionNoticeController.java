package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.entity.basic.UnionNotice;
import com.gt.union.service.basic.IUnionNoticeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 联盟公告 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionNotice")
public class UnionNoticeController {
	private static final String GET_UNIONID = "UnionNoticeController.getByUnionId()";
	private static final String SAVE_UNIONID = "UnionNoticeController.saveByUnionId()";
	private static final String UPDATE_ID_UNIONID = "UnionNoticeController.updateByIdAndUnionId()";

	private Logger logger = Logger.getLogger(UnionNoticeController.class);

	@Autowired
	private IUnionNoticeService unionNoticeService;

	@ApiOperation(value = "获取联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
		try {
			//TODO 这里的操作应该放在service中
			UnionNotice notice = null;
			EntityWrapper wrapper = new EntityWrapper<UnionNotice>();
			wrapper.eq("union_id", unionId);
			wrapper.eq("del_status", 0);
			notice = unionNoticeService.selectOne(wrapper);
			return GTJsonResult.instanceSuccessMsg(notice).toString();
//		} catch (BaseException e) {
//			logger.error("", e);
//			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(GET_UNIONID,"获取联盟公告失败").toString();
		}
	}

	@ApiOperation(value = "新增联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId")Integer unionId
        , @ApiParam(name = "unionNotice", value = "联盟公告信息", required = true) @RequestBody UnionNotice unionNotice) {
		try{
            unionNotice = unionNoticeService.saveByUnionId(unionNotice);
            return GTJsonResult.instanceSuccessMsg(unionNotice).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	@ApiOperation(value = "修改联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateByIdAndUnionId(@ApiParam(name = "id", value = "联盟公告id", required = true) @PathVariable("id")Integer id
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId")Integer unionId
        , @ApiParam(name = "unionNotice", value = "联盟公告信息", required = true) @RequestBody  UnionNotice unionNotice) {
        try{
            //TODO
            return GTJsonResult.instanceSuccessMsg().toString();
//        }catch (BaseException e){
//            logger.error("", e);
//            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }

	}
}
