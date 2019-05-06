package me.seon.bootjpaboard.error;

import me.seon.bootjpaboard.exception.AccountNotFountException;
import me.seon.bootjpaboard.exception.BasicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorExceptionControllerAdvice {

	private final Logger logger = LoggerFactory.getLogger(ErrorExceptionControllerAdvice.class);


	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected  String handleMethodArgmentNotValidExcption(MethodArgumentNotValidException e, HttpServletRequest request) {

		logger.error("handleMethodArgmentNotValidExcption message [{}]", e.getMessage());
		logger.info("mediaType [{}]", request.getContentType());


		BindingResult bindingResult = e.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();


		ErrorResponse errorResponse = buildFieldErrors(
				ErrorCode.INPUT_VALUE_INVALID,
				fieldErrors.parallelStream()
						.map(error -> ErrorResponse.FieldError.builder()
								.field(error.getField())
								.value((String) error.getRejectedValue())
								.reason(error.getDefaultMessage())
								.build())
						.collect(Collectors.toList())
		);


		request.setAttribute("error", errorResponse);

		return  "forward:/error/handler";
	}


	@ExceptionHandler(BasicException.class)
	protected  String basicExcpetionHeandler(BasicException be, HttpServletRequest request) {

		logger.error("BasicException message [{}]", be.getMessage());


		if(be instanceof AccountNotFountException)
			request.setAttribute("error", buildErrors(ErrorCode.ACCOUNT_NOT_FOUNT));


		return  "forward:/error/handler";
	}

	private ErrorResponse buildErrors(ErrorCode errorCode) {
		return ErrorResponse.builder()
				.code(errorCode.getCode())
				.status(errorCode.getStatus())
				.message(errorCode.getMessage())
				.build();
	}

	private ErrorResponse buildFieldErrors(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
		return ErrorResponse.builder()
				.code(errorCode.getCode())
				.status(errorCode.getStatus())
				.message(errorCode.getMessage())
				.errors(errors)
				.build();
	}
}
