# SLOWMAN PubSub 구조 유량제어

## 아이디어

- REDIS 를 이용한 유량제어를 할 수 있지 않을까?
- 요청 받은 모든 데이터를 모두 REDIS에 올려두고 스케줄러를 통해 TPS조절 가능할거라고 생각
- 그러기 위해서는 REDIS가 안전해야 하므로 클러스터를 이용하여 구성. REDIS가 매우 중요 ! 
- Webflux 와 Reactive Redis 를 이용하여 데이터 저장 속도를 높일 수 있지 않을까? 하는 생각 
- 간단하게 구현해보고 익숙해지는 것이 목적임. 

## REDIS 클러스터 구성을 위한 docker 실행

- 위치 : /docker 에서 아래 명령어 실행 

```
docker-compose -f docker-compose.redis-cluster.yml up -d
```


## 프로젝트 구조
- consumer : 레디스의 데이터를 읽어와서 또 다른 ENDPOINT로 전송
- publisher : 요청 받은 모든 데이터를 레디스로 푸시하는 역할
- core : 공통 도메인이나 유틸 


## 테스트 방법

- PublisherApplication 을 실행하면 localhost:8081 로 서버가 기동됨.
- ConsumerApplication 을 같은 방법으로 기동하면 localhost:8082로 기동됨.
- localhost:8081/message/payload-test 를 통해 10만건의 데이터를 REDIS에 저장. (약 3-4초 정도 걸림)
- PayloadSubService 에서 TPS 상수를 조절하면 TPS조절 가능함. 
- PayloadSubService 의 consumeTest 에서 subscribe 부분을 다른 서버로 전송하면 될듯.(미구현)


## 추가 고려할점

- CONSUME에 실패할 경우 어떻게 할것인가? -> 가장 안전한 방법은 CONSUME 하기 전에 데이터를 복사하여 따로 저장해두는 방법
- PUSH에 실패할 수 있는가? -> 데이터를 그대로 PASS 하여 실패가능성은 없다고 봐도 되지 않을까
- 트래픽 조절할때 마다 TPS 변수를 고쳐야하는가? -> 이건 리팩토링의 문제일듯 
- 추가 고려사항은 나중에...