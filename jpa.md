# jpa 학습 1일차
https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-01.md

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