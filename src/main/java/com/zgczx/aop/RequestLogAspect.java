package com.zgczx.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * 编写了一个aop简单使用
 *   管切入到指定类指定方法的代码片段称为切面，而切入到哪些类、哪些方法则叫切入点。
 *   如果几个或更多个逻辑过程中，有重复的操作行为，AOP就可以将其提取出来，运用代理机制，
 * 实现程序功能的统一维护，这么说来可能太含蓄，如果说到权限判断，日志记录等，可能就明白了。
 *
 * 使用@Aspect注解将一个java类定义为切面类
 * 使用@Pointcut定义一个切入点，可以是一个规则表达式，比如下例中某个package下的所有函数，也可以是一个注解等。
 * 根据需要在切入点不同位置的切入内容
 * 使用@Before在切入点开始处切入内容
 * 使用@After在切入点结尾处切入内容
 * 使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
 * 使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
 * 使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
 * @author aml
 * @date 2019/10/8 21:42
 */
@Aspect
@Component
public class RequestLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogAspect.class);

    @Pointcut("execution(public * com.zgczx.controller..*.*(..)) && !execution(public * com.zgczx.controller.exam.ExamController.findExamQuestionInfo(..) ) ")
    public void webLog(){

    }
    @Before(("webLog()"))
    public void doBefor(JoinPoint joinPoint){
        //接收到请求， 记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求的内容
        logger.info("URL: " + request.getRequestURI().toString());
        logger.info("HTTP method: "+ request.getMethod());
        logger.info("IP:" + request.getRemoteAddr());
        logger.info("CLASS_method: "+ joinPoint.getSignature().getDeclaringTypeName() + "."
         + joinPoint.getSignature().getName());
        // 获取参数名
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        logger.info("parameterName： "+ Arrays.toString(methodSignature.getParameterNames()));
        logger.info("parameterValue： "+ Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret){
        // 处理完请求， 返回内容
        logger.info("RESPONSE: "+ ret);

    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pip) throws Throwable{
        long startTime = System.currentTimeMillis();
        Object object = pip.proceed();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("currentTime: " + dateFormat.format(startTime));
        logger.info("Time ： "+ (System.currentTimeMillis() - startTime));
        return object;
    }
}
