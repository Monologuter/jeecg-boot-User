package org.jeecg.modules.form;

import org.jeecg.JeecgSystemApplication;
import org.jeecg.modules.form.mapper.FormDataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName Test1
 * @CreateTime 2021-08-31 17:31
 * @Version 1.0
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = JeecgSystemApplication.class)
public class Test1 {

    @Autowired
    FormDataMapper formDataMapper;

    @Test
    public void test01(){
        System.out.println(formDataMapper.selectById("1412613152667840513").getRowData());
    }
}
