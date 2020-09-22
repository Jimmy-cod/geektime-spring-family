# 玩转Spring全家桶

[极客时间](https://time.geekbang.org)视频课程《玩转Spring全家桶》课程课件及代码示例。

# Spring MVC

**参考与示例来源**

- [极客时间之丁雪丰老师Spring系列](!https://time.geekbang.org/course/intro/156)

**官方文档**

1.https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-servlet

核心：DispatchServlet 所有请求的入口；

- Controller
- XXXResolver
  - ViewResolver
  - HandlerException
  - MultipartResolver
- HanlderMapping



# 1. 常用注解

## 1.1 @Controller

控制器注解

**快捷设置：**

- @RestController Rest 形式服务，结合了`@Controller`与`@ResponseBody`注解



## 1.2 @RequestMapping

当前 Controller 要处理哪些请求；

**属性:**

- path / method 指定映射路径与方法；

  ```kotlin
  @RequestMapping(path = ["/"], method = [RequestMethod.GET])
  fun getAll():List<T> {...}
  ```

- params / headers 限制映射范围；

- consumes / produces 限定请求与响应格式；

**快捷设置：**

- @GetMapping / @PostMapping /@PutMapping / @DeleteMapping



## 1.3 @RequestBody

请求体`POST请求`，默认`json`请求体，比如：

```json
{
	"customer" : "better",
	"items" : ["latte", "mocha"]
}
```

后台接收：

```kotlin
@PostMapping("/")
@ResponseStatus(HttpStatus.CREATED)
fun create(@RequestBody newOrder: NewOrderRequest): CoffeeOrder {
    log.info("Receive new Order {}", newOrder)
    val coffeeList = coffeeService.getCoffeeByName(*newOrder.items?.toTypedArray()!!)
    return orderService.createOrder(newOrder.customer ?: "", *coffeeList.toTypedArray())
}

// NewOrderRequest 类
data class NewOrderRequest(
        var customer: String? = "",
        var items: List<String>? = null
)
```



## 1.4 @ReponseBody

响应体

在SpringBoot中，默认返回对应实体的Json形式：

```kotlin
@RequestMapping("/")
@ResponseBody
fun getAll(): List<Coffee> {
    return coffeeService.getAll()
}
```



### 1.4.1 文件上传处理

使用注解 `@ModelAttribute`来处理文件上传，与参数；

如下代码：

```kotlin
@PostMapping(value = ["/analyze"])
fun processRegister(@ModelAttribute vo: VO) {...}

// 对应 VO 类
data class ApkAnalyzeVO(
        // 文件部分
        var file1: MultipartFile? = null,
        var file2: MultipartFile? = null,

        // 参数部分
        var name: String? = null
)
```







## 1.5 @ResponseStatus

响应状态码

如：@ResponseStatus(HttpStatus.CREATED) 表示201



## 1.6 请求参数定义注解

### 1.6.1 @PathVariable

url路径上的变量，restful形式；

如 `xxx.com/collect/23/20`，对应代码如下：

```kotlin
 @RequestMapping(value = ["/collect/{key1}/{key2}", "/collect/{key1}/"])
 fun getApkDetails(@PathVariable(name = "key1") infoId: Int,
                   @PathVariable(name = "key2", required = false) key2: Int?): List<T> {
        // 注意：key2 设置为 Int?
        return service.doWork(key1, key2 ?: 20)
}
```

传入url为：

```
xxx.com/collect/23/20
```

其中 23 为 key1，20为key2，key2可不传，使用系统默认值；



> **特别注意**：
>
> @PathVariable 可以有默认值，如何使用默认值，请参考上面的`key2`，使用步骤：
>
> 1. 配置多个路径；
> 2. 设置对应对应的key，如： `@PathVariable(name = "key2", required = false) key2: Int?`
> 3. 默认值在使用的时候设置 ，如：`key2 ?: 20`，没有key2，则使用默认值 20；



### 1.6.2 @RequestParam

请求参数，key value 形式

请求url，如：`xxx.com/spider?userId=20&conId=109`，对应代码如下：

```kotlin
@RequestMapping("/spider")
fun spider(@RequestParam("userId") userId: String, @RequestParam("conId") conId: String): ResponseEntity<String> {...}
```



### 1.6.3 @RequestHeader

方便获取请求头(`Request Headers`)某个key的参数值；

比如，想要获取请求头的的 `Accept`的值，代码如下：

```kotlin
@RequestMapping(path = ["/"], name = "getAll", method = [RequestMethod.GET])
@ResponseBody
fun getAll(@RequestHeader("Accept") accept: String): List<Coffee> {
    // accept // text/html,application/xhtml+xml,application/xml;
}
```





# 2. Spring 应用上下文

Spring Application Context

上下文常用接口与实现：

1. ApplicationContext
   - ClassPathXmlApplicationContext
   - FileSystemXmlApplicationContext
   - FileSystemXmlApplicationContext
   - AnnotationConfigApplicationContext
2. WebApplicationContext

# 3. 请求与响应

## 3.1 DispatcherServlet 请求处理流程

[文档](!https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-servlet)



> 在SpringMVC中所有请求响应都会经过核心Servlet `DispatcherServlet`

1. 绑定一些Attribute

   WebApplicationContext/LocaleResolver/ThemeResolver

   > 实际上是调用 Servlet 中 `request.setAttribute(key, value) `方法来进行设置；

2. 处理Multipart

   如有，比如上传文件，转为 `MultipartHttpServletRequest`

3. HandlerMapper 处理

   找到对应的Handler，执行Controller及前后置处理器逻辑；

4. 处理返回`model`,呈现视图

## 3.2  定义请求处理方法

比如：以下代码使用ResponseEntity来响应

```kotlin
@RequestMapping("/spider")
fun spider(@RequestParam("userId") userId: String,
           @RequestParam("containerId") con: String): ResponseEntity<String> {
    val fetchCount = weiboService.spider(userId, containerId)
    // 返回 ResponseEntity
    return ResponseEntity("""{"count":$fetchCount}""", HttpStatus.OK)
}
```

**方法详细官方文档：**

https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-ann-methods



> 比如：@RequestMapping 处理的方法，可接受 `HttpServletRequest`,`HttpServletResponse` 等，
>
> 具体请看[文档](https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-ann-methods)；

### 3.2.1 处理方法示例

`produces`，指定返回JSON结果：

```kotlin
@RequestMapping(path = ["/"], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@ResponseBody
fun getAll(@RequestHeader("Accept") accept: String): List<Coffee> {
    return coffeeService.getAll()
}
```



指定接收与返回：

要求json体，返回json体：

```kotlin
@PostMapping("/",
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@ResponseStatus(HttpStatus.CREATED)
fun create(@RequestBody newOrder: NewOrderRequest): CoffeeOrder {
    log.info("Receive new Order {}", newOrder)
    val coffeeList = coffeeService.getCoffeeByName(*newOrder.items?.toTypedArray()!!)
    return orderService.createOrder(newOrder.customer ?: "", *coffeeList.toTypedArray())
}
```

### 3.2.2 定义类型转换

自定义实现 Spring 的 `WebMvcConfigurer`

在SpringBoot `WebMvcAutoConfiguration` 会自动配置

添加自定义的`Converter`

添加自定义的`Formatter`



**从文本到Money的类型Formatter：**

```kotlin
@Component  // spring 自动注入
class MoneyFormatter : Formatter<Money> {
    /** 仅测试用 CNY 10.00 / 10.00 */
    @Throws(ParseException::class)
    override fun parse(text: String, locale: Locale): Money {
        if (NumberUtils.isParsable(text)) {  // 纯数字
            return Money.of(CurrencyUnit.of("CNY"), NumberUtils.createBigDecimal(text))
        } else if (StringUtils.isNotEmpty(text)) {
            val split = StringUtils.split(text, " ")
            return if (split != null &&
                       split!!.size == 2 && NumberUtils.isParsable(split!![1])) {
                Money.of(CurrencyUnit.of(split!![0]),
                        NumberUtils.createBigDecimal(split!![1]))
            } else {
                throw ParseException(text, 0)
            }
        }
        throw ParseException(text, 0)
    }
    override fun print(money: Money?, locale: Locale): String? {
        return if (money == null) {
            null
        } else money.currencyUnit.code + " " + money.amount
    }
}
```



### 3.2.3 定义校验

通过`Validator`对绑定结果校验

- HIbernate Validator
- 在绑定对应上使用@Valid注解
- 结果返回给BindingResult



**示例代码:**

```kotlin
// Controller 层，接收表单类型 MediaType.APPLICATION_FORM_URLENCODED_VALUE
// 如果表单验证出错，则 SpringBoot 会跑出异常到前段
@PostMapping(path = ["/"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
fun addCoffee(@Valid newCoffeeRequest: NewCoffeeRequest): Coffee? {
    return coffeeService.saveCoffee(newCoffeeRequest.name, newCoffeeRequest.price)
}

// 如果想要自己处理表单异常，则通过BindResult自己处理，即判断result.hasErrors()是否有错误
@PostMapping(path = ["/"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
fun addCoffee(@Valid newCoffeeRequest: NewCoffeeRequest, result: BindingResult): Coffee? {
    if (result.hasErrors()) {
        return null  // 返回空
    }
    return coffeeService.saveCoffee(newCoffeeRequest.name, newCoffeeRequest.price)
}
```

接收表单参数对象：

```kotlin
// 表单对象：NewCoffeeRequest，用来接收参数，这里会自动走类型转换 MoneyFormatter
data class NewCoffeeRequest(
    @NotEmpty
    var name: String,
    @NotNull
    var price: Money  // 参数 price 值，自动转换成 Money 对象，因为配置了 MoneyFormatter
)
```





### 3.2.4 文件上传

SpringBoot 自动配置 `MultipartAutoConfiguration`

类型使用`MultipartFile`类型

表单类型为`multipart/formdata`

配置使用 `MultipartProperties`

配置如：

```properties
spring.servlet.multipart.maxFileSize=
spring.servlet.multipart.location=
```

示例代码：

```kotlin
// Controller 层 - 上传单个文件
@PostMapping("/", produces = [MediaType.MULTIPART_FORM_DATA_VALUE])
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
fun batchAddCoffee(@RequestParam("file") file: MultipartFile): List<Coffee> {
    val coffees = mutableListOf<Coffee>()
    if (file != null) {
        BufferedReader(InputStreamReader(file.inputStream)).apply {
            this.readLines().forEach { line ->
               line.split(" ").let {
                   if (it.size == 2) {
                      coffees.add(coffeeService.saveCoffee(it[0],
                                  Money.of(CurrencyUnit.of("CNY"),
                                  NumberUtils.createBigDecimal(it[1]))))
                    }
                }
           }
           this.close()
        }
    }
    return coffees
}
```

# 4. Spring MVC 视图解析

ViewResolver 与 View 接口

- AbstractCachingViewResolver
- FreeMarkerViewResolver
- ContentNegotiationViewResolver
- InternalResourceViewResolver

ViewResolver 用来解析并返回View对象，并返回View对象来呈现；

## 4.1 DispatcherServlet 中的视图解析逻辑

### 4.1.1 ModelAndView的解析逻辑

1. initStrategies()

   - initViewResolver()  初始化对应的ViewResolver，加载所有的ViewResolver;

2. doDispatch()

   - processDispatchResult()

   - resolverViewName()  解析View对象

     视图名到具体视图的解析，比如：home 对应 home.html;

### 4.1.2 使用@ResponseBody

1. 在HandlerAdapter.handle() 中完成了`Response`输出
   - RequestMappingHandlerAdapter.invokeHandlerMethod()
   - HandlerMethodReturnValueHandlerComposite.handleReturnValue()
   - RequestResponseBodyMethodProcessor.handleReturnValue()

### 4.1.3 重定向

- `redirect:`重定向 ，重定向后，会丢失上一个请求的信息；
- `forward:`转发

## 4.2 Spring MVC 常用的视图

[文档](https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-view)

常用的为：

1. json （前后的分离的形式）
2. Thymeleaf & FreeMarker (模板引擎)

### 4.2.1 配置MessageConverter

类似于前面的Controller HandleMethod，在视图层也有类型到具体内容的转换；



通过`WebMvcConfigurer`的`configureMessageConverters()` 方法

在SpringBoot 自动查找 `HttpMessageConverters`进行注册；

> 也就是SpringBoot 会自动帮助我们做好一系列配置，当然也可以自己配置；

### 4.2.2 SpringBoot 对 Jackson的支持

- JacksonAutoConfiguration

  SpringBoot 通过`@JsonComponent`注册JSON序列化组件，自动注入JSON对应的类中；

- JacksonHttpMessageConvertersConfiguration

  添加到`jackson-dataformat-xml`以及支持xml序列化；



**JSON序列化与反序列化器：**

```kotlin
@JsonComponent
class MoneySerializer protected constructor()
    : StdSerializer<Money>(Money::class.java) {

    @Throws(IOException::class)
    override fun serialize(money: Money, jsonGenerator: JsonGenerator,
                           serializerProvider: SerializerProvider) {
        jsonGenerator.writeNumber(money.amount)
    }
}

@JsonComponent
class MoneyDeserializer protected constructor()
	: StdDeserializer<Money>(Money::class.java) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Money {
        return Money.of(CurrencyUnit.of("CNY"), p.decimalValue)
    }
}
```



**Controller新增方法**

Controller类配置为：`@Controller`

```kotlin
@PostMapping(path = ["/"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@ResponseBody
@ResponseStatus(HttpStatus.CREATED)
fun addJsonCoffeeWithoutBindingResult(@Valid @RequestBody
                                      newCoffeeRequest: NewCoffeeRequest): Coffee? {
    return coffeeService.saveCoffee(newCoffeeRequest.name, newCoffeeRequest.price!!)
}

// 此方法没有配置 produces
@GetMapping(path = ["/{name}"])
@ResponseBody
fun getByName(@PathVariable("name") name: String): List<Coffee>? {
    return coffeeService.getCoffeeByName(name)
}
```

> 因为配置 Money 类型的json序列化与反序列化机制，所以 price简洁了；
>
> ```json
> {
>  "name": "espresso",
>  "price": 20,
>  "id": 6,
>  "createTime": 1552310987147,
>  "updateTime": 1552310987147
> }
> ```

### 4.2.3 输出 JSON 转 xml 配置

1. 新增依赖

```groovy
// jackson xml 依赖
implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.0'
```

2. 请求接口时，附带 headers 为 `Accept = application/xml`,此时将返回xml格式数据：

```xml
<item>
    <name>latte</name>
    <price>25.00</price>
    <id>7</id>
    <createTime>1552310987191</createTime>
    <updateTime>1552310987191</updateTime>
</item>
```

## 4.3 Thymeleaf

模板引擎，可用来替代JSP，用作页面展示；其配置相关简单：

1. 引入 Thymeleaf 依赖；
2. Springboot 会自动配置，对应的是 `ThymeleafAutoConfiguraion`

### 4.3.1 Thymeleaf 常用默认配置

```properties
# 模板缓存，开发阶段使用false
spring.thymeleaf.cache=true
# 校验模板
spring.thymeleaf.check-template=true
# 检查模板位置
spring.thymeleaf.check-template-location=true
spring.thymeleaf.enabled=true
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.mode=HTML
spring.thymeleaf.servlet.content-type=text/html
# 前缀路径配置
spring.thymeleaf.prefix=classpath:/templates/
# 后缀
spring.thymeleaf.suffix=.html
```

示例 略；



# 5. 静态资源与缓存

不建议在Java中做，而采用`Nginx` 做静态资源服务；

## 5.1 SpringBoot 中的静态资源配置

核心逻辑：

- WebMvcConfigurer.addResourceHandlers()

常用配置：

```properties
# 默认从根路径开始匹配
spring.mvc.static-path-pattern=/**
# 会从以下目录开始寻找
spring.resources.static-locations=classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/
```



## 5.2  缓存配置

常用配置(默认单位是秒)

通过类 `ResourceProperties.Cache` 进行设置

```properties
spring.resources.cache.cachecontrol.max-age=
spring.resources.cache.cachecontrol.no-cache=true/false
spring.resources.cache.cachecontrol.s-max-age=
```

> 一般使用`spring`拦截器进行缓存设置



# 6. Spring MVC 异常解析

核心接口：

- HandlerExceptionResolver

常用实现类：

- SimpleMappingExceptionResolver

- DefaultHandlerExceptionResolver

- ResponseStatusExceptionResolver

  ```kotlin
  @ExceptionHandler(MethodArgumentNotValidException::class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 指定响应码
  @ResponseBody
  fun addCoffee2()
  ```

- ExceptionHandlerExceptionResolver

> 核心异常处理逻辑在 `DispatcherServlet`中；



## 6.1 异常处理方法

处理方法：

- @ExceptionHander

  在方法上标明此注解，指定该方法用来处理异常，如下代码：

  ```kotlin
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(
    InvalidParameterException::class,
    MissingServletRequestParameterException::class,
    HttpMessageNotReadableException::class)
  @ResponseBody
  fun handleArgumentException(request: HttpServletRequest, exception: RuntimeException,
                                  locale: Locale): ResponseModel<*> {
          return ResponseModel.fail(
            MessageCodeEnum.CODE_BAD_REQUEST.code, exception.message ?: "")
  }
  ```



添加位置：

- @Controller / @RestController

  在Controller类上方法添加

- @ControllerAdvice / @RestControllerAdvice

  在类似AOP形式拦截器上的类添加，会对所有`Controller`类进行拦截；

# 7. Spring MVC 切入点 - 拦截器

核心接口拦截器：

- HandlerInteceptor

  - boolean preHandle()

    预处理，false 终止，true继续；比如：权限验证；

  - void postHandle()

  - void afterHandler()

针对@ResponseBody 和 ResponseEntity 的情况

- ResponseBodyAdvice

针对异步请求的接口：

- AsyncHandlerInterceptor

> 入口 `DispatcherServlet` 调用



## 7.1 配置拦截器-Controller 性能日志

常规方法：

- WebMvcConfigurer.addInterceptors()

Spring Boot 中的配置：

- 创建带有`@Configuration`注解的`WebMvcConfigurer`的配置类
- 不能带`@EnableWebMvc`注解（想自己彻底控制MVC配置除外）；

我们在Application类实现 `WebMvcConfigurer`接口，并添加拦截：

```kotlin
@SpringBootApplication
class StudyApplication : WebMvcConfigurer {
  override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(PerformanceInteceptor())
                // 拦截指定url下的调用
               .addPathPatterns("/coffee/**").addPathPatterns("/order/**")
    }
}
```

PerformanceInteceptor 类代码：

```kotlin
class PerformanceInteceptor : HandlerInterceptor {

    private val stopWatch = ThreadLocal<StopWatch>()
    private val log = LoggerFactory.getLogger(PerformanceInteceptor::class.java)

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest?,
                           response: HttpServletResponse?, handler: Any?): Boolean {
        val sw = StopWatch()
        stopWatch.set(sw)
        sw.start()
        return true
    }

    @Throws(Exception::class)
    override fun postHandle(request: HttpServletRequest?,
                            response: HttpServletResponse?, handler: Any?,
                            modelAndView: ModelAndView?) {
        stopWatch.get().stop()  // 记录时间点
        stopWatch.get().start() // 后续为呈现的耗时
    }

    @Throws(Exception::class)
    override fun afterCompletion(request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 handler: Any, ex: Exception?) {
        val sw = stopWatch.get()
        sw.stop()
        var method = handler.javaClass.simpleName
        if (handler is HandlerMethod) {
            val beanType = handler.beanType.name
            val methodName = handler.method.name
            method = "$beanType.$methodName"
        }
        log.info("{};{};{};{};{}ms;{}ms;{}ms", request.requestURI, method,
                response.status, if (ex == null) "-" else ex.javaClass.simpleName,
                sw.totalTimeMillis, sw.totalTimeMillis - sw.lastTaskTimeMillis,
                sw.lastTaskTimeMillis)
        stopWatch.remove()
    }
}
```

# 8. 访问Web资源

Spring 用来做后端服务，如果服务本身需要获取其他服务的资源呢，比如：下载其他服务器上的文件；

此时，我们就需要用到`RestTemplate`了；

## 8.1 RestTemplate

### 8.1.1 泛型的支持

通过 `ParameterizedTypeReference<T>

### 8.1.2 RestTemplate 支持的Http 库

通用接口：

- ClientHttpRequestFactory

默认实现：

- SimpleClientHttpRequestFactory

其他实现：

- OkHttp3ClientHttpRequestRequestFactory

### 8.1.3 优化底层请求策略

https://time.geekbang.org/course/detail/156-87036

比如：当SpringBoot请求其他服务器资源时，对应的服务器异常了，我们要怎么处理，我们不能不限制的等待其他服务响应；

典型的优化有：

- 链接管理
  - PoolingHttpClientConnectionManager
  - KeepAlive 策略
- 超时设置
  - connectTimout / readTimeout
- SLL校验
  - 证书检查策略

## 8.2 WebClient

一个以`Reactive`方式处理HTTP请求的非阻塞式的客户端；

支持底层HTTP库：

- Reactor Netty