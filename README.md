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

If you configured `authenticationEntryPoint` in exceptionHandling, spring security will not generate default login page for you (event if you have http.formLogin() configured!)

相关的源代码:
![DefaultLoginPageConfigurer](doc/assets/images/DefaultLoginPageConfigurer.png)


### Solution #1 - provide custom login page
We have to provide our own custom login page. Check [here](https://www.youtube.com/watch?v=yoTohM2jYhs) for how to do that.

1. Change SecurityConfig: `http.formLogin(login -> login.loginPage("/login").permitAll());`
2. Provide a LoginController.
3. Provide templates/login.html
4. Add thymeleaf to pom.xml(This is required becoz any string returned from your controller will be considered some template and you need to provide some template engine to support it, otherwise, you will get http `500` error.
    > Circular view path [login]: would dispatch back to the current handler URL [/login] again. Check your ViewResolver setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)6)
   at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)

### Solution #2 - do nothing
As we use single page application, the login page(from backend) is not required, we can leave it alone, just use `POST /login` endpoint and don't use `GET /login` at all.