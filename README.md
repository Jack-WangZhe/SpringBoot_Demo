## SpringBoot入门

### 一.SpringBoot和SpringMVC的关系

* SpringBoot是SpringMVC的升级版

### 二.SpringBoot的特点

* 化繁为简，简化配置
* 备受关注，是下一代框架
* 微服务的入门级微框架
  * 微服务：主流架构

### 三.第一个SpringBoot应用

* 使用IDEA创建SpringBoot的web应用

* 项目目录

  * pom.xml为maven的配置文件

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>com.study</groupId>
        <artifactId>first-springboot</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>jar</packaging>
    
        <name>first-springboot</name>
        <description>Demo project for Spring Boot</description>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.0.5.RELEASE</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>1.8</java.version>
        </properties>
    
        <dependencies>
            <!-- 作为web项目必须引入的依赖 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter</artifactId>
            </dependency>
            <!-- 单元测试时用到的依赖 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </project>
    ```

  * src/main/java

    ```java
    package com.study.firstspringboot;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    
    //想要启动项目必须要使用@SpringBootApplication注解
    @SpringBootApplication
    public class FirstSpringbootApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(FirstSpringbootApplication.class, args);
        }
    }
    ```

  * src/main/resources/application.properties是springboot的配置文件

  * src/test为单元测试的目录

* 启动项目`右键->Run`

* 在项目的src/main/java中添加HelloController文件

  ```java
  package com.study.demo;
  
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestMethod;
  import org.springframework.web.bind.annotation.RestController;
  @RestController
  public class HelloController {
      @RequestMapping(value="/hello",method = RequestMethod.GET)
      public String hello(){
          return "Hello SpringBoot";
      }
  }
  ```

  * 访问http://localhost:8080/hello即可以看到页面显示Hello SpringBoot

* 第二种启动方式：到指定路径下使用命令`mvn spring-boot:run`

* 第三种启动方式：

  * 先使用`mvn install`将程序编译一下
  * 进入target目录下，使用命令`java -jar 指定jar包文件名`

### 四.属性配置

#### 1.配置src/main/resources

* 配置application.properties文件:访问http://localhost:8081/demo/hello

```properties
#运行时端口
server.port=8081
#项目前缀
server.servlet.context-path=/demo
```

* 推荐新建application.yml替换application.properties文件，因为yml语法简洁

  [注意：yml语法表示变量名和值之间的冒号后要有空格]

  访问http://localhost:8082/demo/hello

```yml
server:
  port: 8082
  servlet:
    context-path: /demo
```

#### 2.通过注解将配置文件中的变量赋值给项目文件中

* 单个映射配置中的变量【主要注解：`@Value`】

  * 使用@Value匹配配置文件中的属性，使用的语法是`${配置文件中的属性名}`

  * 实例代码

    ```java
    //application.yml文件中内容
    server:
      port: 8082
    cupSize: B
    age: 18
    //HelloController.java
    package com.study.demo;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;
    
    @RestController
    public class HelloController {
    
        @Value("${cupSize}")
        public String cupSize;
    
        @Value("${age}")
        public Integer age;
    
        @RequestMapping(value="/hello",method = RequestMethod.GET)
        public String hello(){
            return "Hello Girl"+age+"  "+cupSize;
        }
    }
    ```

* 在配置文件中属性间的使用

  * 在配置文件中也使用`${变量名}`进行相互调用

  * 实例代码

    ```java
    //application.yml
    server:
      port: 8082
    cupSize: B
    age: 18
    content: "Girl is ${age}, and cupSize is ${cupSize}"
    //HelloController.java
    package com.study.demo;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;
    @RestController
    public class HelloController {
    	@Value("${content}")
        public String content;
        @RequestMapping(value="/hello",method = RequestMethod.GET)
        public String hello(){
            return content;
        }
    }
    ```

  * 访问http://localhost:8082/hello页面输出`Girl is 18, and cupSize is B`

* 将配置文件中的多个属性一起映射到类中【对配置分组主要注解：`@Component @ConfigurationProperties`】

  * 使用@Componet标记组件为Spring组件[用于@Autowired自动装载]，使用@ConfigurationProperties的prefix属性标记匹配的项目配置文件中的变量

  * 实例代码

    ```java
    //application.yml
    server:
      port: 8082
    person:
      cupSize: B
      age: 18
    //Person.java
    package com.study.demo;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;
    @Component
    @ConfigurationProperties(prefix = "person")
    public class Person {
        public int age;
        public String cupSize;
    
        public int getAge() {
            return age;
        }
    
        public void setAge(int age) {
            this.age = age;
        }
    
        public String getCupSize() {
            return cupSize;
        }
    
        public void setCupSize(String cupSize) {
            this.cupSize = cupSize;
        }
    
        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", cupSize='" + cupSize + '\'' +
                    '}';
        }
    }
    //HelloController.java
    package com.study.demo;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;
    
    @RestController
    public class HelloController {
    
        @Autowired
        public Person person;
    
        @RequestMapping(value="/hello",method = RequestMethod.GET)
        public String hello(){
            return person.toString();
        }
    
    }
    ```

  * 访问http://localhost:8082/hello页面输出`Person{age=18, cupSize='B'}`

* 在不同情况下使用不同的配置文件【多环境配置】

  * 通过修改application.yml中的`spring:prifiles:active`切换不同的配置文件

    * 创建`application-dev.yml`文件和`application-run.yml`(后缀名随意起，调用时指定对应后缀名即可)

      ```yml
      #application-dev.yml
      server:
        port: 8082
      person:
        cupSize: B
        age: 18
      
      #application-run.yml
      server:
        port: 8082
      person:
        cupSize: F
        age: 25
      ```

    * 修改application.yml的文件内容为对应调用形式

      ```yml
      spring:
        profiles:
          active: dev
      #active的值表示调用的是哪个后缀名的application-xxx.yml
      ```

    * 其他代码

      ```java
      //Person.java
      package com.study.demo;
      
      import org.springframework.boot.context.properties.ConfigurationProperties;
      import org.springframework.stereotype.Component;
      
      @Component
      @ConfigurationProperties(prefix = "person")
      public class Person {
          public int age;
          public String cupSize;
      
          public int getAge() {
              return age;
          }
      
          public void setAge(int age) {
              this.age = age;
          }
      
          public String getCupSize() {
              return cupSize;
          }
      
          public void setCupSize(String cupSize) {
              this.cupSize = cupSize;
          }
      
          @Override
          public String toString() {
              return "Person{" +
                      "age=" + age +
                      ", cupSize='" + cupSize + '\'' +
                      '}';
          }
      }
      //HelloController.java
      package com.study.demo;
      
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.beans.factory.annotation.Value;
      import org.springframework.web.bind.annotation.RequestMapping;
      import org.springframework.web.bind.annotation.RequestMethod;
      import org.springframework.web.bind.annotation.RestController;
      
      @RestController
      public class HelloController {
      
          @Autowired
          public Person person;
      
          @RequestMapping(value="/hello",method = RequestMethod.GET)
          public String hello(){
              return person.toString();
          }
      }
      ```

    * 当active指定为dev时，访问http://localhost:8082/hello结果为`Person{age=18, cupSize='B'}`;当active指定为run时，访问http://localhost:8082/hello结果为`Person{age=25, cupSize='F'}`

  * 通过java -jar配置参数的方式切换不同的配置文件

    * 首先通过`mvn install`重新装载
    * 通过`java -jar target/.jar --spring.profiles.active=对应配置文件的选项`运行不同的配置文件
    * 如`java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`时页面显示`Person{age=18, cupSize='B'}`;当执行`java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=run`时页面显示`Person{age=25, cupSize='F'}`

### 五.Controller控制器的使用

* @Controller：处理http请求，需要返回模板

  * 使用Spring官方的模板引擎[加入到pom.xml后需要`右击->Maven->Reimport`]

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
  </dependency>
  ```

  - 在resources下新建`templates/index.html`

    ```html
    <h1>
        hello Spring Boot!
    </h1>
    ```

  - HelloController.java内容如下

    ```java
    package com.study.demo;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;
    
    @Controller
    public class HelloController {
        @RequestMapping(value="/hello",method = RequestMethod.GET)
        public String hello(){
            return "index";
        }
    }
    ```

* @RestController：Spring4之后新加的注解，原来返回json需要@ResponseBody配合@Controller

* @RequestMapping：配置url映射（可以修饰类和方法）

* @PathVariable：获取url中的数据

  ```java
  package com.study.demo;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestMethod;
  import org.springframework.web.bind.annotation.RestController;
  
  @RestController
  public class HelloController {
      @RequestMapping(value="/hello/{id}",method = RequestMethod.GET)
      public String hello(@PathVariable("id")String id){
          return "id:"+id;
      }
  }
  
  //访问http://localhost:8082/hello/1
  //页面输出 id:1
  ```

* @RequestParam：获取请求参数的值

  ```java
  package com.study.demo;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.*;
  
  @RestController
  public class HelloController {
      @RequestMapping(value="/hello",method = RequestMethod.GET)
      public String hello(@RequestParam(value="id",required=false,defaultValue="0")String id){
          return "id:"+id;
      }
  }
  
  //访问http://localhost:8082/hello 页面输出 id:0
  //访问http://localhost:8082/hello?id=100 页面输出 id:100
  ```

* @GetMapping，@PostMapping...：组合注解使用对应的请求方式

  ```java
  package com.study.demo;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.*;
  @RestController
  public class HelloController {
      @GetMapping
      public String hello(){
          return "Hello SpringBoot";
      }
  }
  //以get形式访问http://localhost:8082/hello时，页面输出Hello SpringBoot
  ```

### 六.数据库操作

#### 1.Spring-Data-Jpa

* JPA(Java Persistence API)定义了一系列对象持久化的标准，目前实现这一规范的产品有Hibernate、TopLink等
* Spring-Data-Jpa就是spring对Hibernate的整合

#### 2.实例演示

* RESTful API设计

| 请求类型 | 请求路径  | 功能               |
| -------- | --------- | ------------------ |
| GET      | /girls    | 获取女生列表       |
| POST     | /girls    | 创建一个女生       |
| GET      | /girls/id | 通过id查询一个女生 |
| PUT      | /girls/id | 通过id更新一个女生 |
| DELETE   | /girls/id | 通过id删除一个女生 |

* 要使用数据库，需要添加两个依赖

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
  	<groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
  </dependency>
  ```

* 对spring-data-jpa和mysql进行相应配置，将配置信息写到application.yml中则表示dev和run配置都可以使用的到。

  * `ddl-auto:create`表示每次程序创建的时候都会创建一个空的表，若不存在则创建，若存在则重新创建（不保留数据）
  * `ddl-auto:update`表示每次程序创建的时候都会更新表，若不存在则创建，若存在保留数据
  * `ddl-auto:create-drop`表示应用停下来的时候会删除表
  * `ddl-auto:none`表示什么都不做
  * `ddl-auto:validate`会验证类中的结构跟表结构是否相同，不相同则报错

  ```yml
  spring:
  	profiles:
  		active:dev
  	datasource：
  		driver-class-name:com.mysql.jdbc.Driver
  		url:jdbc:mysql://127.0.0.1:3306/dbgirl
  		username:root
  		password:123456
  	jpa:
  		hibernate:
  			ddl-auto:create
  		show-sql:true
  ```

* 新建Girl类(@Entity表示此类对应数据库中的表,@Id标记id字段，@GeneratedValue表示自增)

  ```java
  package com.study.demo;
  
  import javax.persistence.Entity;
  import javax.persistence.GeneratedValue;
  import javax.persistence.Id;
  
  @Entity
  public class Girl{
      @Id
      @GeneratedValue
      private Integer id;
      private String cupSize;
      private Integer age;
  
      public Integer getId() {
          return id;
      }
  
      public void setId(Integer id) {
          this.id = id;
      }
  
      public String getCupSize() {
          return cupSize;
      }
  
      public void setCupSize(String cupSize) {
          this.cupSize = cupSize;
      }
  
      public Integer getAge() {
          return age;
      }
  
      public void setAge(Integer age) {
          this.age = age;
      }
  
      @Override
      public String toString() {
          return "Girl{" +
                  "id=" + id +
                  ", cupSize='" + cupSize + '\'' +
                  ", age=" + age +
                  '}';
      }
  }
  ```

* 执行项目，会自动在dbgirl数据库中创建girl表

* 创建GirlRepository接口继承JpaRepository(里面封装了操作数据库的方法)

  ```java
  public interface GirlRepository extends JpaRepository<Girl,Integer>{}
  ```

* 编写控制器GirlController.java

  * 使用Postman测试PUT和Delete时需要使用body的x-www-form-urlencoded选项

  ```java
  @Autowired
  private GirlRepository girlRepository;
  
  //查询所有女生列表
  @GetMapping(value="/girls")
  public List<Girl>girlList(){
      return girlRepository.findAll();
  }
  
  //新增女生
  @PostMapping(value="/girls")
  public Girl girlAdd(@RequestParam("cupSize")String cupSize,@RequestParam("age")Integer age){
      Girl girl = new Girl();
      girl.setCupSize(cupSize);
      girl.setAge(age);
      return girlRepository.save(girl);//方法返回的就是添加的对象
  }
  
  //查询一个女生
  @GetMapping(value="/girls/{id}")
  public Girl girlFindOne(@PathVariable("id")Integer id){
      return girlRepository.findOne(id);
  }
  
  //更新
  @PutMapping(value="/girls/{id}")
  public Girl girlUpdate(@PathVariable("id")Integer id,@RequestParam("cupSize")String cupSize,@RequestParam("age")Integer age){
      Girl girl = new Girl();
      girl.setId(id);
      girl.setCupSize(cupSize);
      girl.setAge(age);
      return girlRepository.save(girl);
  }
  
  //删除
  @DeleteMapping(value="/girls/{id}")
  public void girlDelete(@PathVariable("id")Integer id){
      return girlRepository.delete(id);
  }
  
  //通过年龄查询女生列表
  @GetMapping(value="/girls/age/{age}")
  public List<Girl>girlListByAge(@PathVariable("age")Integer age){
      return girlRepository.findByAge(age);
  }
  ```

* 定义查询相关请求

  * 在GirlRepository中添加相关方法

    ```java
    public interface GirlRepository extends JpaRepository<Girl,Integer>{
        //通过年龄来查询,方法名需要按照格式去书写
        public List<Girl>findByAge(Integer age);
    }
    ```

### 七.事务管理

#### 1.实例演示事务操作

* 使用`@Transactional`标记方法中的操作为事务操作

* **注意：升级到Springboot2.0后，使用jpa操作数据库，发现默认创建表是myisam引擎，而不是innodb，myisam不支持使用@Transactional注解，故需要通过application.yml将引擎设置为innodb类型**

  ```yml
  spring:
    profiles:
      active: run
    datasource:
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://127.0.0.1:3306/dbgirl
      username: root
      password: 123456
    jpa:
      hibernate:
        ddl-auto: create
      show-sql: true
      database-platform: org.hibernate.dialect.MySQL5InnoDBDialect  #不加这句则默认为myisam引擎
  ```

* 实例内容

  * 效果：当同时插入两条数据时，如果任意条数据插入失败则回滚事务
  * 实例代码如下

```java
//新增GirlService.java
@Service
public class GirlService{
    @Autowired
    private GirlRepository girlRepository;
    
    //添加@Transactional事务注解
    @Transactional
    public void insertTwo(){
        Girl girlA = new Girl();
        girlA.setCupSize("A");
        girlA.setAge(18);
        girlRepository.save(girlA);
        
        Girl girlB = new Girl();
        girlB.setCupSize("F");
        girlB.setAge(19);
        girlRepository.save(girlB);
    }
}

//HelloController.java新增代码
@Autowired
public GirlService girlService;

@PostMapping(value="/girls/two")
public void girlTwo(){
    girlService.insertTwo();
}
```

#### 2.适宜添加事务的场景

* 增加数据
* 修改数据
* 删除数据