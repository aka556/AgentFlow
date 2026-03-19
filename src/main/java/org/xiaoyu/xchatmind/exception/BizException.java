package org.xiaoyu.xchatmind.exception;

import lombok.Getter;

/**
 * @author xiaoyu
 * @desciption 自定义异常
 */
@Getter
public class BizException extends RuntimeException {
    private final int code;

    public BizException(String message) {
        super(message);
        this.code = 400;
    }
}
