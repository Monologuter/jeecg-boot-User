package org.jeecg.modules.common.exception;

/**
 * @author jiahaitao
 * @Description: 多数据源异常
 * @company DXC.technology
 * @create 2021-05-13 9:55
 */
public class MultiDatasourceException extends RuntimeException{

    private static final long serialVersionUID = -7034897190745766939L;

    public MultiDatasourceException(){ }

    public MultiDatasourceException(String message) {
        super(message);
    }
}
