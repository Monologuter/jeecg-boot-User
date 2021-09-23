package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 胡文晓
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/1813:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleForm {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;        //主键
    private  String roleId;  //用户id

    private  String formId;  //表单id

    private String  formKey;

    private String formValue;
}
