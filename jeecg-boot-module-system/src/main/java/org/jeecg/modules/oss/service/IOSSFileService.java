package org.jeecg.modules.oss.service;

import java.io.IOException;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.oss.entity.OSSFile;
import org.springframework.web.multipart.MultipartFile;

public interface IOSSFileService extends IService<OSSFile> {

	String upload(MultipartFile multipartFile,String dir) throws IOException;

	boolean delete(OSSFile ossFile);

}
