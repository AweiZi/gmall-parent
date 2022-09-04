package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.UUID;

@SpringBootTest
public class SpelTest {

    @Test
    void test04(){
        ExpressionParser parser = new SpelExpressionParser();

        UUID.randomUUID().toString();

        // haha-
        Expression expression = parser
                .parseExpression("haha-#{T(java.util.UUID).randomUUID().toString()}",new TemplateParserContext());

        System.out.println(expression.getValue());
    }

    void test03(){
        ExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression("new int[]{1,2,3,4}");

        int[] value = (int[]) expression.getValue();
        for (int i : value) {
            System.out.println(i);
        }
    }

    @Test
    void test02(){
        Object[] params1 = new Object[]{49L,50};
        Object[] params2 = new Object[]{55L,50};
        Object[] params3 = new Object[]{77L,50};
        //表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression("sku:info:#{#params[0]}", new TemplateParserContext());
        //1.准备一个计算上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //2.变量贺上下文环境绑定
        context.setVariable("params",params2);
        String value = expression.getValue(context, String.class);
        System.out.println("value = " + value);
    }

    @Test
    void test01(){
        //1.创建一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        //2.准备一个表达式
        String myExpression = "hello #{1+1}";
        //3.得到一个表达式
        Expression expression = parser.parseExpression(myExpression,new TemplateParserContext());
        Object value = expression.getValue();
        System.out.println("value = " + value);
    }
}
