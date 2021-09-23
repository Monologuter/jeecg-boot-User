/**
 * @Author: ${author}
 * @CreateTime: ${date}
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @author: ${author}
 * @date: ${date}
 */
@SpringBootApplication
@MapperScan("org.jeecg")
public class AutoGeneratorCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoGeneratorCodeApplication.class);
    }
}
