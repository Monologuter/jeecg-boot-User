package org.jeecg.modules.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.camunda.bpm.engine.SuspendedEntityInteractionException;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/8 11:16
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BasePointExceptionHandler {

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object> typeMismatchExceptionHandler(TypeMismatchException e) {
        return ResponseEntity.badRequest().body(Result.error(400, "请求参数错误:" + e.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> authorizationExceptionHandler(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(401, "没有相应权限:" + e.getMessage()));
    }

    /**
     * 捕获404异常并返回说明
     *
     * @param e 异常
     * @return 异常详细说明
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        //TODO 暂时无法正常工作,原因不明
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "请求路径不存在:" + e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Result.error(405, "请求方法不支持:" + e.getMessage()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> httpServerErrorExceptionHandler(HttpServerErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500, e.getStatusText()));
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<Object> badSqlGrammarExceptionHandler(BadSqlGrammarException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500, "SQL低级语法错误:" + e.getMessage()));
    }

    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Object> bncategorizedSQLExceptionHandler(UncategorizedSQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500, "字段类型错误:" + e.getMessage()));
    }

    @ExceptionHandler(MultiDatasourceException.class)
    public ResponseEntity<Object> datasourceException(MultiDatasourceException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404,"多数据源异常:"+e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> otherExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500, e.getMessage()));
    }
    @ExceptionHandler(SuspendedEntityInteractionException.class)
    public ResponseEntity<Object> suspendedEntityInteractionException(SuspendedEntityInteractionException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500,  "流程定义已挂起"+ e.getMessage()));
    }
}
