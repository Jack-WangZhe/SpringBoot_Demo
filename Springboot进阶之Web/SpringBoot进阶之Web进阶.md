## SpringBoot进阶之Web进阶

### 一.表单验证

#### 1.为要校验的对象添加对应的校验注解

* 使用`@Min(value=18,message="未成年少女金之入内")`标记对应类中的属性。注解表示age最小值为18，错误提示为：未成年少女禁止入内

* Girl类实例代码

  ```java
  package com.study.demo.domain;
  
  import javax.persistence.Entity;
  import javax.persistence.GeneratedValue;
  import javax.persistence.Id;
  import javax.validation.constraints.Min;
  
  @Entity
  public class Girl{
      @Id
      @GeneratedValue
      private Integer id;
      private String cupSize;
  
      //表示age最小值为18，错误提示为：未成年少女禁止入内
      @Min(value=18,message = "未成年少女禁止入内")
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

#### 2.在对应方法的入参中使用@Vaild注解标记该对应需要被验证，并通过BindingResult类型参数接受验证结果

* 结果对象的hasErrors()方法用于判断是否发生错误

* 结果对象的getFieldError().getDefaultMessage()方法获得对应的错误信息

* 对应方法的实例代码

  ```java
  //新增女生
  @PostMapping(value="/girls")
  public Girl girlAdd(@Valid Girl girl, BindingResult bindingResult){//@Valid注解表示要验证的对象为girl,验证结果会返回到参数bindingResult对象中
      //通过对象的hasErrors()方法获得是否发生错误
      if(bindingResult.hasErrors()){
          //并通过getFieldError().getDefaultMessage()方法获得对应的错误信息
          System.out.println(bindingResult.getFieldError().getDefaultMessage());
          return null;
      }
      girl.setCupSize(girl.getCupSize());
      girl.setAge(girl.getAge());
      return girlRepository.save(girl);//方法返回的就是添加的对象
  }
  ```

* 当发送post请求时，若cupSize属性超过18则会在控制台输出“未成年少女禁止入内”，并返回null值

### 二.AOP统一处理请求日志

#### 1.什么是AOP？

* AOP是一种编程范式，与语言无关，是一种程序设计思想
* 面向切面（AOP）Aspect Oriented Programming:将通用逻辑从业务逻辑中分离出来
* 面向对象（OOP）Object Oriented Programming
* 面向过程（POP）Procedure Oriented Programming

#### 2.网络请求的生命周期

`收到HttpRequest请求` —> `记录请求` —> `处理网络请求` —> `生成HttpResponse` —> `记录回复`

#### 3.操作数据库的生命周期

`收到数据库操作请求` —> `记录请求` —> `增删改查` —> `生成处理结果` —> `记录回复`

#### 4.记录每一个http请求

* 添加依赖

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  ```

* 建立处理文件：aspect/HttpAspect.java

  ```java
  import org.aspectj.lang.annotation.After;
  import org.aspectj.lang.annotation.Aspect;
  import org.aspectj.lang.annotation.Before;
  import org.aspectj.lang.annotation.Pointcut;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.stereotype.Component;
  @Aspect//标记类为Aspect类
  @Component//引入到Spring容器中
  public class HttpAspect {
  
      //org.slf4j.Logger是Spring自带的日志记录框架
      private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);
  
      @Pointcut("execution(public * com.study.demo.controller.HelloController.girlList(..))")//.girlList(..)表示girlList方法不管是任何参数都会被拦截;.*(..)则表示该类的所有方法都会被拦截
      public void log(){
  
      }
  
      //定义在http请求到方法之前将内容记录下来
      @Before("log()")
      public void doBefore(){
          logger.info("记录日志");//使用Logger的方法记录日志
      }
  
      //定义在执行完方法后调用
      @After("log()")
      public void doAfter(){
          logger.info("执行方法之后的调用...");
      }
  }
  ```

* 访问接口时的显示内容(可以看出使用logger.info()方法打印的内容带有时间等相关信息)

  ```shell
  2018-09-19 12:58:55.250  INFO 44464 --- [nio-8082-exec-2] com.study.demo.aspect.HttpAspect         : 记录日志
  2018-09-19 12:58:55.253  INFO 44464 --- [nio-8082-exec-2] c.study.demo.controller.HelloController  : 执行到GirlList
  2018-09-19 12:58:55.280  INFO 44464 --- [nio-8082-exec-2] o.h.h.i.QueryTranslatorFactoryInitiator  : HHH000397: Using ASTQueryTranslatorFactory
  Hibernate: select girl0_.id as id1_0_, girl0_.age as age2_0_, girl0_.cup_size as cup_size3_0_ from girl girl0_
  2018-09-19 12:58:55.364  INFO 44464 --- [nio-8082-exec-2] com.study.demo.aspect.HttpAspect         : 执行方法之后的调用...
  ```

* 调整@Before注解的方法执行指定内容

  ```java
  @Before("log()")
  public void doBefore(JoinPoint joinPoint){
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = attributes.getRequest();
      //url
      logger.info("url={}",request.getRequestURL());
      //method
      logger.info("method={}",request.getMethod());
      //客户端的ip
      logger.info("ip={}",request.getRemoteAddr());
      //请求的类方法,通过当前类的参数传入的JoinPoint对象获取对应的内容
      //joinPoint.getSignature().getDeclaringTypeName()获取的是类名
      //joinPoint.getSignature().getName()获取类方法名
      logger.info("class_method={}",joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
      //参数
      logger.info("args={}",joinPoint.getArgs());
  }
  ```

* 通过`@AfterReturning`注解获得方法返回的内容

  ```java
  //参数1为方法的入参变量名，参数2为使用的连接点方法名
  @AfterReturning(returning="",pointcut="log()")
  //返回的内容可以是很多，但是对于程序而言他们都是对象
  public void doAfterReturning(Object object){
      logger.info("response={}",object);
  }
  ```

* 访问接口时显示的内容

  ```shell
  2018-09-19 13:20:31.876  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : url=http://localhost:8082/girls
  2018-09-19 13:20:31.876  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : method=GET
  2018-09-19 13:20:31.876  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : ip=0:0:0:0:0:0:0:1
  2018-09-19 13:20:31.878  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : class_method=com.study.demo.controller.HelloController.girlList
  2018-09-19 13:20:31.878  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : args={}
  2018-09-19 13:20:31.882  INFO 44599 --- [nio-8082-exec-1] c.study.demo.controller.HelloController  : 执行到GirlList
  2018-09-19 13:20:31.911  INFO 44599 --- [nio-8082-exec-1] o.h.h.i.QueryTranslatorFactoryInitiator  : HHH000397: Using ASTQueryTranslatorFactory
  Hibernate: select girl0_.id as id1_0_, girl0_.age as age2_0_, girl0_.cup_size as cup_size3_0_ from girl girl0_
  2018-09-19 13:20:31.991  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : 执行方法之后的调用...
  2018-09-19 13:20:31.991  INFO 44599 --- [nio-8082-exec-1] com.study.demo.aspect.HttpAspect         : response=[]
  ```

### 三.统一异常处理

#### 统一异常处理的实例

* 定义统一异常返回对象`/src/main/java/com/study/demo/domain/Result.java`

  ```java
  package com.study.demo.domain;
  /**
   * Http请求返回的最外层对象
   */
  public class Result<T> {
      /* 错误码 */
      private Integer code;
      /* 提示信息 */
      private String msg;
      /* 具体内容 */
      private T data;
      public Integer getCode() {
          return code;
      }
      public void setCode(Integer code) {
          this.code = code;
      }
      public String getMsg() {
          return msg;
      }
      public void setMsg(String msg) {
          this.msg = msg;
      }
      public T getData() {
          return data;
      }
      public void setData(T data) {
          this.data = data;
      }
      @Override
      public String toString() {
          return "Result{" +
                  "code=" + code +
                  ", msg='" + msg + '\'' +
                  ", data=" + data +
                  '}';
      }
  }
  ```

* 定义错误对象生成工具方法`/src/main/java/com/study/demo/utils/ResultUtil.java`

  ```java
  package com.study.demo.utils;
  import com.study.demo.domain.Result;
  public class ResultUtil {
      public static Result success(Object object){
          Result result = new Result();
          result.setCode(0);
          result.setMsg("成功");
          result.setData(object);
          return result;
      }
      public static Result success(){
          return success(null);
      }
      public static Result error(Integer code,String msg){
          Result result = new Result();
          result.setCode(code);
          result.setMsg(msg);
          result.setData(null);
          return result;
      }
  }
  ```

* 自定义异常，用于传递对应的错误编号`/src/main/java/com/study/demo/exception/GirlException.java`

  ```java
  package com.study.demo.exception;
  import com.study.demo.enums.ResultEnum;
  public class GirlException extends RuntimeException {
      private Integer code;
      public GirlException(ResultEnum resultEnum){
          super(resultEnum.getMsg());
          this.code = resultEnum.getCode();
      }
      public Integer getCode() {
          return code;
      }
      public void setCode(Integer code) {
          this.code = code;
      }
  }
  ```

* 定义异常处理内容，在方法抛出异常时执行的对应处理方法`/src/main/java/com/study/demo/handle/ExceptionHandle.java`

  ```java
  package com.study.demo.handle;
  import com.study.demo.domain.Result;
  import com.study.demo.exception.GirlException;
  import com.study.demo.utils.ResultUtil;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.web.bind.annotation.ControllerAdvice;
  import org.springframework.web.bind.annotation.ExceptionHandler;
  import org.springframework.web.bind.annotation.ResponseBody;
  @ControllerAdvice
  public class ExceptionHandle {
      public static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
      //声明要捕获哪个异常类
      @ExceptionHandler(value = Exception.class)
      //由于要返回的是一个json，但是类上方又没有@RestController注解，故需要用ResponseBody修饰
      @ResponseBody
      public Result handle(Exception e){
          if(e instanceof GirlException){
              GirlException girlException = (GirlException)e;
              return ResultUtil.error(girlException.getCode(),girlException.getMessage());
          }else{
              logger.error("[系统异常]{}",e);
              return ResultUtil.error(-1,"未知错误");
          }
      }
  }
  
  ```

* 在Controller中添加判断对象年龄并抛出异常测试方法`/src/main/java/com/study/demo/controller/HelloController.java`

  ```java
  package com.study.demo.controller;
  
  import com.study.demo.domain.Girl;
  import com.study.demo.domain.Result;
  import com.study.demo.repository.GirlRepository;
  import com.study.demo.service.GirlService;
  import com.study.demo.utils.ResultUtil;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.context.properties.bind.BindResult;
  import org.springframework.validation.BindingResult;
  import org.springframework.web.bind.annotation.*;
  
  import javax.validation.Valid;
  import java.util.List;
  import java.util.Optional;
  
  @RestController
  public class HelloController {
  
      public final static Logger logger = LoggerFactory.getLogger(HelloController.class);
  
      @Autowired
      private GirlRepository girlRepository;
      @Autowired
      public GirlService girlService;
  
      //查询所有女生列表
      @GetMapping(value="/girls")
      public List<Girl> girlList(){
          logger.info("执行到GirlList");
          return girlRepository.findAll();
      }
  
      //新增女生
      @PostMapping(value="/girls")
      public Result girlAdd(@Valid Girl girl, BindingResult bindingResult){//@Valid注解表示要验证的对象为girl,验证结果会返回到参数bindingResult对象中
          //通过对象的hasErrors()方法获得是否发生错误
          if(bindingResult.hasErrors()){
              return ResultUtil.error(1,bindingResult.getFieldError().getDefaultMessage());
          }
          girl.setCupSize(girl.getCupSize());
          girl.setAge(girl.getAge());
          return ResultUtil.success(girlRepository.save(girl));//方法返回的就是添加的对象
      }
  
      //查询一个女生
      @GetMapping(value="/girls/{id}")
      public Optional<Girl> girlFindOne(@PathVariable("id")Integer id){
          Optional<Girl> girl = girlRepository.findById(id);
          return girl;
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
          girlRepository.deleteById(id);
      }
  
      //通过年龄查询女生列表
      @GetMapping(value="/girls/age/{age}")
      public List<Girl>girlListByAge(@PathVariable("age")Integer age){
          return girlRepository.findByAge(age);
      }
  
      //添加两条数据
      @PostMapping(value="/girls/two")
      public void girlTwo(){
          girlService.insertTwo();
      }
  
      //判断对象年龄并抛出异常测试方法
      @GetMapping(value = "girls/getAge/{id}")
      public void getAge(@PathVariable("id")Integer id) throws Exception{
          girlService.getAge(id);
      }
  }
  ```

* 在service中添加对应处理方法，针对不同情况的age内容抛出不同错误码的异常`/src/main/java/com/study/demo/service/GirlService.java`

  ```java
  package com.study.demo.service;
  import com.study.demo.domain.Girl;
  import com.study.demo.enums.ResultEnum;
  import com.study.demo.exception.GirlException;
  import com.study.demo.repository.GirlRepository;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  import java.util.Optional;
  @Service
  public class GirlService {
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
          girlB.setCupSize("Fffffff");
          girlB.setAge(19);
          girlRepository.save(girlB);
      }
      public void getAge(Integer id) throws GirlException{
          Optional<Girl> girl = girlRepository.findById(id);
          Integer age = girl.get().getAge();
          if(age<10){
              //返回你还在上小学吧——使用枚举的形式获得
              throw new GirlException(ResultEnum.PRIMARY_SCHOOL);
          }else if(age>=10&&age<16){
              //返回你可能在上初中
              throw new GirlException(ResultEnum.MIDDLE_SCHOOL);
          }
      }
  }
  ```

* 定义枚举类型，将错误编号与错误信息统一管理`/src/main/java/com/study/demo/enums/ResultEnum.java`

  ```java
  package com.study.demo.enums;
  //使用枚举将错误编号与错误信息统一管理
  public enum ResultEnum {
      UNKOWN_ERROR(-1,"未知错误"),
      SUCCESS(0,"成功"),
      PRIMARY_SCHOOL(100,"你可能在上小学~"),
      MIDDLE_SCHOOL(101,"你可能在上初中~")
      ;
      private Integer code;
      private String msg;
      //枚举的使用基本上不用set方法，而使用构造方法
      ResultEnum(Integer code, String msg) {
          this.code = code;
          this.msg = msg;
      }
      public Integer getCode() {
          return code;
      }
      public String getMsg() {
          return msg;
      }
  }
  ```

### 四.单元测试

#### 1.对service进行测试方式一：创建`/src/test/java/com/study/demo/GirlServiceTest.java`文件编写findOneTest方法进行测试

* 在GirlService中编写需要测试的方法

  ```java
  package com.study.demo.service;
  import com.study.demo.domain.Girl;
  import com.study.demo.enums.ResultEnum;
  import com.study.demo.exception.GirlException;
  import com.study.demo.repository.GirlRepository;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  import java.util.Optional;
  @Service
  public class GirlService {
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
          girlB.setCupSize("Fffffff");
          girlB.setAge(19);
          girlRepository.save(girlB);
      }
      public void getAge(Integer id) throws GirlException{
          Optional<Girl> girl = girlRepository.findById(id);
          Integer age = girl.get().getAge();
          if(age<10){
              //返回你还在上小学吧
              throw new GirlException(ResultEnum.PRIMARY_SCHOOL);
          }else if(age>=10&&age<16){
              //返回你可能在上初中
              throw new GirlException(ResultEnum.MIDDLE_SCHOOL);
          }
      }
      /**
       * 通过Id查询一个女生的信息
       */
      public Optional<Girl> findOne(Integer id){
          return girlRepository.findById(id);
      }
  }
  
  ```

* 编写GirlServiceTest方法进行测试(`右击Run on findOneTest`进行执行)

  ```java
  package com.study.demo;
  import com.study.demo.domain.Girl;
  import com.study.demo.service.GirlService;
  import org.junit.Assert;
  import org.junit.Test;
  import org.junit.runner.RunWith;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.test.context.junit4.SpringRunner;
  import java.util.Optional;
  //表示当前是在测试环节里面跑的，底层使用JUnit测试工具
  @RunWith(SpringRunner.class)
  //将启动整个Spring的工程
  @SpringBootTest
  public class GirlServiceTest {
      @Autowired
      private GirlService girlService;
      @Test
      public void findOneTest(){
          Optional<Girl> girl = girlService.findOne(4);
          //断言的方式判断，要求参数都是Object
          Assert.assertEquals(new Integer(16),girl.get().getAge());
      }
  }
  ```

#### 2.对service进行测试方法二：利用IDEA生成对应的测试方法

* 在对应需要测试的方法上`右击 ->  Go To -> Test ->Create new Test -> 选择要测试的方法 -> OK`
* 系统会自动在test包下创建对应包即测试文件

#### 3.对controller进行测试

* 使用IDEA执行`右击 ->  Go To -> Test ->Create new Test -> 选择要测试的方法 -> OK`生成对应的测试文件

* 在测试文件类上不仅要添加`@RunWith(SpringRunner.class)和@SpringBootTest`注解，还要添加`@AutoConfigurationMockMvc`注解标注是controller的测试类，并使用MockMvc对象的perform方法进行相关测试内容的设置

  ```java
  package com.study.demo.controller;
  import org.junit.Test;
  import org.junit.runner.RunWith;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.test.context.junit4.SpringRunner;
  import org.springframework.test.web.servlet.MockMvc;
  import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
  import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
  import static org.junit.Assert.*;
  @RunWith(SpringRunner.class)
  @SpringBootTest
  @AutoConfigureMockMvc
  public class HelloControllerTest {
      @Autowired
      private MockMvc mvc;
      @Test
      public void girlList() throws Exception{
          mvc.perform(MockMvcRequestBuilders.get("/girls"))//Get方法，请求地址为/girls
                  .andExpect(MockMvcResultMatchers.status().isOk())//表示期望返回的状态码为200
                  .andExpect(MockMvcResultMatchers.content().string("abc"));//表示期望返回的是abc字符串
      }
  }
  ```

#### 4.执行`mvn clean package`进行打包的时候会自动执行对应的单元测试

* 如果单元测试用例测试不通过则提示报错信息，打包失败
* 如果单元测试用例测试通过则没有错误信息，打包成功
* 当我们打包时希望不执行单元测试时，需要使用命令`mvn clean package -Dmaven.test.skip=true`跳过单元测试

