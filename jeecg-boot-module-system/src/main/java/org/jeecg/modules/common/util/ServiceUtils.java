package org.jeecg.modules.common.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.constant.OperationErrorMessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * What purpose
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/11 15:25
 */
public class ServiceUtils {
    private ServiceUtils() {
    }

    public static void throwIfFailed(boolean success, String message) {
        throwIfFailed(success, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static void throwIfFailed(boolean success, HttpStatus status, String message) {
        if (!success) throw new HttpServerErrorException(status, message);
    }

    public static void throwIfFailed(BooleanSupplier supplier, HttpStatus status, String message) {
        if (!supplier.getAsBoolean()) throw new HttpServerErrorException(status, message);
    }

    public static void throwIfFailed(BooleanSupplier supplier, String message) {
        throwIfFailed(supplier, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static <T> void throwIfNull(Supplier<T> supplier, HttpStatus status, String message) {
        if (Objects.isNull(supplier.get())) throw new HttpServerErrorException(status, message);
    }

    public static <T> void throwIfNull(Supplier<T> supplier, String message) {
        throwIfNull(supplier,HttpStatus.INTERNAL_SERVER_ERROR,message);
    }

    public static void throwIfSaveFailed(BooleanSupplier supplier) {
        throwIfFailed(supplier, OperationErrorMessageConstant.SAVE_FAILED);
    }

    public static void throwIfUpdateFailed(BooleanSupplier supplier) {
        throwIfFailed(supplier, OperationErrorMessageConstant.UPDATE_FAILED);
    }

    public static void throwIfDeleteFailed(BooleanSupplier supplier) {
        throwIfFailed(supplier, OperationErrorMessageConstant.DELETE_FAILED);
    }

    public static void throwException(String message){
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,message);
    }

    @SneakyThrows
    public static <T> T fillCreateField(T t) {
        Class<?> aClass = t.getClass();
        Field id = FieldUtils.getField(aClass, "id", true);
        Field createBy = FieldUtils.getField(aClass, "createBy", true);
        Field createTime = FieldUtils.getField(aClass, "createTime", true);
        FieldUtils.writeField(createTime, t, new Date(), true);
        FieldUtils.writeField(createBy, t, ((LoginUser) SecurityUtils.getSubject().getPrincipal()).getUsername(), true);
        FieldUtils.writeField(id, t, null, true);
        return t;
    }

    @SneakyThrows
    public static <T> T fillUpdateField(T t) {
        Class<?> aClass = t.getClass();
        Field updateBy = FieldUtils.getField(aClass, "updateBy", true);
        Field updateTime = FieldUtils.getField(aClass, "updateTime", true);
        FieldUtils.writeField(updateBy, t, ((LoginUser) SecurityUtils.getSubject().getPrincipal()).getUsername(), true);
        FieldUtils.writeField(updateTime, t, new Date(), true);
        return t;
    }
}
