package org.jeecg.modules.form.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.form.vo.CompliesVO;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 在线Java编译工具
 *
 * @ClassName: OnlineJavaComplieUtil
 * @author: HuangSn
 * @date: 2021/8/9  17:05
 */
@Slf4j
public class OnlineJavaComplieUtil {

    public CompliesVO compliesVO = new CompliesVO();
    public GetProcessResult getProcessResult = new GetProcessResult();

    /**
     * 创建文件
     * @param code
     * @param clazzName
     * @return
     * @throws IOException
     */
    public String writeIntoFile(String code, String clazzName) throws IOException {
        //List<String> result = new ArrayList<>();
        String fileName = clazzName + ".java";
        File file = null;
        try {
            file = new File("../javaTempFile");
            if (!file.exists()) file.mkdir();
            String path = file + "/" + fileName;
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(code);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath()+"/"+clazzName+".java";
    }

    /**
     * 通过code的内容，找到类名
     * @param code
     * @return
     */
    public String getClazzName(String code){
        String pattern = "class(\\s*)(.*?)(\\s*)(\\{) ";
        String clazzName = null;
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(code);
        if (m.find( )) {
            clazzName = m.group(2);
        } else {
            System.out.println("NO MATCH");
        }
        log.info("验证Java类名："+clazzName);
        return clazzName;
    }

    /**
     * 进行编译
     * @param code
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public CompliesVO execute(String code) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        // Java主类方法名
        String javaClazzName = getClazzName(code);
        // 获取结果
        String rest1 = writeIntoFile(code, javaClazzName);
        // 全路径文件名
        String[] cmd;
        Process process = null;
        try{
            cmd = new String[]{"java", rest1};
            process = Runtime.getRuntime().exec(cmd);
        }catch (Exception a){
            a.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        return getProcessResult.getResult(process, (endTime-startTime));
    }
}
