# jpa 학습 Step - 1
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-01.md

    1. 제약조건 맞추기 - nullable, unique 조건등 db 스키마와 동일한 calss 구조를 만드는 것이 바람직하다.
    2. 생성시간, 수정시간 - @CreationTimestamp, @UpdateTimestamp, @Temporal(TemporalType.TIMESTAMP) 활용하기
                            Date Type지원, LocalDateTime Type은 지원안됨.
                            LocalDateTime는 @CreateDate, @ModifiedDate 이용
    3. 외부에서 접근자로 객체를 생성할수 없게 하기
       @NoArgsConstructor(access = AccessLevel.PROTECTED)
    4. @Builder 을 이용한 유연한 객체 생성 - 순서상관없이 생성됨.
    5. DTO를 이용한 Request, Response 명확하게하기
    6. @Setter 사용안하기 - 명확함.
        // setter 이용 방법
        public Account updateMyAccount(long id) {
            final Account account = findById(id);
            account.setAddress1("변경...");
            account.setAddress2("변경...");
            account.setZip("변경...");
            return account;
        }
        // Dto 이용 방법
        public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
          final Account account = findById(id);
          account.updateMyAccount(dto);
          return account;
        }
        // Account 클래스의 일부
        public void updateMyAccount(AccountDto.MyAccountReq dto) {
          this.address1 = dto.getAddress1();
          this.address2 = dto.getAddress2();
          this.zip = dto.getZip();
        } 
        
**객체 자신을 변경하는것은 객체 자신이 되야한다.**

# jpa 학습 Step - 2 / validate, 예외처리
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-02.md

## 중요포인트
* @Vaild를 통한 유효성감사
* @ControllerAdvice를 이용한 Exception 핸들링
* ErrorCode 에러 메시지 통합


## @Vaild를 통한 유효성검사

```java
    @Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class CreateReq {

		@NotEmpty
		private String contents;
		
		...

	}
```
Dto class에 @NotEmpty 어노테이션 추가 


```java
	@PostMapping("")
	public AnswerDto.Res create(@PathVariable("question") Long questionId, @RequestBody @Valid final AnswerDto.CreateReq createReq, HttpSession session) {
		logger.info("create question : [{}] / contents : [{}]", questionId, createReq);

		if(!HttpSessionUtil.isLoginUser(session)) return null;

		return new AnswerDto.Res(service.create(questionId, createReq, session));

	}
```
Controller 단에서 @Valid 를 통해 유효성검사가 실패할 경우 `MethodArgumentNotValidException` 예외가 발생
    

## @ControllerAdvice 를 이용한 Exception 핸들링

```java

@ControllerAdvice
public class ErrorExceptionControllerAdvice {
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected  String handleMethodArgmentNotValidExcption(MethodArgumentNotValidException e, HttpServletRequest request) {

		logger.error("handleMethodArgmentNotValidExcption message [{}]", e.getMessage());
		
		ErrorResponse  errorResponse = 
		...
        
                request.setAttribute("error", errorResponse);
        		
		return  "forward:/error/handler";
	}
	
}

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

```
@ControllerAdvice를 이용한 전역적인 Exception 를 처리 하였고
ErrorExceptionController controller 에서 contentType 으로 html요청과 json 요청을 구분하였다.

### ErrorResponse : 공통 예외 Response
```java

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private String code;
    private int status;
    private List<FieldError> errors;
    ...

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
        ...
    }
}
```

### AccountNotFoundException : 새로운 Exception 정의
```java
public class BasicException extends  RuntimeException{
	public BasicException() {
	}
	
	...	
}

public class AccountNotFountException extends BasicException {

	private String id;

	public AccountNotFountException(String id) {
		this.id = id;
	}
}


@PostMapping("/login")
public String login(User user, HttpSession session, Model model) {

    User loginUser = repository.findByUserId(user.getUserId()).orElseThrow(() -> new AccountNotFountException(user.getUserId()));
    ...
    return  "/user/login";
}

```

###  basicExcpetionHeandler 핸들링
```java
	@ExceptionHandler(BasicException.class)
	protected  String basicExcpetionHeandler(BasicException be, HttpServletRequest request) {

		logger.error("BasicException message [{}]", be.getMessage());

		if(be instanceof AccountNotFountException)
			request.setAttribute("error", buildErrors(ErrorCode.ACCOUNT_NOT_FOUNT));

		return  "forward:/error/handler";
	}
```

사용자 정의 예외을 기본적으로 BasicException를 상속받게하고 insertof를 통해 하나의 method 에서 처리하도록 함.  
 
 
 ### ErrorCode
 ```java
@Getter
public enum ErrorCode {

    ACCOUNT_NOT_FOUND("AC_001", "해당 회원을 찾을 수 없습니다.", 404);    

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
```

####@NotEmpty 외 다른 어노테이션    
    @AssertFalse  거짓인지?        
    @AssertTrue 참인지?
    @DecimalMax 지정 값 이하의 실수인지?
    @DecimalMin 지정 값 이상의 실수인지?
    @Digits(integer=,fraction=) 정수 여부
    @Future 미래 날짜인지?
    @Max 지정 값 이상인지?
    @Min 지정 값 이하인지?
    @NotNull  Null이 아닌지?
    @Null Null인지?
    @Pattern(regex=,flag=) 정규식을 만족하는지?
    @Past   과거날짜인지?
    @Size(min=,max=)  문자열 또는 배열등의 길이 만족 여부
