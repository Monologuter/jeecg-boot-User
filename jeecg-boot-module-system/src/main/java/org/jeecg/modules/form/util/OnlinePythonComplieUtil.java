package org.jeecg.modules.form.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.form.vo.CompliesVO;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.*;

/**
 * python程序调用工具类
 *
 * @author: HuangSn
 * @date: 2021年07月17日 18:41
 */
@Slf4j
public class OnlinePythonComplieUtil {

    private GetProcessResult getProcessResult = new GetProcessResult();
    private DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();

    /**
     * 将python代码写入文件
     *
     * @param code     代码
     * @param userName 用户名
     * @return 返回值
     */
    public List<String> writeIntoFile(String code, String userName) throws IOException {
        log.info("正在创建list");
        //创建文件夹
        File file = new File("..//pyTempFile");
        //获取文件夹的路径
        String filePath1 = file.getCanonicalPath();
        if (!file.exists()) file.mkdir();
        //创建文件 “xxx.py”
        String fileName = null;
        fileName = "py_" + userName + ".py";
        String path = file + "/" + fileName;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath2 = filePath1 + "\\" + fileName;
        List<String> pathList = new ArrayList<>();
        pathList.add(filePath1);
        pathList.add(filePath2);
        pathList.add(fileName);
        return pathList;
    }

    /**
     * 执行python代码
     *
     * @param path 文件路径
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */
    public CompliesVO execute(List<String> path) throws IOException, InterruptedException {
        Process process = null;
        long startTime = System.currentTimeMillis();
        try {
            //docker run -v /opt/temp:/usr/src/python -w /usr/src/python python:latest python test1.py
            String localPath = path.get(0) + ":";
            log.info("localPath:" + localPath);
            String pythonPath = "/usr/src/python";
            String[] args = new String[]{"docker", "run", "-v", localPath + pythonPath, "-w", pythonPath, "python:latest", "python", path.get(2)};
            log.info(Arrays.toString(args));
            //调用本地python3环境，执行文件
            process = Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        return getProcessResult.getResult(process, (endTime - startTime));
    }
}