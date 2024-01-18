package com.laioffer.twitch;

import com.laioffer.twitch.model.TwitchErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
//所有标记为controller 的class 里面的function call都会被执行,去看一下有没有exception,去catch一下
public class GlobalControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    //这个是general的exception, 把我们没有专门写specific response的exception也catch住
    public final ResponseEntity<TwitchErrorResponse> handleDefaultException(Exception e) {
        logger.error("", e);
        return new ResponseEntity<>(
                new TwitchErrorResponse("Something went wrong, please try again later.",
                        e.getClass().getName(),
                        e.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(ResponseStatusException.class)
    //这个是处理specific的exception, 因为不是处理一个instance, 是处理一个class,所以后面要加.class
    public final ResponseEntity<TwitchErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        logger.error("", e.getCause());
        return new ResponseEntity<>(
                new TwitchErrorResponse(e.getReason(), e.getCause().getClass().getName(), e.getCause().getMessage()),
                e.getStatusCode()
        );
    }
}

