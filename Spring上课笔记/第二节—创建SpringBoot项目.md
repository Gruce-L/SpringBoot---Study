

此文档==一到二。3==的代码见spring-boot-02-config

此文档二。4到二。7的代码见spring-boot-02-config-02

此文档二。8到三。2的代码见spring-boot-02-config-02-autoconfig

此文档三。3到最后见spring-boot-03-logging

## 一、使用Spring Initializer 快速创建Spring Boot项目

IDE都支持使用Spring的项目创建向导快速创建一个Spring Boot的项目；

选择我们需要的模块，向导会联网创建Spring Boot项目

默认生成的Spring Boot项目：

- 主程序已经写好了，我们只需要我们自己的逻辑
- resource文件夹中的目录结构
  - static：保存所有的静态资源；js，css，images；
  - templates：保存所有的模板页面：（Spring Boot默认jar包使用嵌入式的Tomcat，默认不支持jsp页面，可以使用模板引擎（freemarker、thymeleaf））
  - application.properties：Spring Boot应用的配置文件；可以修改一些默认设置

## 二、Spring Boot配置

​		配置文件、加载程序、配置原理

### 	1.配置文件

​		Spring Boot使用全局的一个配置文件，配置文件名是固定的

两个默认的全局配置文件

- application.properties
- application.yml

   配置文件的作用：修改Spring Boot自动配置的默认值：Spring Boot在底层多给我们自动配置好；

​	yml：（YAML    A Makerup Language：是一个标记语言）

​			  （YAML    isn`t Makerup Language：不是一个标记语言）

​    标记语言：

- 以前的配置文件：大多数是xml文件
- 现在的配置文件（YAML）：以数据为中心，比json、xml等更适合做配置文件
- YAML配置例子：

```yml
server:
	port:8081
```

XML配置例子：（大量数据浪费在了标签的开辟上）

```xml
<server>
    <port>8081</port>
</server>
```

### 2.YAML语法：

#### 	1.基本语法：

​			k: v表示一对键值对（冒号和v之间的空格必须有）

​			以**空格**的缩进来控制层级关系；只要左对齐的一对数据都是同一层级的

```yml
server:
	port: 8081
	path: /hello
```

属性和值也是大小写敏感；

####     2.值的写法：(属性用空格表示缩进)

###### 		   字面量：普通的值（数字，字符串，布尔）

​				k: v：字面直接来写，字符串默认不用加上单引号或者双引号

​							如果要加：

​							“”：双引号 - 不会转义字符串里面特殊字符，特殊字符会作为本身表示的意思

​									eg.     name: "zhangsan\n lisi"	输出：zhangsan 换行   lisi

​							''：单引号 - 会转义字符串里面的特殊字符，特殊字符最终只是一个字符串输出

​									eg.     name: ‘zhangsan\n lisi’	 输出：zhangsan  \n   lisi

###### 		   对象、Map（属性和值）（键值对）：

​				k: v：在下一行来写对象的属性和值的关系，只是注意缩进

​					对象还是k: v的方式

```yml
		friends：
			lastName: zhangsan	
			age: 24
```

​				行内写法：

```yml
friends: {lastName: zhangsan, age: 18}
```

###### 		   数组（List、Set）：

​				第一种写法：用”-“+空格值表示数组中的一个元素

```yml
pets:
 - cat
 - dog
 - pig
```

​			第二种写法：

```yml
pets: [cat,dog,pig]
```

### 3.配置文件值注入

#### 1.配置文件步骤（分三步）：

第一步 - javaBean：

```yml
/*
将配置文件中配置的每一个属性的值，映射到这个组件中
@ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置
                          进行绑定
prefix = "person"：配置文件中哪个下面的所有属性进行一一映射

只有这个组件是容器中的组件，才能使用容器提供的功能
 */
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private String lastName;
    private Integer age;
    private Boolean boss;
    private Date birth;

    //表示Map类型的数据
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
```

第二步：配置文件

```yml
person:
    lastName: hello
    age: 18
    boss: false
    birth: 2017/12/12
    maps: {k1: v1,k2: 12}
    lists:
      - lisi
      - zhaoliu
    dog:
      name: 小狗
      age: 12
```

第三步：导入配置文件处理器，以后编写配置就有提示

```yml
<!--导入配置文件处理器，配置文件进行绑定就会有提示-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

除了在application.yml上面配置，也可在application.properties中进行配置，且properties的优先级比yml要高。

#### 2.@Value获取值和@ConfigurationProperties获取值比较

|                      | @ConfgurationProperties                        | @Value                                            |
| -------------------- | ---------------------------------------------- | ------------------------------------------------- |
| 功能                 | 批量注入配置文件的属性，只需指定一个perfix即可 | 一个个指定                                        |
| 松散绑定（松散语法） | 支持                                           | 不支持                                            |
| SpEL                 | 不支持                                         | 支持                                              |
| JSR303数据校验       | 支持                                           | 不支持                                            |
| 复杂类型封装         | 支持（可以取出相关数据）                       | 不支持（只能取出基本类型的数据，map相似的不可以） |

==松散语法==：下划线或横杠后面跟字母表示大写

==JSR303==：用于对 Java Bean 中的字段的值进行验证。 

配置文件，不管是yml还是properties，他们都能获取到值；

如果说我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，就是用@Value

```java
@RestController
public class HelloController {

    //获取需要打招呼的人名
    @Value("${person.last-name}")
    private String name;

    @RequestMapping("/sayHello")
    public String sayHello(){
        return "Hello "+name;
    }
}

```

如果说我们专门编写了一个JavaBean来和配置文件进行映射，那么我们就直接使用@ConfigurationProperties；

```java
@ConfigurationProperties(prefix = "person")
//表示校验
//@Validated
public class Person {
```

#### 3.配置文件注入值数据校验

```jav
@Component
@ConfigurationProperties(prefix = "person")
//表示校验
@Validated
public class Person {

    /**和@Value类似
     * <bean class="Person">
     *      <property name="lastName"  value="字面量/${key}从环境变量、配置文件中获取值"/#{SPEL}></property>
     * </bean>
     */
    //lastName必须是邮箱格式
    //@Email
    //@Value("${person.last-name}")
    private String lastName;
    //#{SPEL}   代表spring中的表达式求值
    //@Value("#{11*2}")
    private Integer age;
    //@Value("true")
    private Boolean boss;
    private Date birth;

    //表示Map类型的数据
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
```

#### 4.@PropertySource 和 @ImportResource

@PropertySource：告诉SpringBoot加载指定的配置文件，并绑定到全局变量中

​	出现背景：如果所有的变量都放到全局变量application.properties中，会导致application文件过大

​	语法：

​    	@PropertySource(value = {"classpath:person.properties"})

​		==@ConfigurationProperties(prefix = "person")默认从全局配置中获取值；==

@ImportResource：导入Spring的配置文件，让配置文件中的内容生效；

​	Spring Boot里面没有Spring的配置文件，我们自己编写的配置文件也不能自动识别。想让Spring的配置文件生效，加载进来，需要@ImportResource，标注在主配置类上

```
@PropertySource(value = {"classpath:person.properties"})
在SpringBoot02ConfigApplication中加入这句注释
导入Spring的配置文件让其生效
```

原始添加组件方式（不来编写Spring的配置文件）：

```xml
<!--具体见beans.xml-->

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="helloService" class="com.atguigu.springboot.service.HelloService"></bean>
    <!--检测Spring容器中是否存在这个Service-->
</beans>
```

SpringBoot推荐给容器中添加组件的方式（==推荐使用全注解方式==）：

具体见config.MyAppConfig

​		1.配置类 == Spring配置文件

​		2.使用@Bean给容器中添加组件

```java
/**
 * @Configuration：告诉Spring Boot当前类是一个配置类，就是来替代之前的Spring配置文件
 *
 * 在配置文件中用<bean></bean>标签添加组件
 */
@Configuration
public class MyAppConfig {

    //将方法的返回值添加到容器中；容器中这个组件默认的id是方法名
    //返回这个组件，组件就是helloService
    @Bean
    public HelloService helloService(){
        //如果换成helloService02，输出结果为false
        System.out.println("配置类@Bean中添加组件了");
        return new HelloService();
    }
}

```

### 4.配置文件占位符

#### 		1.随机数：

```jav
${random.value}、${random.int}、 ${random.long}
${random.int(10)}、 ${random.int[1024,65536]}
```

#### 		2.占位符获取之前配置的值，如果没有，可以使用“：”指定默认值

在application.properties中配置：

```properties
person.last-name=张三${random.uuid}
person.age=${random.int}
person.birth=2017/12/12
person.boss=false
person.maps.k1=v1
person.maps.k2=14
person.lists=a,b,c
#如果取出dog的名字张三
person.dog.name=${person.last-name}_dog

#如果没有值的话
#person.dog.name=${person.hello:hello}_dog
#结果：'hello_dog'

#如果没有值，自己也不定义值
#person.dog.name=${person.hello}_dog
#结果：'${person.hello}_dog'
    
person.dog.age=15
```

### 5、Profile

​		Profile是Spring对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方式快速切换环境

#### 1.多Profile文件

​		我们在主配置文件编写的时候，文件名可以是application-{profile}.properties或者yml

​		默认使用application.properties的配置

#### 2.yml支持多文档块方式

```yml
server:
  port: 8081
spring:
  profiles:
    active: prod

#person:
#    lastName: hello
#    age: 18
#    boss: false
#    birth: 2017/12/12
#    maps: {k1: v1,k2: 12}
#    lists:
#      - lisi
#      - zhaoliu
#    dog:
#      name: 小狗
#      age: 12
---
server:
  port: 8083
spring:
  profiles: dev
---
server:
  port: 8084
spring:
  profiles: prod		#指定这个文档块属于哪个环境
```



#### 3.激活指定profile

##### 		1.在配置文件中指定激活的配置

​				spring.profiles.active=

##### 		2.命令行方式

​				--spring.profiles.active=

![image-20200402163857519](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200402163857519.png)

##### 		3.虚拟机参数

![image-20200402164256793](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200402164256793.png)

然后直接运行，控制台将会打开dev对应的端口

### 6、配置文件加载位置

- springboot启动会扫描以下位置的application.properties或者application.yml文件作为Spring Boot的默认配置文件

  -file:./config/

  -file:./

  -classpath:/config/

  -classpath:/

  优先级==由高到低==，高优先级的配置会覆盖低优先级的配置，SpringBoot会从这四个位置全部加载主配置文件，高优先级的文件加载后低优先级的文件也会加载，形成一种**互补配置**。

- ==我们还可以通过spring.config.location来改变默认的配置文件位置==

  项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置，指定配置文件和默认加载的这些配置文件共同起作用，形成互补配置

### 7.外部配置加载顺序

​	1.命令行参数

​	2.来自java:comp/env的NDI属性

​	3.java系统属性（System.getProperties()）

​	4.操作系统环境变量

​	5.RandomValuePropertySource配置的random.*属性值

​	6.jar包外部的application-{profile}.properties或application.yml(带spring.profile)配置文件

​	7.jar包内部的application-{profile}.properties或application.yml(带spring.profile)配置文件

​	8.jar包外部的application-{profile}.properties或application.yml(不带spring.profile)配置文件

​	9.jar包内部的application-{profile}.properties或application.yml(不带spring.profile)配置文件

​	10.@Configuration注解类上的@PropertySource

​	11.通过SpringApplication.setDefaultProperties指定的默认属性

### 8.自动配置原理

#### 1.自动配置原理：

##### 	1.SpringBoot启动的时候加载主配置类，并开启了自动配置功能==@EnableAutoConfiguration==

##### 	2.@EnableAutoConfiguration作用：

- 利用AutoConfigurationImportSelector给容器中导入一些组件	

- 可以查看 selectImports()方法的内容，起终下面的代码

```java
List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
//获取候选配置

SpringFactoriesLoader.loadFactoryNames();
//扫描所有jar包类路径下"META-INF/spring.factories"
//然后把扫描到的这些文件的内容包装成properties对象
//从properties中获取到EnableAutoConfiguration.class类（类名）对应的值，把它们添加在容器中
```

**==将类路径下 META-INF/spring.factories 里面配置的所有EnableAutoConfiguration的值加入到容器中==**

```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
org.springframework.boot.autoconfigure.cloud.CloudServiceConnectorsAutoConfiguration,\
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\
org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration,\
org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveRestClientAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration,\
org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration,\
org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,\
org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration,\
org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration,\
org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration,\
org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration,\
org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration,\
org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration,\
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,\
org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,\
org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration,\
org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration,\
org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration,\
org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration,\
org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration,\
org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration,\
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration,\
org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration,\
org.springframework.boot.autoconfigure.session.SessionAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration,\
org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration,\
org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration,\
org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration,\
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration,\
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration,\
org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration,\
org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration
```

每一个这样的 xxxAutoConfiguration类都是容器中的一个组件，都加入到容器中；用他们来做自动配置；

##### 3.每一个自动配置类进行自动配置

​	以**HttpEncodingAutoConfiguration**（Http编码自动配置）为例解释自动配置原理：

```java
//表示这是一个配置类，和以前编写的配置文件一样，也可以给容器中添加组件
@Configuration(proxyBeanMethods = false)

//启用指定类的ConfigurationProperties功能：将配置文件中对应的值和HttpEncodingAutoConfiguration绑定起来，并把HttpProperties加入到ioc容器中
@EnableConfigurationProperties({HttpProperties.class})

//Spring底层有@Conditional注解，根据不同的条件，如果满足指定的条件，整个配置类里面的配置进行生效；		判断当前应用是否是web应用，如果是，当前配置类生效
@ConditionalOnWebApplication(type = Type.SERVLET)

//判断当前项目有没有这个类CharacterEncodingFilter：SpringMVC中进行乱码解决的过滤器
@ConditionalOnClass({CharacterEncodingFilter.class})

//判断这个配置文件中是否存在某个配置"spring.http.encoding"；如果不存在，判断也是成立的
//翻译过来，就是我们配置文件中，不配置spring.http.encoding = true，也是默认生效的
@ConditionalOnProperty(prefix = "spring.http.encoding", value ={"enabled"}, matchIfMissing = true)

public class HttpEncodingAutoConfiguration {
    
    //它已经和SpringBoot的配置文件映射了
    private final Encoding properties;

    //只有一个有参构造器的情况下，参数的值就会从容器中拿出
    public HttpEncodingAutoConfiguration(HttpProperties properties) {
        this.properties = properties.getEncoding();
    }

    
    //如果生效，给容器中默认添加一个CharacterEncodingFilter组件，这个组件的某些值需要从properties中获取
    @Bean
    
    //判断容器没有这个组件
    @ConditionalOnMissingBean		
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setEncoding(this.properties.getCharset().name());
        filter.setForceRequestEncoding(this.properties.shouldForce(org.springframework.boot.autoconfigure.http.HttpProperties.Encoding.Type.REQUEST));
        filter.setForceResponseEncoding(this.properties.shouldForce(org.springframework.boot.autoconfigure.http.HttpProperties.Encoding.Type.RESPONSE));
        return filter;
    }
```

一句话总结：根据当前不同的条件判断，决定这个配置类是否生效，一旦生效，这个配置类就会给容易中添加各种组件，这些组件的属性是从对应的properties类中获取的，这些类中的每一个属性又是和配置文件绑定的

​	

所有配置文件中能配置的属性都是在xxxxProperties类中封装着，配置文件能配置什么就可以参照某个功能对应的这个属性类

```jav
//从配置文件中获取指定的值和bean的属性进行绑定
@ConfigurationProperties(prefix = "spring.http")
public class HttpProperties {
```

如果配置文件中有这些组件，那么就对这些组件在配置文件中进行赋值即可

```properties
#我们能配置的这个功能都是来源于这个功能的properties类
spring.http.encoding.enabled=true
spring.http.encoding.charset=UTF-8
#强制编码成utf-8
spring.http.encoding.force=true
```

##### 4.精髓

==第一种：==

​	1.SpringBoot启动的时候通过@EnableConfiguration内的selectImport()方法找到Spring.factories文件中的所有配置类并加载

​	2.而我们在全局配置文件中的配置通过@ConfigurationProperties绑定到相对应的Properties中的配置类

​	3.并通过@EnableConfigurationProperties注解将properties结尾的配置类注入到spring容器中

==第二种：==

​	**1.SpringBoot启动会加载大量的自动配置类**

​	**2.我们看我们需要的功能有没有SpringBoot默认写好的自动配置类**

​	**3.我们再来看这个自动配置类中到底配置了哪些组件，只要我们要用的组件有，我们就不需要再来配置；如果没有，需要自己写配置类**

​	**4.给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，而这些属性就可以在配置文件中指定这些属性的值**

xxxxAutoConfiguration：自动配置类：给容器中添加组件

xxxxProperties：封装配置文件中相关属性；

#### 2.细节：

##### 	1.@Conditional派生注解（Spring注解版原生的@Conditional作用）

​		作用：必须是@Conditional指定的条件成立才给容器中添加组件，配置里面的所有内容才生效

自动配置类必须在一定条件下才能生效；

我们如何知道哪些自动配置类生效：可以通过启用debug=true属性，来让控制台打印自动配置报告，这样就可以很方便的知道哪些自动配置类生效

```java
Positive matches:		//自动配置类启用的
-----------------

   AopAutoConfiguration matched:
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   AopAutoConfiguration.ClassProxyingConfiguration matched:
      - @ConditionalOnMissingClass did not find unwanted class 'org.aspectj.weaver.Advice' (OnClassCondition)
      - @ConditionalOnProperty (spring.aop.proxy-target-class=true) matched (OnPropertyCondition)

   DispatcherServletAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet' (OnClassCondition)
      - found 'session' scope (OnWebApplicationCondition)
          
          
Negative matches:		//自动配置类没启用和没有匹配成功的
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)

   AopAutoConfiguration.AspectJAutoProxyingConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'org.aspectj.weaver.Advice' (OnClassCondition)

```



## 三、SpringBoot日志

#### 	1.日志框架

市面上的日志框架：JUL、JCL、jboss-logging、logback、log4j、log4j2、slf4j……

| 日志门面（日志的抽象层）                                     | 日志实现                                                 |
| ------------------------------------------------------------ | -------------------------------------------------------- |
| ~~JCL(jakatra commons logging)~~          SLF4j(Simple Logging  Facade for java)          **~~jboss-logging~~** | Log4j    JUL(java.util.logging)    Log4j2    **Logback** |

左边选一个门面（抽象层），右边选一个实现

日志门面：SLF4j；

日志实现：Logback;

SpringBoot：底层是Spring框架，Spring框架默认使用JCL；

​		==SpringBoot选用SLF4j和Logback；==

#### 2.SLF4j使用

##### 1.如何在系统中使用SLF4j

​	以后开发的时候，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里面的方法；

​	给系统里面导入slf4j的jar和logback的实现jar

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

![img](http://www.slf4j.org/images/concrete-bindings.png)

​	蓝色的为实现，墨绿色的为中间的适配层，适配不适配于slf4j框架的日志实现

​	每一个日志的实现框架都有自己的配置文件，配置文件使用slf4j以后，**配置文件还是做成日志实现框架自己本身的配置文件**

##### 2.遗留问题

项目里面用多了日志就会因为日志问题报各种错误

统一日志记录，即使是别的框架和我统一使用slf4j输出？

![img](http://www.slf4j.org/images/legacy.png)

**如何让系统中所有的日志都统一到slf4j：**

==1.将系统中其他的日志框架先排除出去；==

==2.用中间包来替换原有的日志框架；==

==3.我们再来导入slf4j其他的实现==

#### 3.SpringBoot日志关系

```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

SpringBoot使用它来做日志功能

```xml
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
      <version>2.2.6.RELEASE</version>
      <scope>compile</scope>
    </dependency>
```

底层依赖关系

![image-20200404142954209](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20200404142954209.png)

总结：

​		1）SpringBoot底层也是使用slf4j + logback的方式进行日志记录

​		2）SpringBoot也把其他的日志都替换成slf4j

​		3）中间替换包

```java
@SuppressWarnings("rawtypes")
public abstract class LogFactory {
    static String UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J = "http://www.slf4j.org/codes.html#unsupported_operation_in_jcl_over_slf4j";

    static LogFactory logFactory = new SLF4JLogFactory();
```

​		4）如果我们要引入其他框架，一定要把这个框架的默认日志依赖移除掉？

​				**==SpringBoot能自动适配所有的日志，而且底层使用slf4j+logback记录日志，引入其他框架的时候，只需要把这个框架依赖的日志排除掉；==**

#### 4.日志使用：

##### 1.默认配置：

​		SpringBoot默认帮我们配置好了日志；

```java
import org.junit.jupiter.api.Test;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
//是idea自动导入的包有问题，改成下面这个
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SpringBoot03LoggingApplicationTests {

    //日志记录器
    Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    void contextLoads() {
        //过时的方式：System.out.println();
        //日志的级别：由低到高
        //可以调整输出的日志级别，日志就只会在这个级别以后的高级别生效
        logger.trace("这是trace日志...");
        logger.debug("这是debug日志");
        //SpringBoot默认给我们使用的是info及以上级别的
        //要是容trace到error全部输出，需要在application.properties中添加下面的话
        //logging.level.com.atguigu = trace
        //没有指定级别的，就用SpringBoot默认级别的：root级别
        logger.info("这是info日志...");
        logger.warn("这是warn日志...");
        logger.error("这是error的包...");
    }
}
```

```
日志输出格式：
	%d表示日期时间
	%thread表示线程名
	%-5level表示级别从左显示5个字符宽度
	%logger{50}表示logger名字最长50个字符，否则按照句点分割
	%msg表示日志消息
	%n表示换行
	
	%d{yyyy-MM-dd} [%thread] %-5level %logger{50} - %msg%n
```

SpringBoot修改日志的默认配置

```properties
logging.level.com.atguigu = trace

#不指定路径在当前项目下生成springboot日志
#可以指定完整的路径
#logging.file.name=E:/上课笔记/Spring学习/springboot.log
#在当前磁盘的根路径下创建spring文件夹和里面的log文件夹，使用 spring.log 作为默认文件
#若重复使用，以name为准
logging.file.path=/spring/log

#在控制台输出的日志格式
#-5level代表靠左对齐
logging.pattern.console=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} - %msg%n
#指定文件中日志输出的格式
logging.pattern.file=%d{yyyy-MM-dd} === [%thread] === %-5level === %logger{50} ==== %msg%n
```

##### 2.指定配置

​	给类路径下放上每个日志框架自己的配置文件即可，SpringBoot就不适用他的默认配置了

| Logging System          | Customization                                                |
| ----------------------- | ------------------------------------------------------------ |
| Logback                 | `logback-spring.xml`, `logback-spring.groovy`, `logback.xml` or `logback.groovy` |
| Log4j2                  | `log4j2-spring.xml` or `log4j2.xml`                          |
| JDK (Java Util Logging) | `logging.properties`                                         |

logback.xml：直接被日志框架识别

logback.spring.xml：日志框架就不直接加载日志的配置项，由SpringBoot解析日志配置，可以使用SpringBoot的高级Porfile功能

```xml
<springProfile name="staging">
    <!-- configuration to be enabled when the "staging" profile is active -->
      可以指定某段配置只在某个环境下生效
</springProfile>
```

如果使用logback.xml作为日志配置文件，还要使用profile功能，会有以下错误

```
no applicable action for [springProfile]
```

#### 5.切换日志框架

可以按照slf4j的日志时配图进行相关的切换

slf4j+log4j方式：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <artifactId>logback-classic</artifactId>
      <groupId>ch.qos.logback</groupId>
    </exclusion>
    <exclusion>
      <artifactId>log4j-over-slf4j</artifactId>
      <groupId>org.slf4j</groupId>
    </exclusion>
  </exclusions>
</dependency>

<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-log4j12</artifactId>
</dependency>
```

切换为log4j2

```xml
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>spring-boot-starter-logging</artifactId>
                    <groupId>org.springframework.boot</groupId>
                </exclusion>
            </exclusions>
        </dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

