package org.jeecg.modules.form.util;


import org.jeecg.modules.form.vo.CompliesVO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName: GetProcessResult
 * @Description: 处理在线编译结果的工具类
 * @author: HuangSn
 * @date: 2021/8/10  3:53 下午
 */
public class GetProcessResult {
    public CompliesVO complieVO=new CompliesVO();

    public GetProcessResult(Process process, long l) { }

    public GetProcessResult() { }

    public CompliesVO getResult(Process process, long time) throws InterruptedException, IOException {
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        process.waitFor();
        //获取结果
        ByteArrayOutputStream byteArrayOutputInputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputErrorStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buff)) != -1) {
            byteArrayOutputInputStream.write(buff, 0, len);
        }
        while ((len = errorStream.read(buff)) != -1) {
            byteArrayOutputErrorStream.write(buff, 0, len);
        }
        byte[] inputStreamBytes = byteArrayOutputInputStream.toByteArray();
        byte[] errorStreamBytes = byteArrayOutputErrorStream.toByteArray();
        String res = new String(inputStreamBytes) + new String(errorStreamBytes);
        complieVO.setTakeTime(time);
        complieVO.setRunResult(res);
        return complieVO;
    }
}
