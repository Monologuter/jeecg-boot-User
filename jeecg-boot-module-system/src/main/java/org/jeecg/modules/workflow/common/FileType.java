package org.jeecg.modules.workflow.common;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName FileType
 * @CreateTime 2021-08-30 19:46
 * @Version 1.0
 * @Description: 文件类型枚举类
 */
public enum  FileType {
    JPEG("FFD8FF"),
    ZIP("504B0304"),
    XML("3C3F786D6C");

    private String value = "";
    FileType(String value) {
        this.value = value;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
}