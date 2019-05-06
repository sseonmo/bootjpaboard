package me.seon.bootjpaboard.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorExceptionController {

	private final Logger logger = LoggerFactory.getLogger(ErrorExceptionController.class);


	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	protected  ErrorResponse handleMethodArgmentNotValidExcption(MethodArgumentNotValidException e) {

		logger.error(e.getMessage());

		BindingResult bindingResult = e.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();


		return buildFieldErrors(
				ErrorCode.INPUT_VALUE_INVALID,
				fieldErrors.parallelStream()
						.map(error -> ErrorResponse.FieldError.builder()
								.field(error.getField())
								.value((String) error.getRejectedValue())
								.reason(error.getDefaultMessage())
								.build())
						.collect(Collectors.toList())
		);

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
