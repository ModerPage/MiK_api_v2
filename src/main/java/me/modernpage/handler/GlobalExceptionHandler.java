//package me.modernpage.handler;
//
//import me.modernpage.utils.ResponseUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.NoSuchElementException;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//	@ExceptionHandler(NoSuchElementException.class)
//	@ResponseStatus(value = HttpStatus.NOT_FOUND)
//	public ResponseUtils handleNSException(HttpServletRequest request, Exception ex) {
//		return ResponseUtils.response(404, ex.getMessage(), null);
//	}
//
//	@ExceptionHandler(Throwable.class)
//	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//	public ResponseUtils handleOtherException(HttpServletRequest request, Exception ex) {
//	    return ResponseUtils.response(500, ex.getMessage(), null);
//	}
//}
