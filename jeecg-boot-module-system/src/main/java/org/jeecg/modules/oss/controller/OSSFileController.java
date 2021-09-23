package org.jeecg.modules.oss.controller;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.oss.entity.OSSFile;
import org.jeecg.modules.oss.service.IOSSFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Api(tags = "阿里云oss")
@RequestMapping("/sys/oss/file")
public class OSSFileController {

	@Autowired
	private IOSSFileService ossFileService;

	@ResponseBody
	@GetMapping("/list")
	@ApiOperation("查询列表")
	public Result<IPage<OSSFile>> queryPageList(OSSFile file,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		Result<IPage<OSSFile>> result = new Result<>();
		QueryWrapper<OSSFile> queryWrapper = QueryGenerator.initQueryWrapper(file, req.getParameterMap());
		Page<OSSFile> page = new Page<>(pageNo, pageSize);
		IPage<OSSFile> pageList = ossFileService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	@ResponseBody
	@PostMapping("/upload")
	@ApiOperation("上传")
	//@RequiresRoles("admin")
	public Result<String> upload(@RequestParam(required = false) String dir,@RequestParam("file") MultipartFile multipartFile) {
		Result<String> result = new Result<>();
		try {
			String url = ossFileService.upload(multipartFile,dir);
			result.success("上传成功！");
			result.setResult(url);
		}
		catch (Exception ex) {
			log.info(ex.getMessage(), ex);
			result.error500("上传失败");
		}
		return result;
	}

	@ResponseBody
	@DeleteMapping("/delete")
	@ApiOperation("删除")
	public Result delete(@RequestParam(name = "id") String id) {
		Result result = new Result();
		OSSFile file = ossFileService.getById(id);
		if (file == null) {
			result.error500("未找到对应实体");
		}
		else {
			boolean ok = ossFileService.delete(file);
			if (ok) {
				result.success("删除成功!");
			}
		}
		return result;
	}

	/**
	 * 通过id查询.
	 */
	@ResponseBody
	@GetMapping("/queryById")
	@ApiOperation("根据id查询")
	public Result<OSSFile> queryById(@RequestParam(name = "id") String id) {
		Result<OSSFile> result = new Result<>();
		OSSFile file = ossFileService.getById(id);
		if (file == null) {
			result.error500("未找到对应实体");
		}
		else {
			result.setResult(file);
			result.setSuccess(true);
		}
		return result;
	}

}
