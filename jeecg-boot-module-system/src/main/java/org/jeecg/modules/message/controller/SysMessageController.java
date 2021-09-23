package org.jeecg.modules.message.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.message.entity.SysMessage;
import org.jeecg.modules.message.mapper.SysMessageMapper;
import org.jeecg.modules.message.service.ISysMessageService;
import org.jeecg.modules.message.util.CheckAttrIsNullUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息
 * @author: jeecg-boot
 * @date: 2019-04-09
 * @version: V1.0
 */
@Api(tags = "消息管理")
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessage")
public class SysMessageController extends JeecgController<SysMessage, ISysMessageService> {
	@Autowired
	private ISysMessageService sysMessageService;

	private SysMessageMapper sysMessageMapper;

	/**
	 * 分页列表查询
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysMessage sysMessage, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<SysMessage> page = sysMessageService.findSysMessage(sysMessage, pageNo , pageSize);
		return Result.OK(page);
//		throw new RuntimeException("FDsfds");
		//		Page<SysMessage> page = new Page<>(pageNo, pageSize);
//		QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<>();
//		List<SysMessage> sysMessages;
//		//如果sysMessage属性值全为空则查询全部
//		if(CheckAttrIsNullUtil.checkObjFieldIsAllNull(sysMessage)){
//			queryWrapper.orderByDesc("create_time");
//			sysMessages = sysMessageService.getBaseMapper().selectList(queryWrapper);
//			if(sysMessages.size() <= 0){
//				page.setRecords(sysMessages);
//				return Result.ok(page);
//			}
//
//			//如果EsParam为空格或null 则返回前端空字符串""
//			sysMessages.forEach(msg -> {
//				if(CheckAttrIsNullUtil.checkSomeAttrIsNull(sysMessage,"esParam") || msg.getEsParam().trim().length() != 0) {
//					msg.setEsParam("");
//				}
//			});
//			page.setRecords(sysMessages);
//			return Result.ok(page);
//		}
//		//如果sysMessage属性值不全为空则根据条件查询
//		queryWrapper.eq(!CheckAttrIsNullUtil.checkSomeAttrIsNull(sysMessage,"esReceiver") && sysMessage.getEsReceiver().trim().length() != 0,"es_receiver",sysMessage.getEsReceiver())
//				.like(!CheckAttrIsNullUtil.checkSomeAttrIsNull(sysMessage,"esContent"),"es_content",sysMessage.getEsContent())
//				.like(!CheckAttrIsNullUtil.checkSomeAttrIsNull(sysMessage,"esTitle"),"es_title",sysMessage.getEsTitle())
//				.orderByDesc("create_time");
//		sysMessages = sysMessageService.getBaseMapper().selectList(queryWrapper);
//		if(sysMessages.size() <= 0){
//			page.setRecords(sysMessages);
//			return Result.ok(page);
//		}
//		//如果EsParam为空格或null 则返回前端空字符串""
//		sysMessages.forEach(msg -> {
//			if(CheckAttrIsNullUtil.checkSomeAttrIsNull(sysMessage,"esParam") || msg.getEsParam().trim().length() != 0) {
//				msg.setEsParam("");
//			}
//		});
//		page.setRecords(sysMessages);
//		return Result.ok(page);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysMessage sysMessage) {
		sysMessageService.save(sysMessage);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysMessage sysMessage) {	
		sysMessageService.updateById(sysMessage);
        return Result.ok("修改成功!");

	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageService.removeById(id);
        return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		this.sysMessageService.removeByIds(Arrays.asList(ids.split(",")));
	    return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@ApiOperation("通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<SysMessage> result = new Result<>();
		SysMessage sysMessage = sysMessageService.getById(id);
		if(sysMessage == null){
			result.error500("没有这条数据");
			return result;
		}
		if(sysMessage.getEsParam() == "null" || "null".equals(sysMessage.getEsParam()) || sysMessage.getEsParam() == null || sysMessage.getEsParam().trim().length() == 0){
			sysMessage.setEsParam("");
		}
		return Result.ok(sysMessage);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysMessage sysMessage) {
		return super.exportXls(request,sysMessage,SysMessage.class, "推送消息模板");
	}

	/**
	 * excel导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysMessage.class);
	}

}
