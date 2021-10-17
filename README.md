# JWT
<b>Json Web Token</b><br/>
선택적 서명 및 선택적 암호화를 사용하여 데이터를 만들기 위한 인터넷 표준으로, 페이로드는 몇몇 클레임(claim) 표명(assert)을 처리하는 JSON을 보관하고 있다.<br/>

### (1) Header
```json
{
  "alg" : "HS256",
  "typ" : "JWT"
}
```
### (2) PayLoad
```json
{
  "sub": "arnold",
  "roles": [
    "ROLE_ADMIN",
    "ROLE_SUPER_ADMIN",
    "ROLE_USER"
  ],
  "iss": "http://localhost:8080/api/login",
  "exp": 1634460421
}
```
### (3) Signature
```json
{
  HMACSHA256(
    base64UrlEncode(header) + "." +
    base64UrlEncode(payload),
    your-256-bit-secret
  )
}
```
### (4) Call
```json
{
  "Authorization": "Bearer {Access 토큰 값}"
}
```
## Access Token, Refresh Token
```java
call /api/login
call /api/token/refresh

1. SecurityConfig
protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		http.csrf().disable();
		//SessionCreationPolicy.ALWAYS => 항상세션을 생선
		//SessionCreationPolicy.IF_REQUIRED => 필요시 생성(기본)
		//SessionCreationPolicy.NEVER => 세션 생선하지않지만, 기존에 존재하면 사용
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 스프링시큐리티가 생성하지도않고 기존것을 사용하지도않음(JWT토큰사용)
		http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
}

2. CustomAuthorizationFilter
request.getServletPath().equals(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh"))
...

2. CustomAuthenticationFilter
UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
...
Algorithm algorithm = Algorithm.HMAC256("secreat".getBytes());
String access_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10분
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		
String refresh_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30분
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
tokens.put("access_token", access_token);
tokens.put("refresh_token", refresh_token);
response.setContentType(MediaType.APPLICATION_JSON_VALUE);
new ObjectMapper().writeValue(response.getOutputStream(), tokens);
...

```
## Access Token 만료
```java

1. CustomAuthorizationFilter
try {
    String token = authorizationHeader.substring("Bearer ".length());
	  Algorithm algorithm = Algorithm.HMAC256("secreat".getBytes());
	  JWTVerifier verifier = JWT.require(algorithm).build();
	  DecodedJWT decodedJWT = verifier.verify(token);
	  String username = decodedJWT.getSubject();
	  String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
	  Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
	  Arrays.stream(roles).forEach(role -> {
	  authorities.add(new SimpleGrantedAuthority(role));
	  });
	  UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
	  SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	  filterChain.doFilter(request, response);
} catch (Exception e) {
    log.info("Error logging in : {}", e.getMessage());
	  response.setHeader("error", e.getMessage());
	  response.setStatus(HttpStatus.FORBIDDEN.value());
	  Map<String, String> error = new HashMap<>();
	  error.put("error_message", e.getMessage());
	  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	  new ObjectMapper().writeValue(response.getOutputStream(), error);
 }
```

## 1. 로그인 되었을때 발급되는 토큰
![image](https://user-images.githubusercontent.com/38873242/137619015-53cac83b-dd5e-43ac-87ad-c0ab1393ef8c.png)

## 2. 유효기간이 토큰으로 조회한 결과 
![image](https://user-images.githubusercontent.com/38873242/137618971-c831cc92-a655-413f-95aa-5c8c4ce2cede.png)

## 3. Refresh 토큰으로 Access토큰을 새로발급
![image](https://user-images.githubusercontent.com/38873242/137618935-1a710f4f-a4cc-483d-8092-99a5503a245b.png)

## 4. 새로 발급받은 Access토큰으로 조회 결과
![image](https://user-images.githubusercontent.com/38873242/137618980-c4124f9f-a669-44ff-aa04-37bcd4866259.png)

## 출처
Amigoscode https://youtu.be/VVn9OG9nfH0
