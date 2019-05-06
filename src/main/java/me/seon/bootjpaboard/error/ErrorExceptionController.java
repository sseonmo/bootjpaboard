package me.seon.bootjpaboard.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class ErrorExceptionController {

	private static final Logger logger = LoggerFactory.getLogger(ErrorExceptionController.class);


	@RequestMapping(value = "/handler", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ResponseBody
	protected ErrorResponse handlerForJson(HttpServletRequest request) {
		logger.error("ErrorExceptionController forJson : [{}]",  request.getAttribute("error"));
		return (ErrorResponse)request.getAttribute("error");
	}


	@RequestMapping("/handler")
	protected ModelAndView handlerForHtml(HttpServletRequest request, ModelAndView modelAndView) {
		logger.error("ErrorExceptionController forHtml : [{}]",  request.getAttribute("error"));

		modelAndView.addObject("error", request.getAttribute("error"));
		modelAndView.setViewName("/error/error");
		return modelAndView;
	}


}
