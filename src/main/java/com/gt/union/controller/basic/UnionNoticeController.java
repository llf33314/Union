package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
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

	private Logger logger = Logger.getLogger(UnionNoticeController.class);

	@Autowired
	private IUnionNoticeService unionNoticeService;

	/**
	 * 获取联盟公告
	 * @param unionId
	 * @return
	 */
	@ApiOperation(value = "获取联盟公告" , notes = "获取联盟公告" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionNotice(@RequestParam(value = "unionId", required = true) Integer unionId) {
		if(CommonUtil.isEmpty(unionId)){
			return GTJsonResult.instanceErrorMsg("参数错误").toString();
		}
		UnionNotice notice = null;
		try{
			EntityWrapper wrapper = new EntityWrapper<UnionNotice>();
			wrapper.eq("union_id",unionId);
			wrapper.eq("del_status",0);
			notice = unionNoticeService.selectOne(wrapper);
		}catch (Exception e){
			logger.error("获取联盟公告错误" + e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取联盟公告错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(notice,null,"获取联盟公告成功").toString();
	}



	/**
	 * 新增联盟公告
	 * @param notice
	 * @return
	 */
	@ApiOperation(value = "新增联盟公告" , notes = "新增联盟公告" , produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(description = "新增联盟公告", op_function = "2")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveNotice(@ApiParam(name = "notice", value = "联盟公告信息", required = true) @RequestBody UnionNotice notice,
							 @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam("unionId")Integer unionId) {
		try{
			notice = unionNoticeService.saveNotice(notice);
		}catch (BaseException e){
			logger.error("保存联盟公告错误",e);
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("保存联盟公告错误",e);
			return GTJsonResult.instanceErrorMsg("保存失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(notice,null,"保存成功").toString();
	}

	/**
	 * 修改联盟公告
	 * @param notice
	 * @return
	 */
	@ApiOperation(value = "修改联盟公告" , notes = "修改联盟公告" , produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(description = "修改联盟公告", op_function = "3")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateNotice(@ApiParam(name = "notice", value = "联盟公告信息", required = true) @RequestBody  UnionNotice notice,
							   @ApiParam(name = "id", value = "联盟公告id", required = true) @PathVariable("id")Integer id,
							   @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam("unionId")Integer unionId) {
		notice.setId(id);
		return saveNotice(notice,unionId);
	}
}
