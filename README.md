## Environment

- Java 17

## Check point

1. Check we can invoke spring security formLogin endpoint via axios
2. Check axios will write JSESSIONID back to browser cookie automatically
3. Check axios will write csrf-token to browser automatically
4. Custom login page( SURPRISE! )

## 碰到的坑

为什么直接访问 http://localhost:8080/login 返回404?

![404](doc/assets/images/LoginPage-404.png)

The seemingly irrelevant part `http.exceptionHandling` and ` http.formLogin` are correlated.

If you configured `authenticationEntryPoint` in exceptionHandling, spring security will not generate default login page
for you (event if you have http.formLogin() configured!)

相关的源代码:
![DefaultLoginPageConfigurer](doc/assets/images/DefaultLoginPageConfigurer.png)

### Solution #1 - provide custom login page

We have to provide our own custom login page. Check [here](https://www.youtube.com/watch?v=yoTohM2jYhs) for how to do
that.

1. Change SecurityConfig: `http.formLogin(login -> login.loginPage("/login").permitAll());`
2. Provide a LoginController.
3. Provide templates/login.html
4. Add thymeleaf to pom.xml(This is required becoz any string returned from your controller will be regarded as some
   template, so you need to provide some template engine to support it, otherwise, you will get http `500` error.
   > Circular view path [login]: would dispatch back to the current handler URL [/login] again. Check your ViewResolver
   setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)6)
   at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)

### Solution #2 - do nothing

As we use single page application, the login page(from backend) is not required, we can leave it alone, just
use `POST /login` endpoint and don't use `GET /login` at all.

## CORS

- 注意127.0.0.1 和 localhost 不是一个domain.
- CORS的文档： https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
- declare a bean named `corsConfigurationSource` and configure it in `http.cors()`

## Axios 的 content-type 随参数动态变化

When sending POST requests (also PUT and PATCH requests) with Axios, note how we pass a normal Javascript object as
data. Axios converts this Javascript data to JSON by default. It also sets the “content-type” header to
`application/json`.

However, if you pass a serialized JSON object as data, Axios treats the content type as
`application/x-www-form-urlencoded` (form-encoded request body). If the intended content type is JSON, you have to
manually set the header using the `headers` config option.

比如，`axios.post(url, data)`

当data是使用`URLSearchParams`或`qs.stringify(json)`系列化数据时，
content-type自动变为`'application/x-www-form-urlencoded`， 如果向下面这样重复指定content-type，观察chrome的network请求，
发现content-type变成了一个奇怪的值 -- 重复了两遍， (我觉得这是axios的bug!) 结果导致username无法从后台获取到，
解决办法时删除划线的那一行代码。

![axios content type](doc/assets/images/axios-conent-type.png)

当data是json object类型时，axios的content-type值为 `application/json`

当data时FormData类型时， 可以使用`axios.postForm(..)`方法

结论: 使用axios时， 绝大多数情况下都不要自己折腾 content-type.

