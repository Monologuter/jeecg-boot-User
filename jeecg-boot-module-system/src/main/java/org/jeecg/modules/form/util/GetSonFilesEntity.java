package org.jeecg.modules.form.util;

import lombok.Data;
import lombok.ToString;

/**
 * 存储临时GitLab文件实体类
 *
 * @author: lk
 * @date: 2021年08月02日 16:49
 */
@Data
@ToString
public class GetSonFilesEntity {
    private String file_name;
    private String type;
}
