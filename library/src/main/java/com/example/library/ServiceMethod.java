package com.example.library;

import android.support.v4.app.NavUtils;

import com.example.library.http.Field;
import com.example.library.http.GET;
import com.example.library.http.POST;
import com.example.library.http.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

class ServiceMethod {

    private final boolean hasBody;
    private final ParameterHandler[] parameterHandlers;
    private final String relativeUrl;
    private String httpMethod;
    private HttpUrl baseUrl;
    private okhttp3.Call.Factory callFactory;
    private ServiceMethod(Builder builder){
        this.callFactory = builder.retrofit.callFactory();
        this.baseUrl = builder.retrofit.baseUrl();
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        //方法参数的数组，每个对象包含：参数注解值，参数值
        this.parameterHandlers = builder.parameterHandlers;
        this.hasBody = builder.hasBody;
    }
    public Call toCall(Object[] args) {
        //请求的拼装
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod,baseUrl,relativeUrl,hasBody);
        ParameterHandler[] handlers = parameterHandlers;
        int argumentCount = args!=null?args.length:0;
        if (argumentCount!=handlers.length){
            throw new IllegalArgumentException("argument count("+argumentCount+") doesn't match expected count ("+handlers.length+")");
        }
        //循环拼接每个参数名和参数值
        for (int i = 0; i < argumentCount ; i++) {
            handlers[i].apply(requestBuilder,args[i].toString());
        }
        return callFactory.newCall(requestBuilder.build());
    }

    static final class Builder{
        //okhttpclient封装类
        final Retrofit retrofit;
        //带rest注解的方法
        final Method method;
        //方法的所有注解
        final Annotation[] methodAnnotations;
        //方法参数所有注解（一个方法有多个参数，一个参数有多个注解）
        final Annotation[][] parameterAnnotationsArray;
        String httpMethod;
        //是否有请求体
        boolean hasBody;
        //方法注解的值
        String relativeUrl;
        //方法参数的数组（每个数组内：参数注解值，参数值的）
         ParameterHandler[] parameterHandlers;
        Builder(Retrofit retrofit,Method method){
            this.retrofit = retrofit;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public ServiceMethod build(){
            for (Annotation annotation:methodAnnotations){
                parseMethodAnnotation(annotation);
            }
            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler[parameterCount];
            //遍历方法的参数
            for (int i = 0; i < parameterCount ; i++) {
                //获取每个参数的所有注解
                Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
                if (parameterAnnotations==null){
                    throw new IllegalArgumentException("no retrofit annotation found");
                }
                //获取参数的注解值，参数值
                parameterHandlers[i] = parseParameter(i,parameterAnnotations);
            }
            return new ServiceMethod(this);
        }

        //解析参数的所有注解（嵌套循环）
        private ParameterHandler parseParameter(int i,Annotation[] annotations){
            ParameterHandler result = null;

            for (Annotation annotation:annotations){
                ParameterHandler annotationAction = parseParameterAnnotation(annotation);
                if (annotationAction==null)
                    continue;
                result = annotationAction;
            }
            if (result==null)
                throw new IllegalArgumentException("no retrofit annotation found");
            return result;
        }

        private ParameterHandler parseParameterAnnotation( Annotation annotation) {
            if (annotation instanceof Query){
                Query query = (Query) annotation;
                String name = query.value();
                return new ParameterHandler.Query(name);
            }else if(annotation instanceof Field){
                Field field = (Field) annotation;
                String name = field.value();
                return new ParameterHandler.Field(name);
            }
            return null;
        }

        //解析方法的注解，可能时get或者是post
        private void parseMethodAnnotation(Annotation annotation){
            if (annotation instanceof GET){
                //GET方法没有请求体
                parseHttpmethodAndPath("GET",((GET) annotation).value(),false);
            }else if (annotation instanceof POST){
                parseHttpmethodAndPath("POST",((POST) annotation).value(),true);
            }
        }

        private void parseHttpmethodAndPath(String httpMethod,String value,boolean hasBody){
            //请求方式GET或者POST
            this.httpMethod = httpMethod;
            //是否有请求体
            this.hasBody = hasBody;
            //方法注解的值，用来请求拼接
            this.relativeUrl = value;
        }
    }
}
