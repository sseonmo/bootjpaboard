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

 ### 1:N 관계 팁
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

# step-06: Setter 사용하지 않기
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-06.md

이미 여러번 참조하는 필자가 해왔던 애기를 정리해서 다시한번 설명하는 느낌 이랄까... 
습관처럼 사용하는 setter의 단점과 setter를 이용하지 않고 도메인 객체를 변경하는 방법을 소개

### Setter 메소드는 의도를 갖기 힘들다.
중구난방으로 사용할 수 있는 setter 를 사용하면 어떤의미 파악하기 위해서  함께 사용하는  setter 조합이나 setter를 사용하는 메서드를 
보고 판단할 수 있다. 하지만 도메인 클래스의 의미을 파악할 수 있는 메소드를 이용해 필요한 지역변수를 변경하면
의도가 명확하게 판단할 수 있다.(도메인은 변경은 도메인 클레스에서 이루어 지는게 바람직하다.)   
```java 
//setter 이용
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.setAddress("value");
    account.setFistName("value");
    account.setLastName("value");
    return account;
}

//도메인 클래스 method 이용
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.updateMyAccount(dto);
    return account;
}

// Account 도메인 클래스
public void updateMyAccount(AccountDto.MyAccountReq dto) {
    this.address = dto.getAddress();
    this.fistName = dto.getFistName();
    this.lastName = dto.getLastName();
}

```

그리고 변경될 값에 대한 명확한 명세가 있은 DTO를 두는 것이 바람직합니다.
```java
public static class MyAccountReq {
		private Address address;
		private String firstName;
		private String lastName;
}
```
### Setter 메소드는 사용하지 않기

#### updateMyAccount
```java
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.updateMyAccount(dto);
    return account;
}
// Account 도메인 클래스
public void updateMyAccount(AccountDto.MyAccountReq dto) {
    this.address = dto.getAddress();
    this.fistName = dto.getFistName();
    this.lastName = dto.getLastName();
}
```
위의 예제와 같은 예제 코드입니다. findById 메소드를 통해서 영속성을 가진 객체를 가져오고 도메인에 작성된 updateMyAccount를 통해서 업데이트를 진행하고 있습니다.

repository.save() 메소드를 사용하지 않았습니다. 
다시 말해 메소드들은 객체 그 자신을 통해서 데이터베이스 변경작업을 진행하고, create 메서드에 대해서만 repository.save()를 사용합니다

#### create
save 메소드에는 도메인 객체가 필요하다. 
dto 클래스에  toEntity 메소드를 이용하여 도메인 객체를 생성하여 save 한다.(@builder 패턴이용)

```java
// 전체 코드를 보시는 것을 추천드립니다.
public static class SignUpReq {

	private com.cheese.springjpa.Account.model.Email email;
	private Address address;

	@Builder
	public SignUpReq(Email email, String fistName, String lastName, String password, Address address) {
        this.email = email;
        this.address = address;
	}

	public Account toEntity() {
        return Account.builder()
            .email(this.email)
            .address(this.address)
            .build();
	}
}

public Account create(AccountDto.SignUpReq dto) {
    return accountRepository.save(dto.toEntity());
}
```


# step-07: Embedded를 적극활용
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-07.md

Embedded를 사용하면 칼럼들을 자료형으로 규합해서 응집력 및 재사용성을 높여 훨씬 더 객체지향 프로그래밍을 할수 있게 도울 수 있다. 
그리고 또 하나 객체에게 명확한 책임을 역활을 나눌수 있다.

### 풍부한 객체(Rich Object)
```java

public class Email {
    ...
    public String getHost() {
        int index = value.indexOf("@");
        return value.substring(index);
    }

    public String getId() {
        int index = value.indexOf("@");
        return value.substring(0, index);
    }
}

``` 

### 재사용성
가령 해외 송금을 하는 기능이 있다고 가정할 경우 Remittance 클래스는 보내는 금액, 나라, 통화, 받는 금액, 나라, 통화가 필요하다. 이처럼 도메인이 복잡해질수록 더 재사용성은 중요합니다.

```java
class Remittance{
    //자료형이 없는 경우
    @Column(name = "send_amount") private double sendAamount;
    @Column(name = "send_country") private String sendCountry;
    @Column(name = "send_currency") private String sendCurrency;

    @Column(name = "receive_amount") private double receiveAamount;
    @Column(name = "receive_country") private String receiveCountry;
    @Column(name = "receive_currency") private String receiveCurrency;

    //Money 자료형
    private Money snedMoney;
    private Money receiveMoney;
}
class Money {
    @Column(name = "amount", nullable = false, updatable = false) private double amount;
    @Column(name = "country", nullable = false, updatable = false) private Country country;
    @Column(name = "currency", nullable = false, updatable = false) private Currency currency;
}

```

위처럼 Money라는 자료형을 두고 금액, 나라, 통화를 두면 도메인을 이해하는데 한결 수월할 뿐만 아니라 수많은 곳에서 재사용 할 수 있습니다. 사용자에게 해당 통화로 금액을 보여줄 때 소숫자리 몇 자리로 보여줄 것인지 등등 핵심 도메인일수록 재사용성을 높여 중복 코드를 제거하고 응집력을 높일 수 있습니다.  


# step-08: OneToOne 관계 설정 팁
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-08.md

OneToOne 관계 설정 시에 간단한 팁을 정리하겠습니다. 해당 객체들의 성격은 다음과 같습니다.

*   주문과 쿠폰 엔티티가 있다.
*   주문 시 쿠폰을 적용해서 할인받을 수 있다.
*   주문과 쿠폰 관계는 1:1 관계 즉 OneToOne 관계이다.

주의 깊게 살펴볼 내용은 다음과 같습니다.

*   외래 키는 어디 테이블에 두는 것이 좋은가?
*   양방향 연관 관계 편의 메소드
*   제약 조건으로 인한 안정성 및 성능 향상

`외래키를 어디 테이블이 같은것일 좋을까??`
**JPA상 외래키를 같는 쪽이 연관 관계의 주인이되고 연관 관계의 주인만이 데이터베이스 연관 관계와 매핑되어 외래 키를 관리(등록, 수정, 삭제) 할 수 있다.**

### Order가 주인일때
#### 장점 - insert sql를 한번 실행
 
```java 
// order가 연관 관계의 주인일 경우 SQL
insert into orders (id, coupon_id, price) values (null, ?, ?) 

//coupon이 연관 관계의 주인일 경우 SQL
insert into orders (id, price) values (null, ?)
update coupon set discount_amount=?, order_id=?, use=? where id=?
```
(coupon는 이미 등록되어있음)
order 테이블에 coupon_id 칼럼을 저장하기 때문에 주문 SQL은 한 번만 실행됩니다. 
반면에 coupon이 연관 관계의 주인일 경우에는 coupon에 order의 외래 키가 있으니 order INSERT SQL 한 번, 
coupon 테이블에 order_id 칼럼 업데이트 쿼리 한번 총 2번의 쿼리가 실행됩니다.

작은 장점으로는 데이터베이스 칼럼에 coupon_id 항목이 null이 아닌 경우 할인 쿠폰이 적용된 것으로 판단할 수 있습니다.

#### 단점 - 연관 관계 변경 시 취약

기존 요구사항은 주문 한 개에 쿠폰은 한 개만 적용 이 가는 했기 때문에 OneToOne 연관 관계를 맺었지만 
하나의 주문에 여러 개의 쿠폰이 적용되는 기능이 추가되었을 때 변경하기 어렵다는 단점이 있습니다.
(OneToOne => OneToMany 구조변경 어려움(migration 이슈 발생) ) 

### 연관 관계의 주인 설정

**주인 설정이라고 하면 뭔가 더 중요한 것이 주인이 되어야 할 거 같다는 생각이 들지만 
연관 관계의 주인이라는 것은 왜래 키의 위치와 관련해서 정해야 하지 해당 도메인의 중요성과는 상관관계가 없습니다.**

OneToOne 관계를 맺으며 외래키를 어디에 둘건인지를 확장성을 고려해 Many로 변경할 수도 잇는 객체가 가지는 것이 좀 더 좋치 않을까싶다.

### 양방향 연관관계 편의 메소드

```java

// Order가 연관관계의 주인일 경우 예제
class Coupon {
    ...
    // 연관관계 편의 메소드
    public void use(final Order order) {
        this.order = order;
        this.use = true;
    }
}

class Order {
    private Coupon coupon; //(1)
    ...
    // 연관관계 편의 메소드
    public void applyCoupon(final Coupon coupon) {
        this.coupon = coupon;
        coupon.use(this);
        price -= coupon.getDiscountAmount();
    }
}

// 주문 생성시 1,000 할인 쿠폰 적용
public Order order() {
    final Order order = Order.builder().price(1_0000).build(); // 10,000 상품주문
    Coupon coupon = couponService.findById(1); // 1,000 할인 쿠폰
    order.applyCoupon(coupon);
    return orderRepository.save(order);
}

```
연관 관계의 주인이 해당 참조할 객체를 넣어줘야 데이터베이스의 칼럼에 외래 키가 저장됩니다. 즉 Order가 연관 관계의 주인이면 (1)번 멤버 필드에 Coupon을 넣어줘야 데이터베이스 order 테이블에 coupon_id 칼럼에 저장됩니다.

양방향 연관 관계일 경우 위처럼 연관 관계 편의 메소드를 작성하는 것이 좋습니다. 위에서 말했듯이 연관 관계의 주인만이 왜래 키를 관리 할 수 있으니 applyCoupon 메소드는 이해하는데 어렵지 않습니다.

### 제약 조건으로 인한 안정 성 및 성능 향상

```java
public class Order {
    ...

    @OneToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "id", nullable = false)
    private Coupon coupon;
}

```

모든 주문에 할인 쿠폰이 적용된다면 @JoinColumn의 nullable 옵션을 false로 주는 것이 좋습니다. NOT NULL 제약 조건을 준수해서 안전성이 보장됩니다.

* nullable = false , inner join
* nullable = true  , outer join

**외래 키에 NOT NULL 제약 조건을 설정하면 값이 있는 것을 보장합니다. 
따라서 JPA는 이때 내부조인을 통해서 내부 조인 SQL을 만들어 주고 이것은 외부 조인보다 성능과 최적화에 더 좋습니다.**

# step-09: OneToMany 관계 설정 팁(2)
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-09.md

### CasCade PERSIST 설정
헷갈리지 말자!! PERSIST 설정은 부모 엔티티가 영속화 될때 관련된 다른 엔티티도 같이 영속화 하는 옵션이다.
**연관관계랑은 상관이 없어요**
```java
 // cascade PERSIST 설정 안 했을 경우
 insert into question (id, create_at, create_date, modify_date, update_at, contents, del_yn, title, writer_id) values (null, ?, ?, ?, ?, ?, ?, ?, ?)

 // cascade PERSIST 설정 했을 경우
 insert into question (id, create_at, create_date, modify_date, update_at, contents, del_yn, title, writer_id) values (null, ?, ?, ?, ?, ?, ?, ?, ?)
 insert into question_history (id, create_at, create_date, modify_date, update_at, question_id, status) values (null, ?, ?, ?, ?, ?, ?)
 
```

## 고아 객체 (orphanRemoval)
JPA는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공한다. 이를 고아객체 제거라고 한다.
이 기능은 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제 돼서 개발의 편리함이 있습니다.
**연관관계랑은 상관이 없어요**

```java
 // question를 삭제하려고 할때
 
 // orphanRemoval=false - FOREIGN KEY 에러발생
 org.h2.jdbc.JdbcSQLException: Referential integrity constraint violation: "FKJE5JKPP1J8JR08KQN8J8O8O6E: PUBLIC.QUESTION_HISTORY FOREIGN KEY(QUESTION_ID) REFERENCES PUBLIC.QUESTION(ID) (1)"; SQL statement:

 // orphanRemoval=true
 delete from question_history where id=?
 delete from question where id=?
 
```
Question, QuestionHistory 는 참조 관계를 맺고 잇어 Question 만 삭제할 수 없습니다. 
orphanRemoval 설정이 되어있는 경우 쉽게 삭제 가능하지만,
rphanRemoval 설정이 없는 경우 그 작업을 선행하지 않아 FOREIGN KEY 에러발생 한다.

# step-10: Properties 설정값
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-10.md

### 추천패턴 - ConfigurationProperties
```java
@Configuration
@ConfigurationProperties(prefix = "user")
@Validated
public class SampleProperties {
    @Email
    private String email;
    @NotEmpty
    private String nickname;
    private int age;
    private boolean auth;
    private double amount;

    // getter, setter
}

public class SamplePropertiesRunner implements ApplicationRunner {
    private final SampleProperties properties;
    @Override
    public void run(ApplicationArguments args)  {
        final String email = properties.getEmail();
        final String nickname = properties.getNickname();
        final int age = properties.getAge();
        final boolean auth = properties.isAuth();
        final double amount = properties.getAmount();

        log.info("==================");
        log.info(email); // yun@test.com
        log.info(nickname); // yun
        log.info(String.valueOf(age)); // 27
        log.info(String.valueOf(auth)); // true
        log.info(String.valueOf(amount)); // 100.0
        log.info("==================");
    }
}
```
아주 간단하고 명확한 해결 방법은 ConfigurationProperties를 이용해서 POJO 객체를 두는 것입니다. 장점들은 다음과 같습니다.

#### Validation
```java
user:
  email: "yun@" // 이메일 형식 올바르지 않음 -> @Email
  nickname: "" // 필수 값 -> @NotEmpty

```
JSR-303 기반으로 Validate 검사를 할 수 있습니다. 위 코드 처럼 @Validated, @Email 어노테이션을 이용하면 쉽게 유효성 검사를 할 수 있습니다.

```java
Binding to target com.cheese.springjpa.properties.SampleProperties$$EnhancerBySpringCGLIB$$68016904@3cc27db9 failed:

    Property: user.email
    Value: yun@
    Reason: 이메일 주소가 유효하지 않습니다.

Binding to target com.cheese.springjpa.properties.SampleProperties$$EnhancerBySpringCGLIB$$d2899f85@3ca58cc8 failed:

    Property: user.nickname
    Value: 
    Reason: 반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다.
```
**위와 같이 컴파일 과정 중에 잡아주고 에러 메시지도 상당히 구체적입니다.**

#### 빈으로 등록 해서 재사용성이 높음
```java
public class SampleProperties {
    @Email
    private String email;
    @NotEmpty
    private String nickname;
    private int age;
    private boolean auth;
    private double amount;

    // getter, setter 
    // properties 사용할 떄는 SampleProperties 객체를 사용함, 데이터의 응집력, 캡슐화가 높아짐
}
```

#### 유연한 바인딩과 yml 또는 properties 파일에 작성시 자동으로 SampleProperties 연관된 속성들을 찾아준다. 

#### pom.xml dependency
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

# step-11: Properties environment 설정하기
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-11.md

#### profiles 를 통한 환경별 설정 팁

| environment | 설명      | 파일명                   |
| ----------- | ------- | --------------------- |
| local       | 로컬 개발환경 | application-local.yml |
| dev         | 개발환경    | application-dev.yml   |
| prod        | 운영      | application-prod.yml  |



## application.yml
```yml
server:
  port: 8080
```
* 모든 환경에서 공통으로 사용할 정보들을 작성합니다.
* 모든 환경에서 사용할 것을 공통으로 사용하기 때문에 코드의 중복과 변경에 이점이 있습니다.
* 본 예제에서는 port만 공통으로 설정했습니다.

## application-{env}.yml

```yml
user:
  email: "yun@test"
  nickname: "nickname"
  age: 28
  auth: false
  amount: 101

spring:
  jpa:
    database: h2
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  datasource:
    data: classpath:init.sql # 시작할때 실행시킬 script

  jackson:
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false

logging:
  level:
    ROOT: info
```
* 각 개발환경에 맞는 properties 설정을 정의합니다.
* 대표적으로 데이터베이스 정보, 외부 설정 정보 등이 있습니다.
* `application.yml` 에서 정의한 `server.port` 8080 값이 자동으로 설정됩니다.

## env 설정 방법

### application.yml에서 설정하는 방법
```yml
spring:
  profiles:
    active: local

server:
  port: 8080
```
#### 실행
* `profiles.active` 속성에 원하는 정보 env를 작성합니다.(intellij)
* -Dspring.profiles.active={env}
         
### 우선순위
외부 환경 설정에 대한 우선순위는 [Spring-Boot Document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config)에 표시되어 있습니다. 실제 배포시에는 우선순위를 반드시 고려해야합니다.

# step-12: 페이징 API 만들기
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/step-13/doc/step-12.md

## Simple code
```java

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionRepository repository;

	@GetMapping("")
	public Page<Question> findAll(Pageable  pageable) {
		return repository.findAll(pageable);
	}
}

```
Controller 에서 Pageable 인터페이스를 받고 repository 메서드 findAll(pageable)로 넘기기만 하면됩니다.


#### 응답
```java
{
  "content": [
    {
      "@id": 1,
      "createDate": "2019-06-03 06:56:40",
      "modifyDate": "2019-06-03 06:56:40",
      "createAt": "2019-06-02T21:56:40.475+0000",
      "updateAt": "2019-06-02T21:56:40.475+0000",
      "id": 12,
      "writer": {
        "createDate": "2019-06-03 06:56:40",
        "modifyDate": "2019-06-03 06:56:40",
        "createAt": "2019-06-02T21:56:40.353+0000",
        "updateAt": "2019-06-02T21:56:40.353+0000",
        "id": 1,
        "userId": "seonmo",
        "password": {
          "value": "pass",
          "expirationDate": "2019-06-17T06:56:40.248",
          "failedCount": 0,
          "ttl": 1209600,
          "expiration": false
        },
        "name": "선모",
        "email": {
          "value": "seonmo@gmila.com"
        },
        "formattedCreateDate": "2019-06-03 06:56:40",
        "formattedModifyDate": "2019-06-03 06:56:40"
      },
      "answers": [],
      "title": "testTitle11",
      "histories": [
        {
          "createDate": "2019-06-03 06:56:40",
          "modifyDate": "2019-06-03 06:56:40",
          "createAt": "2019-06-02T21:56:40.477+0000",
          "updateAt": "2019-06-02T21:56:40.477+0000",
          "id": 12,
          "status": "CREATE",
          "formattedCreateDate": "2019-06-03 06:56:40",
          "formattedModifyDate": "2019-06-03 06:56:40"
        }
      ],
      "delYn": "N",
      "contents": "testContent11",
      "formattedCreateDate": "2019-06-03 06:56:40",
      "formattedModifyDate": "2019-06-03 06:56:40"
    },
    ...
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "pageSize": 5,
    "pageNumber": 0,    // 현재 페이지 0부터 시작
    "unpaged": false,
    "paged": true
  },
  "totalPages": 3,      // 총 페이지
  "totalElements": 12,  // 총 게시물 개수 
  "last": false,        // 마지막 페이지 여부
  "number": 0,          // 현재 페이지 0부터 시작
  "size": 5,            // 페이지 당 보여주는 게시물 개수
  "sort": {     
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 5,    
  "first": true,            // 첫페이지 여부
  "empty": false            // 리스트가 비어있는지 여부
}
```

## 개선
위의 Pageable의 개선할 점이 있습니다. 우선 size에 대한 limit이 없습니다. 위의 API에서 size값을 200000을 넘기면 실제 데이터베이스 쿼리문이 200000의 조회할 수 있습니다. 그 밖에 page가 0 부터 시작하는 것들도 개선하는 것이 필요해 보입니다.

#### PageRequest
```java
public class PageRequest {

	private int page;

	private int size;

	private Sort.Direction direction;

	public void setPage(int page) {
		this.page = page <= 0 ? 1 : page;
	}

	public void setSize(int size) {
		int DEFAULT_SIZE = 5;
		int MAX_SIZE = 20;

		this.size = size > MAX_SIZE ? MAX_SIZE : size;
	}

	public void setDirection(Sort.Direction direction) {
		this.direction = direction;
	}

	public org.springframework.data.domain.PageRequest of() {
		return org.springframework.data.domain.PageRequest.of(page -1, size, direction, "createDate");
	}
}
```

##### Pageable을 대체하는 PageRequest 클래스를 작성합니다. 
적당히 type safe 하게.. 

* setPage(int page) 메서드를 통해서 0보다 작은 페이지를 요청했을 경우 1 페이지로 설정합니다.
* setSize(int size) 메서드를 통해서 요청 사이즈 50 보다 크면 기본 사이즈인 10으로 바인딩 합니다.
* of() 메서트를 통해서 PageRequest 객체를 응답해줍니다. 페이지는 0부터 시작하니 page -1 합니다. 본 예제에서는 sort는 createdAt 기준으로 진행합니다.

#### Controller
```java
@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionRepository repository;

	@GetMapping("")
	public Page<Question> findAll(final PageRequest pageable) {
		return repository.findAll(pageable.of());
	}
}
```


# step-13: Query Dsl이용한 페이징 API 만들기
참조 - https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-13.md

## 기초작업

```java
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
</dependency>

<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>

<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>

```
com.querydsl.apt.jpa.JPAAnnotationProcessor로 Entity들을 compile하여 outputDirectory 위치시킨다.  


## Controller
```java
@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class ApiQuestionController {

	private QuestionService service;

	@GetMapping("")
	public Page<Question> findAll(
			@RequestParam(name = "type") final QuestionSearchType type,
			@RequestParam(name = "value", required = false) final String value,
			final PageRequest pageRequest) {

		return service.findAll(type, value, pageRequest.of());
	}
	
	public enum QuestionSearchType {
    	TITLE, USERNM, ALL
    }
}

```

## Service
```java
@Service
@Transactional
public class QuestionService extends QuerydslRepositorySupport  {

	@Resource
	private QuestionRepository repository;

	public QuestionService() {
		super(Question.class);
	}

	public Page<Question> findAll(final QuestionSearchType type, final String value, final Pageable pageable) {

		final QQuestion question = QQuestion.question;
		final JPQLQuery<Question> query;

		switch (type) {

			case TITLE:
				query = from(question)
						.where(question.title.likeIgnoreCase(value+"%"));
				break;

			case USERNM:
				query = from(question)
						.where(question.writer.userId.likeIgnoreCase(value+"%"));
				break;

			case ALL:
				query = from(question).fetchAll();
				break;

			default:
				throw  new IllegalArgumentException();

		}

		final List<Question> questions = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(questions, pageable, query.fetchCount());
	}
}

```
QuerydslRepositorySupport를 이용하면 동적 쿼리를 쉽게 만들수 있습니다. 객체 기반으로 쿼리를 만드는 것이라서 타입 세이프의 강점을 그대로 가질 수 있습니다. QuerydslRepositorySupport 추상 클래스를 상속 받고 기본 생성자를 통해서 조회 대상 엔티티 클래스를 지정합니다.
Querydsl를 가장 큰 장점은 *타입세이프* 인 것 같다. 


@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
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
