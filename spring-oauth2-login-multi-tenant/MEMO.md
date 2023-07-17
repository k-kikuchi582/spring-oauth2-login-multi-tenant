`AuthorizationFilter`
```java
AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, request);
this.eventPublisher.publishAuthorizationEvent(this::getAuthentication, request, decision);
if (decision != null && !decision.isGranted()) {
    throw new AccessDeniedException("Access Denied");
}
```

`OAuth2LoginAuthenticationFilter`
IdPでの認証後のリダイレクトの受け先
リダイレクトで認可コードを受け取っているので、IdPのトークンエンドポイントにリクエストしてアクセストークンとIDトークンをもらう


`OAuth2LoginConfigure.getLoginLinks`
