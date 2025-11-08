package com.senjay.archat.common.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;


/**
 * @author 33813
 */
public class SpElUtils {

//   SpEL 表达式解析器。
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    /**
     * 在 Java 中，通过反射可以获取一个方法的参数类型，但默认情况下无法获取参数名（也就是 String uid 中的 uid）。
     * 这时候就需要 “发现器” 来“发现”这些参数名 ——
     * Spring 提供的 ParameterNameDiscoverer 接口，就是专门干这个事情的。
     * 而 DefaultParameterNameDiscoverer 是它的一个实现类，用来从方法签名中智能提取参数名。
     */
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     *
     * @param method
     * @param args
     * @param spEl
     * @return
     */
    public static String parseSpEl(Method method, Object[] args, String spEl) {
        // 解析参数名 (注意是名字 不是类型 反射拿不到参数名字的)  Optional 的使用！！！
        String[] params = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(new String[]{});
        /**
         * 这里创建了一个 SpEL 表达式计算时的上下文对象。
         * EvaluationContext 是 SpEL 提供的接口，里面可以放变量（比如方法参数），表达式在执行时可以从这里取值。
         * StandardEvaluationContext 是其默认实现，支持设置变量、方法解析等。
         * 这一步是为表达式的计算准备环境。
         */
        EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < params.length; i++) {
            // 所有参数都作为原材料扔进去
            context.setVariable(params[i], args[i]);
        }
        Expression expression = PARSER.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }
//  为某个方法生成一个全局唯一的标识符
    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
