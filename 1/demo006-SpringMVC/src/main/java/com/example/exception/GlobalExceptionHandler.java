package com.example.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @ControllerAdvice 是Spring MVC 4.0引入的注解，
 * 用于定义全局异常处理方法，集中处理控制器中抛出的异常。
 *
 * 作用：
 * 1. 统一异常处理 - 将分散在各个Controller中的异常处理逻辑集中
 * 2. 减少重复代码 - 不需要在每个Controller中单独处理异常
 * 3. 更好的分离 - 业务逻辑和异常处理分离
 *
 * @ExceptionHandler 注解：
 * - 用于标注异常处理方法
 * - 可以指定处理的异常类型
 * - 支持优先级：子类异常优先于父类异常
 *
 * 工作原理：
 * 1. 当Controller抛出异常时，Spring MVC会查找 @ExceptionHandler 方法
 * 2. 按照异常类型匹配（从子类到父类）
 * 3. 找到匹配的处理方法后，执行该方法处理异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有类型的异常
     *
     * @param request HTTP请求对象
     * @param ex      异常对象
     * @return ModelAndView 包含错误信息的视图
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {
        // 创建ModelAndView
        ModelAndView mav = new ModelAndView();

        // 设置错误视图
        mav.setViewName("error");

        // 添加错误信息到模型
        mav.addObject("exception", ex.getClass().getName());
        mav.addObject("message", ex.getMessage());
        mav.addObject("requestURI", request.getRequestURI());

        // 打印异常日志
        System.out.println("========== 全局异常处理器 ==========");
        System.out.println("异常类型: " + ex.getClass().getName());
        System.out.println("异常信息: " + ex.getMessage());
        System.out.println("请求URI: " + request.getRequestURI());
        ex.printStackTrace();
        System.out.println("====================================");

        return mav;
    }

    /**
     * 处理运行时异常（RuntimeException及其子类）
     *
     * @param request HTTP请求对象
     * @param ex      运行时异常对象
     * @return ModelAndView 包含错误信息的视图
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("exception", "RuntimeException");
        mav.addObject("message", ex.getMessage());
        mav.addObject("requestURI", request.getRequestURI());

        System.out.println("========== RuntimeException ==========");
        System.out.println("异常信息: " + ex.getMessage());
        ex.printStackTrace();
        System.out.println("=======================================");

        return mav;
    }

    /**
     * 处理NullPointerException
     *
     * 专门处理空指针异常，提供更友好的错误信息
     *
     * @param request HTTP请求对象
     * @param ex      空指针异常对象
     * @return ModelAndView 包含错误信息的视图
     */
    @ExceptionHandler(NullPointerException.class)
    public ModelAndView handleNullPointerException(HttpServletRequest request, NullPointerException ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("exception", "NullPointerException");
        mav.addObject("message", "空指针错误：" + ex.getMessage());
        mav.addObject("requestURI", request.getRequestURI());

        return mav;
    }

    /**
     * 处理IllegalArgumentException
     *
     * 当参数验证失败时抛出此异常
     *
     * @param request HTTP请求对象
     * @param ex      非法参数异常对象
     * @return ModelAndView 包含错误信息的视图
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("exception", "IllegalArgumentException");
        mav.addObject("message", "参数错误：" + ex.getMessage());
        mav.addObject("requestURI", request.getRequestURI());

        return mav;
    }
}
