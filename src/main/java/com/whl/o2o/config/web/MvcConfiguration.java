package com.whl.o2o.config.web;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.whl.o2o.interceptor.shopadmin.ShopLoginInterceptor;
import com.whl.o2o.interceptor.shopadmin.ShopPermissionInterceptor;
import com.whl.o2o.interceptor.superadmin.SuperAdminLoginInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.ServletException;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Configuration
@EnableWebMvc
public class MvcConfiguration implements WebMvcConfigurer, ApplicationContextAware {
    // Spring容器
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 静态资源配置
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:/projectDev/image/upload/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/whl/image/upload/");

    }

    /**
     * 定义默认的请求处理器
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 视图解析器
     * @return
     */
    @Bean(name = "viewResolver")
    public ViewResolver createViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setApplicationContext(this.applicationContext);// 设置Spring 容器
        viewResolver.setCache(false);// 缓存
        viewResolver.setPrefix("/WEB-INF/html/");// 解析的前缀
        viewResolver.setSuffix(".html");// 设置试图解析的后缀
        return viewResolver;
    }

    /**
     * 文件上传解析器
     * @return
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver createMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        multipartResolver.setMaxUploadSize(20971520); // 1024 * 1024 * 20 = 20M
        multipartResolver.setMaxInMemorySize(20971520);
        return multipartResolver;
    }

    @Value("${kaptcha.border}")
    private String border;

    @Value("${kaptcha.textproducer.font.color}")
    private String fcolor;

    @Value("${kaptcha.image.width}")
    private String width;

    @Value("${kaptcha.textproducer.char.string}")
    private String cString;

    @Value("${kaptcha.image.height}")
    private String height;

    @Value("${kaptcha.textproducer.font.size}")
    private String fsize;

    @Value("${kaptcha.noise.color}")
    private String nColor;

    @Value("${kaptcha.textproducer.char.length}")
    private String clength;

    @Value("${kaptcha.textproducer.font.names}")
    private String fnames;

    /**
     * 由于web.xml不生效了，需要在这里配置Kaptcha验证码Servlet
     */
    @Bean
    public ServletRegistrationBean<KaptchaServlet> servletRegistrationBean() throws ServletException {
        ServletRegistrationBean<KaptchaServlet> servlet = new ServletRegistrationBean<KaptchaServlet>(new KaptchaServlet(), "/Kaptcha");
        servlet.addInitParameter("kaptcha.border", border);// 无边框
        servlet.addInitParameter("kaptcha.textproducer.font.color", fcolor); // 字体颜色
        servlet.addInitParameter("kaptcha.image.width", width);// 图片宽度
        servlet.addInitParameter("kaptcha.textproducer.char.string", cString);// 使用哪些字符生成验证码
        servlet.addInitParameter("kaptcha.image.height", height);// 图片高度
        servlet.addInitParameter("kaptcha.textproducer.font.size", fsize);// 字体大小
        servlet.addInitParameter("kaptcha.noise.color", nColor);// 干扰线的颜色
        servlet.addInitParameter("kaptcha.textproducer.char.length", clength);// 字符个数
        servlet.addInitParameter("kaptcha.textproducer.font.names", fnames);// 字体
        return servlet;
    }

    /**
     * 添加拦截器配置
     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        /** 店家管理系统拦截部分 **/
//        String interceptPath = "/shopadmin/**";
//        // 注册拦截器
//        InterceptorRegistration loginIR = registry.addInterceptor(new ShopLoginInterceptor());
//        // 配置拦截的路径
//        loginIR.addPathPatterns(interceptPath);
//        /** shopauthmanagement page **/
//        loginIR.excludePathPatterns("/shopadmin/addshopauthmap");
//        /** scan **/
//        loginIR.excludePathPatterns("/shopadmin/adduserproductmap");
//        loginIR.excludePathPatterns("/shopadmin/exchangeaward");
//        // 还可以注册其它的拦截器
//        InterceptorRegistration permissionIR = registry.addInterceptor(new ShopPermissionInterceptor());
//        // 配置拦截的路径
//        permissionIR.addPathPatterns(interceptPath);
//        // 配置不拦截的路径
//        /** shoplist page **/
//        permissionIR.excludePathPatterns("/shopadmin/shoplist");
//        permissionIR.excludePathPatterns("/shopadmin/getshoplist");
//        /** shopregister page **/
//        permissionIR.excludePathPatterns("/shopadmin/getshopinitinfo");
//        permissionIR.excludePathPatterns("/shopadmin/registershop");
//        permissionIR.excludePathPatterns("/shopadmin/shopoperation");
//        /** shopmanage page **/
//        permissionIR.excludePathPatterns("/shopadmin/shopmanagement");
//        permissionIR.excludePathPatterns("/shopadmin/getshopmanagementinfo");
//        /** shopauthmanagement page **/
//        permissionIR.excludePathPatterns("/shopadmin/addshopauthmap");
//        /** scan **/
//        permissionIR.excludePathPatterns("/shopadmin/adduserproductmap");
//        permissionIR.excludePathPatterns("/shopadmin/exchangeaward");
//
//        /** 超级管理员系统拦截部分 **/
//        interceptPath = "/superadmin/**";
//        // 注册拦截器
//        InterceptorRegistration superadminloginIR = registry.addInterceptor(new SuperAdminLoginInterceptor());
//        // 配置拦截的路径
//        superadminloginIR.addPathPatterns(interceptPath);
//        superadminloginIR.excludePathPatterns("/superadmin/login");
//        superadminloginIR.excludePathPatterns("/superadmin/logincheck");
//        superadminloginIR.excludePathPatterns("/superadmin/main");
//        superadminloginIR.excludePathPatterns("/superadmin/top");
//        superadminloginIR.excludePathPatterns("/superadmin/clearcache4area");
//        superadminloginIR.excludePathPatterns("/superadmin/clearcache4headline");
//        superadminloginIR.excludePathPatterns("/superadmin/clearcache4shopcategory");
//    }
}
