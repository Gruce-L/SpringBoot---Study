## 一、主要成员：

#### 		1.Spring FrameWork：

​			用于构建企业级应用的轻量级一站式解决方案

#### 		2.Spring Boot：

​			Spring Boot为快速启动且最小化配置的spring应用而设计，并且它具有用于构建生产级别应用的一套固化的视图

​				特点：提供各种非功能特性；不用生成代码，没有XML配置

#### 		3.Spring Cloud（从单机到云开发）：

**基于Spring Boot的微服务解决方案**。它为开发者提供了很多工具，用于快速构建分布式系统的一些通用模式，例如：配置管理、注册中心、服务发现、限流、网关、链路追踪等。

## 二、为何Spring Boot 和 Spring Cloud 出现是必然的：

#### 		1.开箱即用

#### 		2.与生态圈的深度整合			3.==注重运维===		

## 三、编写第一个Spring程序

#### 		1.Spring Boot常用注解：

​			（1）@Controller： 处理http请求，用来响应页面，但是必须配合模板使用

​			（2）@RestController ：是@ResponseBody和@Controller的组合注解。是Spring4后加入的新注解，原来返回json需要以上两个注解

​			（3）@RequestMapping：配置url（对可以从互联网上得到的资源的位置和访问方法的一种简洁的表示）映射，即注解会应用到控制器的所有处理器方法上。

#### 	2.创建一个maven工程：

​		（1）导入依赖springboot相关的依赖：

​		（2）编写一个主程序：启动spring boot应用

```
//需要让idea知道这是一个springboot程序，需要加入springboot注解
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

​		（3）编写相关的Controller，Service

```
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
```

​	 （4）简化部署工作

```
<!--这个插件，可以将应用打包成一个可执行的jar包-->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

将这个应用打成jar包，直接使用java -jar的命令进行执行



## 四、HelloWorld探究

#### 		1.POM文件

##### 			（1）父项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>

他的父项目为
<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-dependencies</artifactId>
		<version>1.5.9.RELEASE</version>
		<relativePath>../../spring-boot-dependencies</relativePath>
	</parent>
他来真正管理Spring Boot应用里面所有的依赖版本
```

#### 		2.启动器

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

##### spring-boot-starter-==web==:

​		spring- boot-starter：spring-boot场景启动器：帮我们导入了web模块正常运行所依赖的组件；

​		Spring Boot将所有的功能场景都抽取出来，做成一个个的starter，也称启动器；只需要在项目里面引入这些starter相关场景的所有依赖都会导入进来，要用什么就导入什么场景的启动器

#### 3.主程序类，主入口类

```java
//需要让idea知道这是一个springboot程序，需要加入springboot注解
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

​		@SpringBootApplication：SpringBoot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动SpringBoot应用；

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

@SpringBootConfiguration：SpringBoot的配置类：标注在某个类上，表示这是一个Spring Boot的配置类

@Configuration：配置类上来标注这个注解；  配置类-------配置文件； 配置类也是容器中的一个组件：@Component

@EnableAutoConfiguration：开启自动配置功能：以前我们需要配置的东西，现在Spring Boot帮我们自动配置，@EnableAutoConfiguration告诉Spring Boot开启自动配置功能，这样自动配置才能生效

​		实现原理：

```Java
@AutoConfigurationPackage
@Import({EnableAutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

@AutoConfigurationPackage：自动配置包

​		@Import({Registrar.class})：Spring中的底层注解@Import，给容器中注入一个组件；导入的组件由AutoConfigurationPackage.Registrar.class

​		==将主配置类（@SpringBootApplication标注的类）的所在包及子包里面的所有组件扫描到Spring容器==

​		EnableAutoConfigurationImportSelector：将所有需要导入的组件以全类名的形式返回，这些组件就会被添加到容器中，会给容器中导入非常多的自动配置类（×××AutoConfiguration）；就是给容器中导入这个场景需要的所有组件并配置好这些组件

有了自动配置类，就免去手动编写配置注入功能组件等工作

​		==具体如何实现的：==

```
SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.getBeanClassLoader())
getBeanClassLoader()：类的加载器机制
Spring Boot启动的时候从类路径下META-INF/spring.factories中获取EnableAutoConfiguration指定的值；将这些值作为自动配置类导入到容器中，自动配置类生效，帮我们进行自动配置工作；以前我们自己需要配置的东西，自动配置类都帮我们
```

J2EE的整体整合解决方案和自动配置都在spring-boot-autoconfiguration-1.5.9.RELEASE.jar；