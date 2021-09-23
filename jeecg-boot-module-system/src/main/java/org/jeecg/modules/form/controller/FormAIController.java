package org.jeecg.modules.form.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.dto.FormDTO;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.service.FormDesignerService;
import org.jeecg.modules.form.vo.FormVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 表单AI辅助功能控制器
 * @author XuDeQing
 * @date 2021-03-2021/3/4 8:49
 */
@RestController
@RequestMapping("/form")
@Slf4j
public class FormAIController {
    @Value("${ai.server}")
    private String serverIp;
    @Value("${ai.port}")
    private Integer serverPort;

    private final FormDesignerService formService;

    @Autowired
    public FormAIController(FormDesignerService formService) {
        this.formService = formService;
    }

    private String getServerUrl() {
        return "http://" + serverIp + ":" + serverPort;
    }

    @PostMapping("/recognize")
    public Result<FormDTO> generateFormJsonFromAIByTxt(@RequestParam String formName, @RequestParam String formCode, @RequestParam MultipartFile[] files) {
        return getFormDTO(formName, formCode, files, "/recognize");
    }

    @PostMapping("/recognize-hw")
    public Result<FormDTO> generateFormJsonFromAIByHw(@RequestParam String formName, @RequestParam String formCode, @RequestParam MultipartFile[] files) {
        return getFormDTO(formName, formCode, files, "/recognize-hw");
    }

    private Result<FormDTO> getFormDTO(String formName, String formCode, MultipartFile[] files, String url) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        for (int i = 0; i < files.length; i++) {
            formData.add("file" + i, files[i].getResource());
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestBody = new HttpEntity<>(formData, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(getServerUrl() + url, requestBody, String.class);
        ServiceUtils.throwIfFailed(StringUtils.isNotBlank(responseEntity.getBody()), FormErrorMessageConstant.FORM_RECOGNIZE_FAILED);
        FormDTO form = new FormDTO();
        form.setName(formName);
        form.setCode(formCode);
        form.setJson(JSON.parseObject(responseEntity.getBody()));
        FormVO formVO = new FormVO();
        BeanUtils.copyProperties(form, formVO);
        return Result.OK(formService.saveFormDTO(form));
    }

    @GetMapping("/test")
    public FormDO test() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("node d:/sourceCode/node/processTest.js");
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        FormDO formDO = objectMapper.readValue(stringBuffer.toString(), FormDO.class);
        int exitCode = process.waitFor();
        log.info(String.valueOf(exitCode));
        return formDO;
    }
}
