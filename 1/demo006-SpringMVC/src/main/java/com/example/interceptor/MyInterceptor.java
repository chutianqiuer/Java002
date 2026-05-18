package com.example.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *
 * 拦截器是Spring MVC的重要组成部分，它可以在请求处理的不同阶段进行拦截和处理。
 *
 * 与Filter的区别：
 * - Filter是Servlet规范的一部分，依赖于Servlet容器
 * - HandlerInterceptor是Spring MVC的组件，与Spring容器集成更紧密
 * - Filter对所有请求生效，拦截器可以更精确地控制拦截的URL
 *
 * 拦截器方法说明：
 * 1. preHandle(HttpServletRequest, HttpServletResponse, Object)
 *    - 调用时间：请求到达控制器之前
 *    - 返回值：true表示继续处理，false表示中断请求
 *    - 典型用途：登录验证、日志记录、权限检查
 *
 * 2. postHandle(HttpServletRequest, HttpServletResponse, Object, ModelAndView)
 *    - 调用时间：控制器执行之后，视图渲染之前
 *    - 典型用途：修改Model数据、添加通用数据
 *
 * 3. afterCompletion(HttpServletRequest, HttpServletResponse, Object, Exception)
 *    - 调用时间：整个请求处理完成后（包括视图渲染）
 *    - 典型用途：资源清理、日志记录、异常处理
 */
public class MyInterceptor implements HandlerInterceptor {

    /**
     * preHandle - 前置处理方法
     *
     * 该方法在请求进入控制器之前执行。
     * 可以在这里进行登录验证、权限检查、日志记录等操作。
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器（通常是Controller中的方法）
     * @return true 继续执行后续的处理器和拦截器
     *         false 中断请求，不会执行控制器方法，也不会执行后续拦截器的preHandle
     * @throws Exception 如果发生异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        // 获取请求方法
        String method = request.getMethod();

        // 获取请求参数（如果是GET请求）
        String queryString = request.getQueryString();

        // 打印日志 - 实际项目中可以使用Logger
        System.out.println("========== MyInterceptor.preHandle ==========");
        System.out.println("请求URI: " + requestURI);
        System.out.println("请求方法: " + method);
        System.out.println("请求参数: " + (queryString != null ? queryString : "无"));
        System.out.println("处理器: " + handler);
        System.out.println("==============================================");

        /**
         * 拦截器示例：简单的登录验证
         * 实际项目中应该检查Session或Token
         */
        // 如果请求的是受保护的路径，可以在这里进行验证
        // if (requestURI.startsWith("/admin") && !isLoggedIn(request)) {
        //     response.sendRedirect("/login");
        //     return false;
        // }

        // 返回true表示放行，请求会继续传递给下一个拦截器或控制器
        return true;
    }

    /**
     * postHandle - 后置处理方法
     *
     * 该方法在控制器执行之后、视图渲染之前执行。
     * 可以在此方法中修改ModelAndView中的数据，或添加通用数据。
     *
     * @param request      HTTP请求对象
     * @param response     HTTP响应对象
     * @param handler      处理器
     * @param modelAndView 控制器返回的ModelAndView对象
     *                     如果控制器返回值为void或@ResponseBody，此参数可能为null
     * @throws Exception 如果发生异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("========== MyInterceptor.postHandle ==========");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("处理器: " + handler);

        // 如果有ModelAndView，可以在这里修改数据
        if (modelAndView != null) {
            System.out.println("视图名: " + modelAndView.getViewName());
            System.out.println("模型数据: " + modelAndView.getModel());

            // 示例：添加通用数据（如当前登录用户）
            // modelAndView.addObject("currentUser", getCurrentUser());

            // 示例：修改视图名
            // modelAndView.setViewName("newViewName");
        }

        System.out.println("==============================================");
    }

    /**
     * afterCompletion - 完成处理方法
     *
     * 该方法在整个请求处理完成后执行。
     * 无论控制器是否正常执行，该方法都会执行。
     * 适合进行资源清理工作。
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器
     * @param ex       控制器执行过程中抛出的异常
     *                 如果没有异常，此参数为null
     * @throws Exception 如果发生异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("========== MyInterceptor.afterCompletion ==========");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("处理器: " + handler);

        // 如果有异常，可以在这里处理
        if (ex != null) {
            System.out.println("异常信息: " + ex.getMessage());
            // 可以记录日志或发送报警邮件
        }

        // 示例：清理ThreadLocal资源
        // RequestContextHolder.clear();

        System.out.println("====================================================");
    }
}
