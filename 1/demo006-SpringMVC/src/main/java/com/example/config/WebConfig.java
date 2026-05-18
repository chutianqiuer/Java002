package com.example.config;

import com.example.interceptor.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

/**
 * Web MVC配置类
 *
 * 本类继承 WebMvcConfigurer 接口，用于定制Spring MVC的配置。
 * WebMvcConfigurer 提供了很多配置方法，可以覆盖默认配置或添加自定义配置。
 *
 * 传统XML配置对比：
 * - <mvc:annotation-driven/>        → @EnableWebMvc
 * - <mvc:resources mapping=""/>      → addResourceHandlers()
 * - <mvc:default-servlet-handler/>  → configureDefaultServletHandling()
 * - <bean class="...ViewResolver"/>  → viewResolver()
 *
 * 本类重点配置内容：
 * 1. 视图解析器 - 配置JSP视图解析
 * 2. 静态资源处理 - 允许访问静态资源
 * 3. 拦截器配置 - 注册自定义拦截器
 * 4. 消息转换器 - 配置JSON序列化
 */
@Configuration
/**
 * 继承 WebMvcConfigurer 接口
 * WebMvcConfigurer 是Spring MVC配置的核心接口，
 * 通过覆写其中的方法，可以自定义Spring MVC的各个组件。
 */
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * 配置视图解析器
     *
     * 视图解析器负责将逻辑视图名称（如 "success"）解析为具体的视图对象（如JSP）。
     *
     * InternalResourceViewResolver 配置：
     * - prefix: 视图文件的前缀路径
     * - suffix: 视图文件的后缀名
     * - viewClass: 视图类，这里使用 JstlView 支持JSTL标签
     *
     * 例如：逻辑视图名为 "success"
     * 实际路径为：/WEB-INF/views/success.jsp
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // 创建内部资源视图解析器
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        // 设置视图前缀
        resolver.setPrefix("/WEB-INF/views/");
        // 设置视图后缀
        resolver.setSuffix(".jsp");
        // 指定视图类，支持JSTL
        resolver.setViewClass(JstlView.class);
        // 注册到视图注册表
        registry.viewResolver(resolver);
    }

    /**
     * 配置静态资源处理
     *
     * 默认情况下，Spring MVC只处理URL到控制器的映射，
     * 不会处理静态资源（如CSS、JS、图片等）。
     * 通过配置静态资源处理器，可以让Spring MVC也能处理静态资源请求。
     *
     * 传统XML配置：
     * <mvc:resources mapping="/static/**" location="/static/"/>
     * <mvc:default-servlet-handler/>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源处理器
        // mapping: URL路径模式
        // locations: 实际文件位置
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");

        // 添加webjars资源处理（如果使用webjars）
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("/webjars/");
    }

    /**
     * 配置默认的Servlet处理
     *
     * 当Spring MVC找不到URL对应的控制器时，
     * 会将请求转发给默认的Servlet（如Tomcat的DefaultServlet）处理。
     * 这样可以确保静态资源被正确处理。
     *
     * 传统XML配置：<mvc:default-servlet-handler/>
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 启用默认Servlet处理
        configurer.enable();
    }

    /**
     * 注册自定义拦截器
     *
     * 拦截器（Interceptor）可以在请求到达控制器之前和响应返回之后进行拦截处理。
     * 类似于Servlet Filter，但更轻量且与Spring MVC集成更紧密。
     *
     * 拦截器工作流程：
     * 1. preHandle - 控制器执行前调用
     * 2. Controller执行
     * 3. postHandle - 控制器执行后，视图渲染前调用
     * 4. afterCompletion - 整个请求完成后调用
     *
     * 传统XML配置：
     * <mvc:interceptors>
     *     <mvc:interceptor>
     *         <mvc:mapping path="/**"/>
     *         <bean class="com.example.interceptor.MyInterceptor"/>
     *     </mvc:interceptor>
     * </mvc:interceptors>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 创建拦截器实例
        MyInterceptor interceptor = new MyInterceptor();

        // 注册拦截器，指定拦截的URL模式
        InterceptorRegistration registration = registry.addInterceptor(interceptor);
        // 拦截所有URL
        registration.addPathPatterns("/**");
        // 排除不需要拦截的URL
        registration.excludePathPatterns("/static/**", "/webjars/**", "/error");
    }

    /**
     * 配置消息转换器
     *
     * 消息转换器用于将Java对象与特定格式（如JSON、XML）进行转换。
     * 当控制器方法标注 @ResponseBody 时，返回值会被消息转换器处理。
     * 当控制器方法参数标注 @RequestBody 时，请求体会被消息转换器处理。
     *
     * MappingJackson2HttpMessageConverter：
     * - 使用Jackson库将Java对象转换为JSON
     * - 支持注解 @JsonIgnore、@JsonFormat 等
     * - 支持日期格式化等
     *
     * 传统XML配置：
     * <mvc:annotation-driven>
     *     <mvc:message-converters>
     *         <bean class="...MappingJackson2HttpMessageConverter"/>
     *     </mvc:message-converters>
     * </mvc:annotation-driven>
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建Jackson消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 配置转换器（可以使用ObjectMapper配置日期格式等）
        // converter.setObjectMapper(new ObjectMapper());
        converters.add(converter);
    }

    /**
     * 配置CORS（跨域资源共享）
     *
     * CORS是一种W3C规范，允许网页从不同域获取资源。
     * 当前端页面与后端API不在同一域时，需要配置CORS。
     *
     * 例如：
     * - 前端：localhost:8080
     * - 后端：localhost:9090
     * - 需要配置CORS才能跨域访问
     *
     * 传统XML配置：
     * <mvc:cors>
     *     <mvc:mapping path="/api/**"
     *                  allowed-origins="*"
     *                  allowed-methods="GET,POST"/>
     * </mvc:cors>
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
