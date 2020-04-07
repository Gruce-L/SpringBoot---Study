## 一、Web开发

#### 1.使用SpringBoot：

**1）、创建SpringBoot应用，选中我们需要的模块**

**2）、SpringBoot已经默认将这些场景配置好，只需要在配置文件中指定少量配置就可运行起来**

**3）、自己编写业务逻辑代码**



#### **自动配置原理？**

这个场景SpringBoot帮我们配置了什么？能不能修改？能修改哪些配置？能扩展哪些功能？xxxx

​	**1.SpringBoot启动会加载大量的自动配置类**

​	**2.我们看我们需要的功能有没有SpringBoot默认写好的自动配置类**

​	**3.我们再来看这个自动配置类中到底配置了哪些组件，只要我们要用的组件有，我们就不需要再来配置；如果没有，需要自己写配置类**

​	**4.给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，而这些属性就可以在配置文件中指定这些属性的值**

```
xxxxAutoConfiguration：帮我们给容器中自动配置组件
xxxxProperties：配置类来封装配置文件的内容
```

#### 2.SpringBoot对静态资源的映射规则

```java
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties {
//可以设置和静态资源有关的参数，比如缓存时间等
```



```java
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      if (!this.resourceProperties.isAddMappings()) {
           logger.debug("Default resource handling disabled");
      } 
      else {
           Duration cachePeriod =this.resourceProperties.getCache().getPeriod();
           CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
           if (!registry.hasMappingForPattern("/webjars/**")) {
          	this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
                }
           String staticPathPattern = this.mvcProperties.getStaticPathPattern();
           if (!registry.hasMappingForPattern(staticPathPattern)) {
       	    this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(WebMvcAutoConfiguration.getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
                }

            }
        }

//是来配置欢迎页映射
 @Bean
        public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
            WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(new TemplateAvailabilityProviders(applicationContext), applicationContext, this.getWelcomePage(), this.mvcProperties.getStaticPathPattern());
            welcomePageHandlerMapping.setInterceptors(this.getInterceptors(mvcConversionService, mvcResourceUrlProvider));
            return welcomePageHandlerMapping;
        }
```



==1）、所有/webjars/**，都去classpath:/META-INF/resources/webjars/找资源；==

​		webjars：以jar包的方式引入静态资源；https://www.webjars.org/

​		localhost:8080/webjars/jquery/3.3.1/jquery.js

```xml
<!--引入jquery-webjar-->在访问的时候只需要写webjars下面资源的名称即可
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.3.1</version>
        </dependency>
```

==2）、“/**”:访问当前项目的所有资源（静态资源的文件夹）==

```java
"classpath:/META-INF/resources/", 
"classpath:/resources/", 
"classpath:/static/", 
"classpath:/public/",
"/"			//当前项目的根路径
```

localhost:8080/abc===  去静态资源文件夹里面找abc	

==3）、欢迎页：静态资源文件夹下的所有index.html页面，被"/**"映射。==

​		localhost:8080/	找index页面

==4）、所有的**/favicon.ico都是在静态资源文件下找；==

#### 3.模板引擎

JSP、Velocity、Freemarker、Thymeleaf；

SpringBoot推荐使用的Thymeleaf：语法更简单，功能更强大；

##### 1.引入Thymeleaf

```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

切换thymeleaf版本
<thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
        <!-- 布局功能的支持程序  thymeleaf3主程序  layout2以上版本 -->
        <!-- thymeleaf2 和 latout1 适配 -->
<thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
```

##### 2.Thymeleaf使用&语法

```java
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";
    private boolean checkTemplate = true;
    private boolean checkTemplateLocation = true;
    private String prefix = "classpath:/templates/";
    private String suffix = ".html";
    
    //只要我们把html页面放在classpath:/templates/，thymeleaf就能自动渲染；
```

使用

1、导入thymeleaf的名称空间：

```xml
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

2、使用thymeleaf语法

```xml
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功！</h1>
    <!-- th:text 将div里面的文本内容设置为 -->
    <div th:text="${hello}">这是显示欢迎信息</div>
</body>
</html>
```

##### 3、语法规则

1）、th:text：改变我们当前元素里面的文本内容；

​		th：任意html属性；来替换原生属性的值

![img](https://cdn.static.note.zzrfdsn.cn/images/springboot/assets/2018-02-04_123955.png)

##### 4、SpringMVC自动配置

###### Spring Boot 自动配置好了SpringMVC

以下是SpringBoot对SpringMVC的默认：

- Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.
  - 自动配置了ViewResolver（视图解析器：根据方法的返回值得到视图对象（View），视图对象决定如何渲染（转发？重定向?））
  - ContentNegotiatingViewResolver：组合所有的视图解析器；
  - ==如何定制：可以自己给容器中添加一个视图解析器（ViewResolver）；自动的将其组合进来；==

- Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-mvc-static-content))).
  - 静态资源文件夹路径，webjars
- Automatic registration of `Converter`, `GenericConverter`, and `Formatter` beans.
  - Converter：转换器 - public String hello(User user)：类型转换使用Converter
  - Formatter：格式化器 - 2020/04/06 == 转换成Date日期类型

```java
//需要在文件中配置日期格式化的规则
@Bean
        public FormattingConversionService mvcConversionService() {
            WebConversionService conversionService = new WebConversionService(this.mvcProperties.getDateFormat());
            //日期格式化组件
            this.addFormatters(conversionService);
            return conversionService;
        }
```

==自己添加的格式化器、转换器，只需要放在容器中即可==

- Support for `HttpMessageConverters` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-mvc-message-converters)).

  - HttpMessageConverters：SpringMVC用来转换Http请求和响应的；User--json；
  - HttpMessageConverters是从容器中确定；获取所有的HttpMessageConverters；

  ​       ==是自己给容器中添加HttpMessageConverters，只需要将自己的组件注册在容器中==（@Bean）

```java
 public HttpMessageConverters(HttpMessageConverter<?>... additionalConverters) {
        this((Collection)Arrays.asList(additionalConverters));
    }
```

- Automatic registration of `MessageCodesResolver` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-message-codes)).
  - 定义错误代码生成规则

```java
public static enum Format implements MessageCodeFormatter {
        PREFIX_ERROR_CODE {
            public String format(String errorCode, @Nullable String objectName, @Nullable String field) {
                return toDelimitedString(new String[]{errorCode, objectName, field});
            }
        },
        POSTFIX_ERROR_CODE {
            public String format(String errorCode, @Nullable String objectName, @Nullable String field) {
                return toDelimitedString(new String[]{objectName, field, errorCode});
            }
        };
```

- Static `index.html` support.
  - 静态首页访问
- Custom `Favicon` support (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-mvc-favicon)).
- Automatic use of a `ConfigurableWebBindingInitializer` bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-mvc-web-binding-initializer)).
  - 我们可以配置一个ConfigurableWebBindingInitializer来替换默认的；（添加到容器中）
  - 作用是初始化WebDataBinder；（Web数据绑定器）

```java
protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer(FormattingConversionService mvcConversionService, Validator mvcValidator) {
            try {
                return (ConfigurableWebBindingInitializer)this.beanFactory.getBean(ConfigurableWebBindingInitializer.class);
            } catch (NoSuchBeanDefinitionException var4) {
                return super.getConfigurableWebBindingInitializer(mvcConversionService, mvcValidator);
            }
        }
```

**org.springframework.boot.autoconfigure.web：web的所有自动配置场景**

If you want to keep Spring Boot MVC features and you want to add additional [MVC configuration](https://docs.spring.io/spring/docs/5.2.1.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`. If you wish to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, you can declare a `WebMvcRegistrationsAdapter` instance to provide such components.

If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`.

###### **扩展MVC：**

```xml
<mvc:view-controller path="/hello" view-name="success"/>
<mvc:interceptors>
    <mvc:interceptor>
        <mvc:mapping path="hello"/>
        <bean></bean>
    </mvc:interceptor>
</mvc:interceptors>
```

==编写一个配置类（@Configuration），是WebMVCconfigureAdapter类型；不能标注@EnableWebMvc==

既保留了所有的自动配置，也能用我们扩展的配置

```java
//使用WebMvcConfigurationSupport可以来扩展SpringMVC的功能
@Configuration
public class MyMvcConfig extends WebMvcConfigurationSupport{

    //要什么方法，就重写什么功能

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        // super.addViewControllers(registry);
        //链式访问，浏览器发送atguigu请求，也来到success页面
        registry.addViewController("/atguigu").setViewName("success");
    }
}
```

原理：

1）、WebMvcConfiguration是SpringMVC的自动配置类

2）、在做其他自动配置时候，会导入@Import(EnableWebMvcConfiguration.class)

```java
//WebMvcAutoConfiguration 
@Configuration(
        proxyBeanMethods = false
    )
    public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
        
//WebMvcAutoConfiguration的父类DelegatingWebMvcAutoConfiguration
private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

    public DelegatingWebMvcConfiguration() {
    }

    @Autowired(
        required = false
    )
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addWebMvcConfigurers(configurers);
        }

    }
```

3）、容器中所有的WebMvcConfiguration都会一起起作用；

4）、我们的配置也会被调用

效果：SpringMVC的自动配置和我们的扩展配置都会起作用

###### 全面接管SpringMVC：

​	SpringBoot对SpringMVC的自动配置不需要了，所有都是我们自己配置，即**所有的SpringMVC自动配置都失效。只需要在配置类中加@EnableWebMVC即可**



###### 为什么@EnableWebMvc自动配置都失效了：

1）、@EnableWebMvc

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc{}
```

2）、

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport){}
```

3）、

```java
@Configuration(
    proxyBeanMethods = false
)
@ConditionalOnWebApplication(
    type = Type.SERVLET
)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})

//判断容器中没有这个组件的时候，自动配置类才生效
@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
@AutoConfigureOrder(-2147483638)
@AutoConfigureAfter({DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class, ValidationAutoConfiguration.class})
public class WebMvcAutoConfiguration {

```

4）、@EnableWebMvc将WebMvcConfigurationSupport组件导入进来，导入的WebMvcConfigurationSupport知识SpringMVC最基本的功能

##### 5.如何修改SpringBoot的默认配置

模式：

​	1）、SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean），如果有就用用户自己配置的，否则自动配置；如果有些组件可以有多个（比如ViewResolver），将用户配置的和自动配置的组合起来

​	2）、在SpringBoot中会有非常多的xxxxConfigure帮助我们进行扩展配置

##### 6.RestfulCRUD

###### 1）、默认访问首页

###### 2）、国际化

​			（1）编写国际化配置文件；

​			（2）使用ResourceBundleMessageSource管理国际化资源文件；

​			（3）在页面使用fmt:message取出国际化内容

​		==在SpringBoot出来后，只需编写（1）即可==

步骤：	

​		（1）编写国际化配置文件，抽取页面需要显示的国际化消息

![image-20200406150124997](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200406150124997.png)

​		

​		（2）SpringBoot自动配置好了管理国际化资源文件的组件

```java
@ConfigurationProperties(prefix = "spring.messages")
public class MessageSourceAutoConfiguration {
		
    private String basename = "messages";
    //我们的配置文件可以直接放在类路径下叫messages.properties;
    
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        if (StringUtils.hasText(this.basename)) {
//设置国际化资源文件的基础名（去掉语言国家代码的）
          messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(this.basename)));
        }

        if (this.encoding != null) {
            messageSource.setDefaultEncoding(this.encoding.name());
        }

        messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
        messageSource.setCacheSeconds(this.cacheSeconds);
        messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
        return messageSource;
    }
}
```

​			（3）去页面获取国际化的值：

```xml
 <body class="text-center">
        <form class="form-signin" action="dashboard.html">
            <img class="mb-4" src="asserts/img/bootstrap-solid.svg" alt="" width="72" height="72">
            <h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
            <label class="sr-only">Username</label>
            <input type="text" class="form-control" th:placeholder="#{login.username}" placeholder="Username" required="" autofocus="">
            <label class="sr-only">Password</label>
            <input type="password" class="form-control" th:placeholder="#{login.password}" placeholder="Password" required="">
            <div class="checkbox mb-3">
                <label>
          <input type="checkbox" value="remember-me"> [[#{login.remember}]]
        </label>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit" th:text="#{login.btn}">Sign in</button>
            <p class="mt-5 mb-3 text-muted">© 2017-2018</p>
            <a class="btn btn-sm">中文</a>
            <a class="btn btn-sm">English</a>
        </form>

    </body>
```

​			(4)然后就可以更改浏览器语言，页面就会使用对应的国际化配置文件

![1573900071209](https://cdn.static.note.zzrfdsn.cn/images/springboot/assets/1573900071209.png)

​			(5)原理

​				国际化Locale（区域信息对象）；

​				LocaleResolver（获取区域信息对象的组件）；

​				在springmvc配置类`WebMvcAutoConfiguration`中注册了该组件

```java
        @Bean
        /**
          *前提是容器中不存在这个组件，
　　　　　　*所以使用自己的对象就要配置@Bean让这个条件不成立（实现LocaleResolver 即可）
　　　　　　*/
        @ConditionalOnMissingBean

        /**
          * 如果在application.properties中有配置国际化就用配置文件的
          * 没有配置就用AcceptHeaderLocaleResolver 默认request中获取
          */
        @ConditionalOnProperty(
            prefix = "spring.mvc",
            name = {"locale"}
        )
        public LocaleResolver localeResolver() {
            if (this.mvcProperties.getLocaleResolver() == org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.LocaleResolver.FIXED) {
                return new FixedLocaleResolver(this.mvcProperties.getLocale());
            } else {
                AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
                localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
                return localeResolver;
            }
        }Copy to clipboardErrorCopied
```

默认的就是根据请求头带来的区域信息获取Locale进行国际化

```java
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = this.getDefaultLocale();
        if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
            return defaultLocale;
        } else {
            Locale requestLocale = request.getLocale();
            List<Locale> supportedLocales = this.getSupportedLocales();
            if (!supportedLocales.isEmpty() && !supportedLocales.contains(requestLocale)) {
                Locale supportedLocale = this.findSupportedLocale(request, supportedLocales);
                if (supportedLocale != null) {
                    return supportedLocale;
                } else {
                    return defaultLocale != null ? defaultLocale : requestLocale;
                }
            } else {
                return requestLocale;
            }
        }
    }
```

###### 3）：登录

开发期间模板引擎页面修改以后，要实时生效

1）、禁用模板引擎的缓存：在application.properties中设置spring.thymeleaf.cache=false 

2）、页面修改完成以后CTRL+F9，重新编译；

登录错误消息的提示

```login.html
<!--判断：只有在msg有的情况下生成这个p标签-->
<p style="color: red" th:text="${msg}" th:if="${not#strings.isEmpty(msg)}"></p>
```

###### 4）、拦截器进行登陆检查

###### 5）、CRUD-员工列表

实验要求：

1）、RestfulCRUD：CRUD满足Rest风格；

路径的格式：URI（统一资源标识符）：/资源名称/资源标识	HTTP请求方式区分对资源的CRUD操作

|      | 普通CRUD（uri来区分操作）               | RestfulCRUD               |
| ---- | --------------------------------------- | ------------------------- |
| 查询 | getEmp                                  | emp--GET方式              |
| 添加 | addEmp，带上员工的信息                  | emp--POST方式             |
| 修改 | updateEmpid=xxx&xxx==xx，带上员工的信息 | emp/{Id}--PUT方式发送请求 |
| 删除 | deleteEmp?id=1，删除员工信息            | emp/{id}---DELETE         |

2）、实验的请求架构：

|                                      | 请求的URI | 请求方式 |
| ------------------------------------ | --------- | -------- |
| 查询所有员工（来到修改页面）         | emps      | GET      |
| 查询某个员工                         | emp/{id}  | GET      |
| 添加页面                             | emp       | GET      |
| 添加员工                             | emp       | POST     |
| 来到修改页面（查出员工进行信息回显） | emp/1     | GET      |
| 修改员工                             | emp       | PUT      |
| 删除员工                             | emp/1     | DELETE   |

3）、员工列表

**thymeleaf公共页面元素抽取**

```html
1.抽取公共片段
<div th:fragment='copy'>
&copy;  2011 The Good Thymes Virtual Grocery
</div>

2.引入公共片段
<div th:insert="~{footer :: copy}"></div>
~{templates::selector}：模板名：：选择器
~{templates::fragmentname}：模板名：：片段名

3.默认效果
insert的功能片段在div标签中
如果使用th:insert等属性进行引入，可以不用写~{}；
行内写法可以加上：[[~{}]];	[[~()]];
```

三种引入功能片段的th属性：

th.insert：将公共片段整个插入到声明引入的元素中

th.replace：将声明引入的元素替换为公共片段

th.include：将被引入的片段的内容包含进这个标签中

```html
/*公共片段*/
<footer th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

/*引入方式*/
<div th:insert="footer :: copy"></div>
<div th:replace="footer :: copy"></div>
<div th:include="footer :: copy"></div>


/*效果*/
<div>
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
</div>

<footer>
&copy; 2011 The Good Thymes Virtual Grocery
</footer>

<div>
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

