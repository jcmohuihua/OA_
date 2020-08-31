package com.web.oa.exception;

import java.io.Serializable;

/**
 * @author mhh
 * 2020/8/29 0029 - 上午 10:11
 * <p>
 * 自定义异常类
 */
public class BusinessException extends RuntimeException implements Serializable {

    public BusinessException(Object obj) {
        super(obj.toString());
    }
}
