package org.xiaoyu.xchatmind.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.xiaoyu.xchatmind.model.common.ApiResponse;

/**
 * @author xiaoyu
 * @desciption 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获业务异常，返回前端
     */
    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(BizException e) {
        return ApiResponse.error(e.getMessage());
    }

    /**
     * 捕获404错误
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handle404(NoResourceFoundException e) {
        return ResponseEntity.notFound().build();
    }

    /**
     * 捕获所有未处理的异常, 对前端不返回错误信息
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return ApiResponse.error("服务器内部错误");
    }
}
