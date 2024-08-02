
# 중고 책 대여 시스템 

### 채팅 서비스 github 링크
https://github.com/jinheung90/chatting-base

### 기술 스택
spring boot, jpa, querydsl, redis, flyway etc...

### AWS
rds, elasticache, ecs, ecr, parameter store, aws privateLink, fargate ... 

https://docs.aws.amazon.com/ko_kr/AmazonECS/latest/developerguide/networking-inbound.html
![ecs.png](ecs.png)

### deploy
![deploy.png](..%2F..%2FDesktop%2Fdeploy.png)

추가 설명

배포하는 중 테스트를 하기 위한 대기 시간을 설정할 수 있으며 8080으로 테스트 한 후에 
교체가 되면 기존 연결을 바꾼다.


## sequenceDiagram

### 카카오 로그인 

```mermaid
sequenceDiagram
        
    actor 유저
    participant 시작화면
    participant 서버
    participant 카카오서버
    participant aws sns
    participant redis
    

    유저 ->> 서버: 휴대폰 번호 전송, 서버측은 휴대폰 번호 유효성 검사
    서버 ->> aws sns: 문자 메세지 전송 요청
    aws sns ->> 유저: 휴대폰 인증번호 6자리 전송
    서버 ->> redis: 인증 번호, 번호 저장 3분 후 flush
    유저 ->> 서버: 인증 번호 6자리 입력 및 전송
    서버 ->> redis: 휴대폰 번호를 통해 인증번호 가져옴
    redis ->> 서버: 응답, 서버는 인증번호 검증
    서버 ->> 유저: 휴대폰 인증 확인 임시토큰 보내줌
    유저 ->> 서버: 유저 프로필 입력
    유저 ->> 시작화면: 카카오 회원 가입 또는 일반 회원가입
    
    시작화면 ->> 카카오서버: authorization_code 호출
    카카오서버 ->> 시작화면: authorization_code 발급
    시작화면 ->> 서버: authorization_code
    서버 ->> 카카오서버: 카카오 access_token 요청
    카카오서버 ->> 서버: 카카오 access_token 발급
    서버 ->> 카카오서버: 카카오 프로필 요청
    카카오서버 ->> 서버: 카카오 프로필 전송 scope [email, kakao_member_id]
    서버 ->> 서버: kakao_member_id 유저 정보 확인
    alt 유저 정보가 없으면 
        서버 ->> 서버: 회원가입
    end
    서버 ->> 시작화면: 유저 정보, jwt 토큰
```

