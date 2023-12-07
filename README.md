# 게시판 프로젝트
> **권한에 따라 분리되어있는 게시판** <br/>
**개발기간: 2023.09 ~ 2023.12** <br/>
**1인 개발** <br/>
**데이터베이스: mariadb,redis,mongodb(댓글 한정)**  <br/>
**배포: cloudType**  <br/>
**개발 목적: REST API 개발 및 보안적인 요소에 집중**<br/>
**프론트 앤드 <-> 백엔드 통신 방식: REST API** 

## 서버 내부 구조
<img width="706" alt="스크린샷 2023-12-07 오후 3 20 18" src="https://github.com/xron2626/recent-board-project/assets/133539339/2d20a72e-06f3-4a13-aeda-825691d2b580">
<br/> 
<p>url 도메인을 통해서 https://siteproject22.online을 작성할 경우 nginx에서 location /로 next.js url과 연결하고,
nginx에서 location /api로 https://app.siteproject22.online과 연결하였습니다.</p>



## 화면 구성 📺(이미지를 누르면 크게 확장해서 보실 수 있으십니다.)


|                       메인 페이지                        |                     로그인 페이지                      |
| :------------------------------------------------------: | :---------------------------------------------------------: |
| <img width="329" src="https://bit.ly/3GvlSxR"/>  | <img width="329" src="https://bit.ly/47ZZhG5"/> |
|                      쪽지 페이지                      |                       게시글 보기 페이지                       |
| <img width="329" src="https://bit.ly/3TdXp7T"/> | <img width="329" src="https://bit.ly/3NfneAM"/> |
|                      통계 페이지                      |                       swagger                       |
| <img width="329" src="https://bit.ly/3Re9Yxi"/> | <img width="329" src="https://bit.ly/47Ky75O"/> |
---

## 백엔드 개발한 기능 📦

### ⭐️ 리액트와 스프링부트를 다른 서버로 분리하여 배포한다음 스프링부트는 서브도메인, 리액트는 도메인에 연결한다음 쿠키로 통신할 수 있게끔 설정
### ⭐️ 비회원은 쿠키에서 uuid값으로 통신, 회원은 쿠키의 token 값으로 통신하도록 설정(우선순위는 회원의 token값 먼저 설정하고, 해당 토큰 데이터가 잘못되었거나, 없으면 uuid로 통신하게끔 설정)
### ⭐️ Oauth도 시큐리티랑 연동해서 시큐리티에 있던 라이브러리의 기능을 DI를 이용해 구현해서 내부 로직을 추가하여 앞의 부분과 마찬가지로 회원계정으로 token값으로 통신할 수 있게끔 설정
### ⭐️ REST API를 할 수 있는 범위까지 만들어보고, 간단하게 테스트 코드도 일부 추가함
### ⭐️ 내가 요청하지 않은 곳에서 쿠키값을 요청할 수 있으니 CORS 설정
### ⭐️ Oauth에서 제공하는 데이터만으로 부족할 때, OAUTH 로그인 시도할 떄 가입하지 않은 게정이면, 추가 데이터를 강제로 입력하도록 설정하고, 이후 로그인 될 떄 부터는 가입된 계정안에 추가 데이터가 들어있으니 바로 로그인되도록 설정
### ⭐️ 아이디 찾기, 비밀번호 변경, 게시판 쓰기, 게시판 읽기, 회원가입, 좋아요, 싫어요, 댓글, 대댓글 작성, ckeditor(이미지 업로드, 텍스트 정렬, 폰트 사이즈 크기, 폰트 색 설정)등 다양한 기능 추가 
### ⭐️ 게시글 작성자에게 댓글이 달릴경우 해당 작성자에게 서버의 소켓을 추가하여 알림이 뜨게 하고 쪽지 페이지에 해당 작성자에게만 알림이 뜬 데이터들을 확인할 수 있게 기능 추가
### ⭐️ 내가 만든 프로젝트를 실제 도메인 사이트에 연결하면서, 로컬로도 동작할 수 있게끔 설정
### ⭐️ 테스트 환경과 개발 환경의 설정을 살짝 분리했고, 암호가 중요한 경우는 환경변수로 설정하고, 환경변수와 관련된 데이터는 깃허브에 등록하지 않음
### ⭐️ 오랫동안 저장되지 않아도 데이터는 Redis에 추가하고, Comment에 한정해서 List 타입으로 다루는게 코드가 간단해보여서 몽고db로 사용
### ⭐️ 어드민용 통계페이지 및 swagger 작성
### ⭐️ CKEDITOR를 개발 시간 상의 이유로 리액트에 js를 붙혀서 개발했는데 기존 CKEDITOR js에 XSS 삽입 공격을 방지하려고 Content Security Policy 정책 삽입
### ⭐️ uuid랑 토큰 값은 인터셉터를 이용해 서비스가 시작되기 전에 항상 먼저 없으면 uuid 데이터가 추가되거나 token 만료, 사용자의 토큰 조작 시 로그아웃 페이지로 강제이동 같은 처리 추가
### ⭐️ 스프링 시큐리티로 비밀번호 값 노출 안하려고 하였고, 엔티티 그대로 가져오는 것이 아니라 DTO 같은 값을 fetch 통신에서 리턴하여 다른 사용자의 uuid 같은 부분은 노출되지 않도록 개발

## 알람 및 쪽지 관련 기능(영상에 마우스를 클릭해 좀 더 빠르게 영상을 시청하실 수 있으십니다.)
### 알람 기능(게시판에 댓글을 달 경우 해당 게시판의 게시글 작성자가 어느 페이지에서건 "새로운 글이 작성되었습니다."라고 알림 추가)
> https://github.com/xron2626/recent-board-project/assets/133539339/262d10ed-b2c8-43c3-938a-e1922b34f038


### 쪽지 기능(해당 알람이 발생한 이후, 우측 상단의 메일함 같은 곳을 클릭하면 작성자에게 발생한 알림을 모아놓은 쪽지 리스트의 페이지가 나타납니다.)
> https://github.com/xron2626/recent-board-project/assets/133539339/a3078cd6-be7f-4269-8da7-7fd0cef63c6a

## 게시판 회원 계정
### 어드민 관리자 계정
id:12345 비밀번호:12345
### 일반 회원 계정
id:12345 비밀번호:12345

## 회원과 권한을 user안의 같은 패키지 안에 묶고, board,comment를 다른 패키지로 분리해서 개발한 이유 

### 권한은 회원 안에서만 존재하고, 게시글에 대해서는 권한이 존재하지 않고 그 게시글을 작성한 사용자에게 권한이 있는거라고 판단했다. 또한 삭제될 때도 UserAuthority는 User가 삭제될 때만 같이 사라지는 관계이다.  반면 user와 board는 SITEUSER나 OAUTHUSER같은 권한을 가진 유저를 생각하면 board 페이지에 들어가서 board를 삭제한다고 해서 user가 삭제되지 않는다. 그리고 도메인 상 사용자 위주의 기능을 나타내는 로그인, 회원가입, 아이디 찾기, 비밀번호 변경 페이지와 물론 유저를 테이블에서 join하여 사용할 수 있지만 게시판의 글 읽기, 글 수정, 글 삭제가 중심이 되는 게시판과는 비즈니스적으로도 분리된다고 판단하였습니다.
<br/>
성별이나 권한은 user 안에 의존적이지만 장기적으로 좋아요,싫어요를 담당하는 부분은 Board,Comment 양 쪽에서 작동이 가능하도록 추가할 예정이기 때문에 Board안에 Comment를 넣으려니, feedback 위치가 애매해지고, 시간상의 이유로 RDB를 사용하면 LIST로 묶으려니 로직이 복잡해서, Comment에 한정해 MongoDB로 개발했는데, 이 경우 테이블의 위치도 달라서, 분리하여 개발하였습니다.

## nginx를 프록시 서버로 작성한 이유
### 리액트에서는 스프링부트의 서버 도메인에 바로 https://app.siteproject22.online/ 으로 연결해서 Nginx를 거치지않아도 되지만,  같은 기능을 처리하는 서버를 여러개 만들고, nginx가 프록시 서버로 연결해 그 중 하나로 뿌려 서버의 트래픽 부하를 줄이는 경우, 보안적인 이유로 내부 서버(리액트 서버, 스프링부트 서버)를 드러내고 싶지 않을 수도 있고, 포트폴리오 작성할 때 실제로 그런지는 알 수 없지만 아키텍처 다이어그램에서 nginx를 거치게 개발하는 경우도 많이 봐서 근거를 가지고, 주변사람의 선택을 따라가는 것도 필요하다고 생각해서 위와 같은 이유로 작성하였다.

## 리액트를 프론트언어로 개발한 이유
하지만, 스켈레톤 애니메이션을 처리하는 방법을 익힌다면, 먼저 보여줘야 할 데이터가 무겁고, 실시간적으로 보여줘야 할 경우에 효율적으로 처리할 수 있고,
간단한 데이터라도, js로 ajax,fetch,axios 같은 기능을 쓰는데 통신이 지연되는 경우 로딩되는 경우가 필요하다고 판단했고,
그러나 js 자체만으로는 스켈레톤 애니메이션의 코드를 작성하기 어려워질 것 같다고 판단했고, angular.js나 vue.js는 생태계 발달이 react.js보다는 
상대적으로 덜하다고 들어서, 관련 자료를 찾기 힘들 것 같아 react를 선택하였다.









