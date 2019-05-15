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


# jpa 학습 Step - 3 / validate, 예외처리(2)
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-03.md

##@Embeddable / @Embedded

```java
@Embeddable
public class Email {

	@Column(name = "email", nullable = false, unique = true)
	private String value;	

}


public class User extends AbstractEntity{

	@Embedded
	private Email email;

}

public static class SignUpReq {

		@Valid // @Valid 반드시 필요
		private Email email;

        ...
        
		@Builder
		public SignUpReq(@NotEmpty @Size(max = 20) String userId, @NotEmpty String password, @NotEmpty String name, Email email) {
			this.userId = userId;
			this.password = password;
			this.name = name;
			this.email = email;
		}

		public User toEntity() {
			return User.builder()
					.userId(this.userId)
					.password(this.password)
					.name(this.name)
					.email(this.email)
					.build();
		}
	}

```

@Embeddable / @Embedded 를 활용해서 validate 를 적용했지만 다른 관점에서 보자면 @Embeddable 를 통해 관심사가 비슷한 
프로퍼티를 한 클래스에 묶어 관리할 수 있다는게 핵심이 아닐까 싶다. 그냥 내 생각.

임베디드 타입으로 정의되었을때 json 형태 - address, email, name
```json
{
  "address": {
    "address1": "string",
    "address2": "string",
    "zip": "string"
  },
  "email": {
    "address": "string"
  },
  "name":{
    "first": "name",
    "last": "name"
  },
  "password": "string"
}
```

# jpa 학습 Step - 4 / Embedded를 이용한 Password 처리
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-04.md


 ## Embeddable 타입의 Password  클래스 정의
 ### 비밀번호 요구사항
 * 비밀번호 만료 기본 14일 기간이 있다.
 * 비밀번호 만료 기간이 지나는 것을 알 수 있어야 한다.
 * 비밀번호 5회 이상 실패했을 경우 더 이상 시도를 못하게 해야 한다.
 * 비밀번호가 일치하는 경우 실패 카운트를 초기화 해야한다
 * 비밀번호 변경시 만료일이 현재시간 기준 14로 연장되어야한다.
 
 ```java
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

	@Column(name = "password", nullable = false)
	private String value;

	@Column(name = "password_expiratrion_date")
	private LocalDateTime expirationDate;

	@Column(name = "password_failed_count", nullable = false)
	private int failedCount;

	@Column(name = "pasword_ttl")
	private long ttl;

	@Builder
	public Password(final String value) {
//		this.ttl = 2109_604;
		this.ttl = 60 * 60 * 24 * 14; // 14일
		this.value = value;
		this.expirationDate = extendExpirationDate();
	}

	private LocalDateTime extendExpirationDate() {
		return LocalDateTime.now().plusSeconds(ttl);
	}


	public Boolean isMatched(final String password) {

		// 비밀번호 실패 제한이 있을경우
//		if(failedCount > 5) throw new PasswordFaildExceed();

		Boolean result = isMatches(password);

		updateFailedCount(result);

		return result;
	}

	private Boolean isMatches(String password) {
		return this.value.equals(password) ;
	}

	private void updateFailedCount(boolean matches) {
		if (matches)
			resetFailedCount();
		else
			increaseFailedCount();
	}

	private void resetFailedCount() {
		this.failedCount = 0;
	}

	private void increaseFailedCount() {
		this.failedCount++;
	}

	// 비밀번호 유효기간이 정해져있을때...
	public Boolean isExpiration() {
		return LocalDateTime.now().isAfter(this.expirationDate);
	}

	// 비밀번호 변경
	public Boolean changePassword(final String oldPassword) {
		Boolean result = isMatches(oldPassword);
		if(result)  this.expirationDate = extendExpirationDate();

		return result;

	}

}

```
`객체의 변경이나 질의는 반드시 해당 객체에 의해서 이루어져야 한다.
결과적으로 password에 대한 책임이 명확해진다.`
 
 
# jpa 학습 Step - 5 /  OneToMany 관계 설정 팁
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-05.md


Question과 QuestionHistory 는 1:N 관계이고
Question 생성, 수정, 삭제 여부에 따라서 QuestionHistory가 쌓이는 구조이다. 

```java
public class Question extends AbstractEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(nullable = false)
	private String title;

	@OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<QuestionHistory> histories = new ArrayList<>();
	
	....
}

public class QuestionHistory extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private Status status;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false, updatable = false)
	private Question question;

	@Builder
	public QuestionHistory(Status status, Question question) {
		this.status = status;
		this.question = question;
	}
}

```

 ###1:N 관계 팁
 *  Question 통해서 QuestionHistory를 관리함으로 CascadeType.PERSIST 설정을 주었습니다.
 *  1:N 관계를 맺을경우 List를 주로 사용하는데 객체생성을 null 로 하는것 보다는 `new ArrayList<>()`로 설정하는것이 바람직
 하다. 초기화 하기 않았을 경우 기본적인 collection 함수를 사용할 수 가없다. 
 ```java
public class Question extends AbstractEntity{
	....
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    	private List<QuestionHistory> histories = new ArrayList<>();
		
    private void addHistory(Status status) {
         this.histories.add(buildHistory(status));
     }
     
     private QuestionHistory buildHistory(Status status) {
         return QuestionHistory.builder()
                 .status(status)
                 .question(this)
                 .build();
     
     }
 }
``` 
*   CascadeType.PERSIST 설정을 주면 Question에서 QuestionHistory를 저장시킬 수 있습니다. 
이 때 ArrayList 형으로 지정돼 있다면 add 함수를 통해서 쉽게 저장할 수 있습니다. 이렇듯 ArrayList의 다양한 함수들을 사용할 수 있습니다.
*   FetchType.EAGER 통해서 모든 정보를 가져오고 있습니다. 로그 정보가 수십 개 이상일 겨우는 Lazy 로딩을 통해서 가져오는것이 좋지만
3~4개 정도 가정햇을 경우 FetchType.EAGER도 나쁘지 않다고 생각합니다.(내 생각 아님.. ㅋ 성능상 영향은 없을 거 같다.(내 생각 ㅋ))



@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# 영속성 전이 CASCADE
 
>참조 
* <http://wonwoo.ml/index.php/post/1002>
* <https://yellowh.tistory.com/127>

```java
@Entity
@Data
public class Parent {

  @Id @GeneratedValue
  private Long id;

  @OneToMany(mappedBy = "parent")
  private List<Child> children = new ArrayList<>();
}

@Entity
@Data
public class Child {

  @Id @GeneratedValue
  private Long ig;

  @ManyToOne
  private Parent parent;
}


```
#### CascadeType.PERSIST<저장> - 부모를 영속화할 때 연관된 자식들도 함께 영속화 한다
CascadeType.persist는 연관된 엔티티도 같이 영속화하라는 옵션이다.
영속성 전이는 연관관계를 매핑하는 것과는 무관하다. 단지 연관된 엔티티도 같이 영속화하기 위한 기능이다.
```java
@Entity
@Data
public class Parent {

  @Id @GeneratedValue
  private Long id;

  @OneToMany(mappedBy = "parent" ,cascade = CascadeType.PERSIST)
  private List<Child> children = new ArrayList<>();
}
```
```java
private static void saveWithCascade(EntityManager entityManager){
  Child child1 = new Child();
  Child child2 = new Child();

  Parent parent = new Parent();
  child1.setParent(parent);
  child2.setParent(parent);

  parent.getChildren().add(child1);
  parent.getChildren().add(child2);

  entityManager.persist(parent);
}
```

#### CascadeType.REMOVE<삭제> - 부모 엔티티와 연관된 자식 엔티티도 함께 삭제 한다
 ```java
@OneToMany(mappedBy = "parent" ,cascade = CascadeType.REMOVE)
private List<Child> children = new ArrayList<>();
 ```
 ```java
private static void deleteWithCascade(EntityManager entityManager){
  Parent parent = entityManager.find(Parent.class, 1L);
  entityManager.remove(parent);
}
```
CascadeType.REMOVE 를 설정하지 않고 부모엔티티만 삭제하려 했다면 외뢰키 제약조건으로 예외가 발생한다.

#### CascadeType.DETACH - 부모 엔티티가 detach()를 수행하게 되면, 연관된 엔티티도 detach() 상태가 되어 변경사항이 반영되지 않는다
#### CascadeType.MERGE - 트랜잭션이 종료되고 detach 상태에서 연관 엔티티를 추가하거나 변경된 이후에 부모 엔티티가 merge()를 수행하게 되면 변경사항이 적용된다.(연관 엔티티의 추가 및 수정 모두 반영됨)
#### CascadeType.ALL – 모든 Cascade 적용
