> # 프로젝트명 : Carbonlease ( 탄소 절약 인증 플랫폼 )

### 한 줄 소개
* 탄소 절약 활동을 기록·공유하고, 캠페인 및 공지사항을 통해 친환경 활동을 장려하는 웹 서비스입니다.

> ## 메인 화면

![제목 없는 디자인](https://github.com/user-attachments/assets/e1ba503e-a2a1-42c5-95ec-4a5454317ff2)


> ## 팀원 소개
* 팀장 : 박수현
* 팀원 : 박세혁, 백준걸, 최준영, 현금자
  
> ## 프로젝트 소개
&nbsp; 이 프로젝트는 Spring Boot 4 + MyBatis + Oracle + JWT 인증 구조의 백엔드 서버로,<br />
다음과 같은 기능을 제공합니다.
* CRUD 기반의 게시판 서버
* 인증/인가(JWT 기반 Login / Token Refresh)
* 캠페인 및 공지사항 관리
* 실시간 대기오염 공공데이터 API 연동
* 파일 업로드 · 이미지 썸네일 처리
* 관리자 페이지 전용 API
* RESTful API 구조 준수

> ## 개발 기간
* 2025.11.10 ~ 2025.12.10 (약 4주)
* 개발 인원 총 5명

> ## 주요 기능

* ### 회원 관리 ### 
  * 회원가입 / 로그인 (JWT 발급)
  * AccessToken & RefreshToken 구조
  * 사용자 권한(ROLE_USER / ROLE_ADMIN)
  * 프로필 정보 수정 및 탈퇴
  * 카카오 주소 검색 API 연동

*  ### 메인 ### [openapi/main]
   * KEPCO(한국전력) API를 활용한 전국 월별 전력 사용량 시각화
   * 실시간 대기오염 정보(공공데이터 API) 제공

*  ### 캠페인 ### [campaign]
   * 이벤트 참여(웹소켓 활용)
   * 전체 조회
   * 상세 조회
   * 페이징 기능
   * 좋아요 기능 (전체조회/상세조회 연동, 동기화)
   * 댓글 CRUD 기능 (상세조회)

* ### 캠페인 관리자 페이지 ### [admin/campaign]
  * 게시글 등록 / 수정
  * 게시글 숨김 / 복구 / 삭제
  * 사용자 상태 관리
  * 검색 기능

* ### 대시보드 ### [admin/main]
  * [도넛 차트] 게시글 별 통계 데이터
  * [지도 버블 차트] 지역별 커뮤니티 총 활동량
  * [라인 차트] 지역별 커뮤니티 활동량
  * [라인 차트] 인기글Top5

* ### 일반 게시판 ### [board]
  * CRUD
  * 페이징
  * 조회수 카운트
  * 댓글 CRUD

* ### 인증 게시판 ### [activity]
  * 게시글 CRUD
  * 이미지 파일 업로드 (Multipart + UUID 저장)
  * 썸네일 이미지 자동 지정
  * 지도 좌표(lat/lng) 저장
  * 조회수 증가
  * 좋아요 토글 로직
  * 댓글 CRUD
  * 페이징 처리

* ### 공지사항 게시판 ### [notice]
  * 관리자 전용 등록/수정/삭제
  * 목록 조회 및 페이징
  * 공지 상세 보기
  * 일정 캘린더 API 연동

* ### 실시간 대기오염 정보 제공 ### [openapi/sidebar]
  * 공공데이터 API 연동
  * 시군구/시도별 미세먼지 정보 조회
  * 캐싱 처리로 요청 최소화
  * Rate-limit 방어 로직 포함

* ### 관리자 페이지 ### [admin]
  * 게시글 숨김 / 복구 / 삭제
  * 사용자 상태 관리
 
> ## 기술 스택
### Backend
* Java 21
* Spring Boot 3.5.x
* Spring Security + JWT
* MyBatis
* Oracle Database
* Lombok

### Infra
* Maven build

> ## 프로젝트 구조

```
src
└── main
    ├── java
    │   └── com.kh
    │       ├── activity
    │       ├── admin
    │       │   ├── activity
    │       │   ├── board
    │       │   ├── campaign
    │       │   ├── main
    │       │   ├── member
    │       │   └── notice
    │       ├── auth
    │       ├── board
    │       ├── campaign
    │       ├── configuration
    │       │   ├── filter
    │       │   │   └── JwtFilter
    │       │   ├── S3Config
    │       │   └── SecurityConfigure
    │       ├── common
    │       │   ├── ResponseData
    │       │   ├── exception
    │       │   ├── PageInfo
    │       │   └── FileUtil
    │       ├── event
    │       ├── member
    │       ├── notice
    │       ├── openapi
    │       │   ├── main
    │       │   │   ├── client
    │       │   │   ├── config
    │       │   │   └── scheduler
    │       │   └── sidebar
    │       │   │   ├── client
    │       │   │   └── config
    │       └── token
    │
    ├── resources
    │   ├── mapper
    │   └── application.yml
    │
```


> ## 주요 트러블 슈팅

> ### 박수현
1. 전력 사용량 API가 실제 데이터 대신 더미 HTML만 반환하는 문제
    * 문제점  
     1. 데이터 불일치 및 누락: 초기 도입한 공공 API가 응답 시 HTML 더미 데이터를 반환하거나 특정 연/월 데이터가 누락되어 시각화 기능이 작동하지 않는 현상 반복.
     2. 심각한 응답 지연: 안정적인 API(KEPCO)로 교체 후에도 데이터 호출 및 가공 과정에서 5.51초라는 긴 로딩 시간이 발생하여 서비스 가용성 저하 우려.
     3. 사용자 경험 저하: 최초 접속 시 사용자가 5초 이상 대기해야 하며, 동시 접속자 증가 시 WAS 스레드 고갈로 인한 서버 다운 리스크 존재.
   * 원인 분석 
     1. 외부 의존성 리스크: 외부 기관의 시스템 상태(데이터 누락, 엔드포인트 중단)에 내 서비스의 생존 여부가 직접적으로 연결된 구조적 결함 확인.
     2. 동기식 처리의 한계: 사용자 요청이 들어온 시점에 외부 API 호출-데이터 수집-가공 로직이 순차적으로 실행되면서 응답 병목 발생.
     3. 캐시 부재: 동일한 통계 데이터임에도 불구하고 매 요청마다 무거운 가공 로직을 반복 수행함.
   * 해결 방법 
     -> API 무결성 검증 및 교체: 로그 분석을 통해 기존 API의 오류를 확인하여 기관에 신고하고, 안정성이 검증된 KEPCO API로 전환.
     -> 캐싱 전략(@Cacheable) 도입: 반복되는 데이터 요청에 대해 메모리 캐시를 적용하여 재요청 응답 시간을 6ms로 단축.
     -> 배치 구조로의 전환 (스케줄러 & DB 저장): * 사용자 요청 경로에서 무거운 외부 API 호출을 완전히 분리.
     -> Spring Scheduler를 활용해 이용량이 적은 새벽 시간대에 데이터를 미리 수집·가공하여 DB에 저장하는 'Pre-fetching' 구조 구현.
     -> 운영 안정성 확보: 관리자 전용 데이터 갱신 API(/refreshData)를 구축하여 데이터 최신성을 수동으로 제어할 수 있는 운영 환경 마련.
     
2. S3 전환 및 이미지 수정 로직 최적화 (자원 누수 및 중복 데이터 해결)
    * 문제점
      1. 게시글 수정 시 기존 파일이 삭제되지 않고 방치되어 서버 및 S3 스토리지에 '유령 파일'이 누적되는 자원 낭비 발생.
      2. S3 전환 과정에서 발생한 데이터 불일치로 TooManyResultsException 에러가 발생하며 시스템 기능 중단.
   * 원인 분석
     1. 로직 부재: 기존 파일의 존재 여부 확인 없이 수행되는 무조건적인 INSERT 처리.
     2. 외부 자원 불일치: DB 롤백과 별개로 처리되는 외부 저장소(S3) 자원의 상태 관리 미흡.
   * 해결 방법
     -> 자원 관리 원칙 수립: 신규 파일 저장 전 기존 레코드와 물리 파일(S3)을 먼저 삭제하는 Delete-then-Insert 프로세스 정립.
     -> 방어적 쿼리 설계: 데이터 중복 발생 시에도 시스템이 중단되지 않도록 Inline View와 ROWNUM=1을 활용해 최신 파일 하나만 참조하도록 튜닝.
     -> S3 유틸리티 고도화: 로컬 삭제 방식에서 S3 API를 통한 원격 삭제 요청 방식으로 마이그레이션.
   * 최종 성과
     -> 수정 시 물리 파일 즉시 삭제로 스토리지 낭비 0% 달성 및 어떤 예외 상황에서도 기능 연속성 보장.     

3. Vite 환경 WebSocket 연결 이슈 및 런타임 환경변수 최적화
    * 문제점
      1. 연결 불가: 실시간 참여자 수 동기화를 위한 WebSocket 연결 시 Opening Web Socket... 상태에서 중단되며 연결 실패.
      2. 참조 에러: 콘솔에 global is not defined 에러 발생 및 브로드캐스트 메시지 수신 불가.
      3. 배포 유연성 부족: Vite의 기본 환경변수(.env) 방식 사용 시, 빌드 타임에 값이 고정되어 배포 환경별로 API 주소를 동적으로 변경할 수 없는 문제 발생.
   * 원인 분석
     1. 런타임 환경 차이: Node.js 전용 객체인 global을 참조하는 라이브러리(SockJS 등)가 브라우저 표준을 엄격히 따르는 Vite 빌드 환경과 충돌.
     2. 빌드 타임 주입의 한계: Vite는 빌드 시점에 환경변수를 코드에 정적으로 치환(Static Replacement)하기 때문에, 서버 환경이 바뀔 때마다 매번 재빌드가 필요한 구조적 불편함 존재.
   * 해결 방법
     -> 글로벌 객체 매핑: vite.config.js의 define 옵션을 활용하여 브라우저 환경의 window 객체를 global 식별자로 치환하여 라이브러리 호환성 해결.
     -> 런타임 환경변수 주입 전략: window.location 객체를 활용해 현재 브라우저의 접속 프로토콜과 도메인을 실시간으로 읽어오는 config.js 구현.
     -> index.html에서 해당 스크립트를 가장 먼저 로드하여, 빌드 없이도 접속 환경에 따라 API_URL과 WS_URL이 동적으로 결정되도록 아키텍처 개선.
   * 최종 성과
     -> WebSocket 연결 성공으로 실시간 데이터 동기화 기능(참여자 수) 정상 구현.
     -> 환경별 별도 빌드 프로세스 없이 하나의 빌드 결과물로 모든 환경에 대응 가능한 유연한 배포 구조 확립.

> ### 박세혁

1. 카카오 로그인 요청 시 API 응답 값을 프론트엔드로 보내지 못하는 문제
   * 문제점  
     카카오 로그인에서 `redirect_uri`를 백엔드 주소로 설정했을 때,  
     카카오가 유저를 서버로 리다이렉트하면서  
     백엔드는 단순 화면 이동만 처리할 수 있고,  
     ResponseEntity의 body로 토큰/회원 정보를 프론트에 전달하기 어려운 구조가 됨.  
     토큰과 사용자 정보를 쿼리스트링으로 보내는 방법도 있었지만,  
     이 경우 URL에 민감 정보가 그대로 노출되는 보안 문제가 있음.
   * 해결 시도  
     -> `HttpServletResponse.sendRedirect()`로 프론트 화면 이동은 시켰지만,  
        토큰·사용자 정보를 안전하게 같이 전달할 수 있는 방법은 마땅치 않았음.  
   * 최종 해결 방법  
     -> `redirect_uri`를 프론트엔드 주소로 변경  
     -> 카카오에서 프론트로 전달한 `code`를,  
        프론트가 다시 axios 요청으로 백엔드에 전달하는 구조로 재설계  
     -> 백엔드는 이 `code`로 AccessToken 및 사용자 정보를 발급하고  
        ResponseEntity의 body(JSON)로 안전하게 응답  
     → 화면 제어는 프론트가 담당,  
        민감 정보는 URL에 노출되지 않고 HTTP Body로만 전달되는 안전한 구조 완성

> ### 백준걸
1. /api/ 접근 시 403 Forbidden 발생 문제
    * 문제점
      인증게시판 상세조회 등 일부 API가 로그인하지 않아도 조회 가능해야 하는데, <br />
      Spring Security 필터에서 /api/** 경로를 인증 필요로 처리하여 <br />
      GET 요청조차 403이 발생하는 문제가 있었음. <br />
    * 원인 분석
       * Securityconfig의 경로 매칭 우선순위가 잘못되어 <br />
         GET 요청도 인증 필터를 통과해야만 하는 구조 <br />
       * permitAll 설정이 POST/PUT 등과 섞여 적용되어 정상 작동하지 않음 <br />
    * 해결 방법
       -> GET /api/** 전체를 permitAll로 명확하게 설정 <br />
       -> 경로 매칭 순서를 재정렬하여 인증 필터 충돌 제거 <br />
       -> 인증이 필요한 요청과 공개 요청을 명확히 분리해 문제 해결 <br />
2. 공공데이터 API 호출 시 429 Too Many Requests 발생
    * 문제점
      RestTemplate로 외부 API를 호출하는 과정에서 <br />
      페이지 이동·시도/지역 변경 때마다 API가 반복 호출되어 <br />
      호출 제한(분당/일일)을 초과하는 문제가 지속 발생. <br />
    * 원인 분석
       * 캐싱 미구현으로 동일 요청이 여러 번 발생
       * null·오류 응답 시 재요청이 발생해 트래픽 폭증
    * 해결 방법
       -> API 호출 실패 시 fallback 데이터 사용 <br />
       -> 간단한 메모리 캐싱 구조 적용하여 동일 요청 중복 차단 <br />
       -> RestTemplate 호출 실패 시 재시도 횟수 제한 <br />
3. 이미지 업로드 경로 매칭 오류
    * 문제점
      게시글 이미지 업로드 후 이미지가 표시되지 않거나 <br />
      잘못된 경로(uploads/activity vs static/uploads/activity)로 저장되며 <br />
      404가 발생하는 문제가 있었음. <br />
    * 원인 분석
        * 파일 저장 경로와 컨트롤러에서 제공하는 접근 URL이 불일치
        * 설정(application.yml)과 FileHandler 경로가 서로 다른 구조로 작성됨
    * 해결 방법
       -> 업로드 루트 디렉토리 통일 (uploads/activity/...) <br />
       -> YML static-location과 파일 저장 경로 일치 <br />
       -> Mapper, Service, FileHandler에서 경로 관련 문자열 정리 <br />

#### 최준영
1. 공지사항/일정 관리 요청이 Spring Security 필터에서 차단되는 문제
    * 문제점
      관리자 페이지에서 공지사항·일정 관련 API 호출 시, <br />
      ROLE_ADMIN 권한이 있어도 SecurityConfig 필터에서 요청이 차단됨 <br />
      컨트롤러까지 요청이 도달하지 않아 403 혹은 404가 발생했음. <br />
    * 원인 분석
       * /admin/** 경로와 /notices/** 경로의 권한 규칙 충돌
       * SecurityConfig에서 permitAll / authenticated / hasRole 순서가 잘못됨
    * 해결 방법
       -> 관리자 페이지용 API를 /admin/notices/** , /admin/schedule/** 등으로 완전히 분리 <br />
       -> SecurityConfig에서 관리자 경로를 명확히 hasRole("ADMIN") 뒤에 배치 <br />
       -> 필터 단계에서 차단되지 않고 정상적으로 Controller까지 도달하도록 정리 <br />
2. 상세조회 + 조회수 증가 로직 순서 오류
    * 문제점
      존재하지 않는(status = N) 게시글을 조회 요청할 때 <br />
      "조회수 증가 -> 상세조회" 순서로 되어있어 <br />
      없는 게시글도 조회수만 증가하는 버그 발생. <br />
    * 원인 분석
       * 로직 순서가 비표준적
       *  ResourceNotFoundException 처리가 누락된 구간 존재
   * 해결 방법
      -> "상세조회 -> 유효성 검사 -> 조회수 증가" 순서로 변경 <br />
      -> 예외 처리 및 return 흐름을 명확히 정리하여 비정상 증가 방지 <br />
3. 공지사항 페이지네이션 코드 중복
    * 문제점
      일반 사용자 공지 목록 / 관리자 공지 목록에서 <br />
      동일한 페이징 로직이 반복됨. <br />
    * 원인 분석
       * 두 기능이 같은 구조지만 컨트롤러가 별도 구현됨
    * 해결 방법
       -> 공통 페이징 로직을 Service 계층으로 이동 <br />
       -> Mapper 결과 구조를 통일해 재사용성 향상 <br />

> ### 현금자

1. 댓글 수정/삭제 시 로그인 사용자와 작성자 비교가 올바르게 이루어지지 않는 문제
    * 문제점
      댓글 수정 또는 삭제 요청 시 서버에서 <br />
      "로그인한 사용자 = 댓글 작성자" 여부를 검증해야 하는데, <br />
      Mapper에서 작성자 번호(writerNo)를 조회하지 않아 <br />
      항상 비교가 실패하는 상황이 발생함. <br />
    * 원인 분석
       * reply 조회 쿼리에 작성자 번호 컬럼이 포함되지 않았음
       * 서비스 계층에서 writerNo 검증이 불가능한 구조였음
    * 해결 방법
       -> Mapper(XML)에 writerNo 필드 추가 <br />
       -> DTO 및 Service 계층에서 로그인 사용자 번호와 정상 비교 가능하도록 수정 <br />
       -> 수정/삭제 요청 시 불일치하면 바로 권한 예외 발생하도록 처리 <br />
2. 게시글 상태(stauts) 업데이트 불일치 문제
    * 문제점
      게시글 삭제/수정 시 status 컬럼이 Y/N 규칙으로 통일되어 있지 않아 <br />
      일부 기능에서 삭제된 게시글이 그대로 조회되는 문제가 발생함. <br />
    * 원인 분석
       * status 관리가 테이블·쿼리마다 다르게 구현됨
       * updateDate 자동 갱신 로직이 일부 쿼리에서 누락됨
    * 해결 방법
        -> status 값을 Y/N 으로 일관되게 정리 <br />
        -> updateDate를 update 시 자동 갱신하도록 쿼리 통일 <br />
        -> 숨김/삭제/조회 여부가 정확히 반영되도록 로직 표준화 <br />
> ## 배운점

> ### 박수현
* 내용

> ### 박세혁
* Git 협업 방식 및 흐름
* MVC 패턴을 활용한 책임 분리 구조 이해
* 스프링 부트 프로젝트 세팅 및 전반적인 구성 경험
* Spring Security 및 JWT 토큰 기반 로그인 방식에 대한 이해
* 카카오 소셜 로그인 API를 활용한 OAuth 로그인 플로우 경험

> ### 백준걸
* Spring Security에서 경로 우선순위가 실제 권한 체크 결과에 큰 영향을 미침
* 외부 API(특히 공공데이터 API)는 429·Timeout 등 현실적인 문제가 많아 <br />
  캐싱·fallback·재시도 제한이 필수임
* 이미지 업로드처럼 단순해 보이는 기능도 <br />
  경로 통일·URL 매핑·파일 핸들링 구조를 정확히 이해해야 안정적으로 동작
* MyBatis에서 DTO를 조립하는 구조를 최적화하여 <br />
  CRUD + 조회 성능을 개선하는 방법을 배움

> ### 최준영
* Spring Security의 필터 흐름과 권한 체크 우선순위 중요성 체감
* 공지·일정 기능에서 공통 로직을 서비스 계층으로 분리할 때 <br />
  재사용성·가독성이 크게 향상됨
* 조회수 증가처럼 단순해 보이는 기능도 <br />
  “실행 순서”가 전체 로직 안정성에 큰 영향을 준다는 점을 배움
* API 설계 시 사용자용 / 관리자용 엔드포인트 구분이 명확해야 유지보수가 쉬움

> ### 현금자
* 테이블의 컬럼(특히 PK,FK, 상태값)의 의미와 구조를 정확히 이해해야 <br />
  서버 로직이 안정적으로 동작함을 깨달음 <br />
* Mapper XML에서 필요한 필드를 누락하면 백엔드 전체 흐름이 <br />
  잘못될 수 있음을 경험 <br />
* DB 구조 -> Mapper -> Service -> Controller 흐름의 일관성이 중요함 <br />
* 기능 단위로 패키지·폴더 구조를 나누는 것이 유지보수 효율을 높임 <br />

> ## 개선 사항

> ### 박수현
* 내용

> ### 박세혁
* 일반 로그인과 소셜 로그인 로직을 더 명확하게 분리할 필요가 있음
* JWT 토큰 생성/검증/재발급 구조를 리팩토링하여 유지보수성을 높일 필요가 있음

> ### 백준걸
* 공공데이터 API 캐싱을 프론트가 아닌 백엔드 단에서 더 고도화할 필요 있음 <br />
  (Redis 등으로 확장 가능)
* 에러 응답 형식(JSON 포맷)을 프로젝트 전체에서 일관되게 적용 필요
* 인증게시판/관리자 기능 일부가 아직 컨트롤러·서비스 계층에서 분리 부족 → <br />
  리팩토링 여지 존재
* 파일 업로드/썸네일 처리 로직도 모듈화하면 재사용성 증가

> ### 최준영
* 공통 기능(페이징, 예외 처리, DTO 변환 등)을 더 체계적으로 모듈화할 필요
* 일정/공지 관련 로직을 패키지 단위로 더 세분화하면 구조 명확성 증가
* raw Map 형태 응답 대신 DTO 기반 응답으로 변경하여 타입 안정성 강화
* 페이지네이션, 유효성 검사 등의 반복 코드를 줄이기 위한 구조 개선 가능

> ### 현금자
* 일부 CRUD 로직에서 중복되는 코드가 존재 -> 공통화 필요
* Service 계층에서 반복되는 검증/예외 처리 로직을 유틸 또는 AOP로 분리가능
* 게시판/댓글 모듈 구조를 보다 명확히 분리하면 유지보수가 더 수월해질 것으로 보임


> ## 팀원 정보


| 이름 | 담당 | GitHub |
|------|------------------------|------------------------------|
| 박수현 (팀장) | 메인페이지 & 캠페인 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/Elinasu001) &nbsp; https://github.com/Elinasu001 |
| 백준걸 | 인증게시판 & 사이드바 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/ajungeul93-rgb) &nbsp; https://github.com/ajungeul93-rgb|
| 최준영 | 공지사항 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/cjysy0104) &nbsp;https://github.com/cjysy0104|
| 현금자 | 일반게시판 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/yoonja486) &nbsp;https://github.com/yoonja486|
| 박세혁 | 회원관리 & 로그인 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/Rostreaca) &nbsp;https://github.com/Rostreaca|

> ## 문의
* Email

| 이름 | 이메일 |
|--------|---------------------|
| 박수현 | suelina001@gmail.com |
| 박세혁 | tpgur98@gmail.com |
| 백준걸 | ajungeul93@gmail.com |
| 최준영 | cjysy0104@gmail.com |
| 현금자 | yoonja486@gmail.com |
* GitHub Issues 활용
