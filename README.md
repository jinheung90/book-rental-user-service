
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

### 회원가입
https://www.notion.so/b1dbd606fdd047a8b21430b750fd7d56?pvs=4
### 책 좋아요 기능

```mermaid
sequenceDiagram
    actor  유저
    participant 서버
    participant redis
    participant mysql
    participant 스케줄러
    
    유저 ->> 서버: 좋아요 누름
    서버 ->> redis: 저장
    유저 ->> 서버: 위시 리스트 요청
    서버 ->> redis: 위시 리스트 요청
    redis ->> 서버: 유저 책 정보의 키응답
    서버 ->> mysql: 책 정보 데이터 요청
    mysql ->> 서버: 응답
    서버 ->> 유저: 위시리스트 응답
    
    loop 매일 02시
        스케줄러->>redis: 변경된 좋아요 상태 리스트 불러오기
        redis->> 스케줄러: 응답
        스케줄러->>mysql: 상태 업데이트
    end
```


