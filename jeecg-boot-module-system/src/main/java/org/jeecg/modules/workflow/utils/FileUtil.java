package org.jeecg.modules.workflow.utils;

import org.jeecg.modules.workflow.common.FileType;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName FileUtil
 * @CreateTime 2021-08-30 20:01
 * @Version 1.0
 * @Description: 文件操作工具类
 */
public class FileUtil {
    /**
     * 私有化构造方法
     */
    private FileUtil(){}

    /**
     * 将文件头转换成16进制字符串
     * @param src 原生数组
     * @return
     */
    private static String byteToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if(src == null || src.length <=0){
            return null;
        }
        for (int i = 0;i<src.length;i++){
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length()<2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头
     * @param is 文件流
     * @return
     * @throws IOException
     */
    private static String getFileContent(InputStream is) throws IOException{
        byte[] b = new byte[28];
        InputStream inputStream = null;
        try{
            is.read(b,0,28);
        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }finally {
            if (inputStream != null){
                try{
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return byteToHexString(b);
    }

    /**
     * 获取文件类型类
     * @param is
     * @return
     * @throws IOException
     */
    public static FileType getType(InputStream is) throws IOException{
        String fileHead = getFileContent(is);
        if (fileHead == null || fileHead.length() == 0){
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type:fileTypes){
            if (fileHead.startsWith(type.getValue())){
                return type;
            }
        }
        return null;
    }
    public static String getFileType(InputStream is) throws Exception{
        FileType fileType = getType(is);
        if (fileType!=null){
            return fileType.getValue();
        }
        return null;
    }

    /**
     * 获取工作流文件中的formKey
     */
    public static Map<String,String> getFormKeyList(MultipartFile file) throws Exception {
        Map<String,String> result = new HashMap();
        //获取文件类型
        String type = FileUtil.getFileType(file.getInputStream());
        if(FileType.ZIP.getValue().equals(type)){
            //以zip形式解析
            //创建要部署的zip流
            ZipInputStream deployInputStream = new ZipInputStream(file.getInputStream());
            //拷贝一个相同的zip流，用于解析
            byte[] temp02 = UtilTools.toByteArray(file.getInputStream());
            InputStream te = new ByteArrayInputStream(temp02);
            ZipInputStream zipInputStream = new ZipInputStream(te);
            //解析拷贝的zip流并填充formKey列表
            ZipEntry zipEntry;
            while((zipEntry = zipInputStream.getNextEntry())!=null){
                if (zipEntry.isDirectory()){
                    continue;
                }
                //原始流抽取出数组
                byte[] temp = UtilTools.toByteArray(zipInputStream);
                //用数组创建一个临时流
                InputStream tempis = new ByteArrayInputStream(temp);
                UtilTools.readModelFromStream(tempis,result);
            }
        }else if(FileType.XML.getValue().equals(type)){
            //解析bpmn填充外置表单列表
            UtilTools.readModelFromStream(file.getInputStream(),result);
        }else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FILE);
        }
        return result;
    }
}
