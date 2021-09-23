package org.jeecg.modules.form.constant;

/**
 * @ClassName: OperationType
 * @Description:
 * @author: HuangSn
 * @date: 2021/8/30  10:35
 */
public enum OperationTypeEnum {

    // insert/update/delete/read
    INSERT,
    UPDATE,
    DELETE,
    READ;

    public OperationTypeEnum getType(){
        if(this.equals(INSERT)){
            return INSERT;
        }
        if(this.equals(DELETE)){
            return DELETE;
        }
        if(this.equals(READ)){
            return READ;
        }
        if(this.equals(UPDATE)){
            return UPDATE;
        }
        return null;
    }

}
