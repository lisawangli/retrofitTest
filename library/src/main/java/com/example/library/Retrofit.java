package com.example.library;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Retrofit {
    //缓存请求方法，key host.get value该方法的属性封装 方法名，参数注解
    private final Map<Method,ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    private HttpUrl baseUrl;
    private okhttp3.Call.Factory callFactory;

    private Retrofit(Builder builder) {
         this.baseUrl = builder.baseUrl;
         this.callFactory = builder.callFactory;
    }

    //对外提供api
    public okhttp3.Call.Factory callFactory(){
        return callFactory;
    }

    public HttpUrl baseUrl(){
        return baseUrl;
    }

    public <T> T create(final Class<T> service){
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServiceMethod serviceMethod = loadServiceMethod(method);
                System.out.println(method.getName()+"========="+Arrays.toString(args));
                return new OkHttpCall(serviceMethod,args);
            }

        });
    }
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result!=null)
            return result;
        synchronized (serviceMethodCache){
            result = serviceMethodCache.get(method);
            if (result==null){
                result = new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method,result);
            }
        }
        return result;
    }

    public static final class Builder{
        private okhttp3.Call.Factory callFactory;
        private HttpUrl baseUrl;

        public Builder callFactory(okhttp3.Call.Factory callFactory){
            this.callFactory = callFactory;
            return this;
        }
        public Builder baseUrl(String baseUrl){
            if (baseUrl.isEmpty()){
                throw new NullPointerException("baseUrl is empty");
            }
            this.baseUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        /**
         * 属性的赋值，校验和初始化的工作
         * @return
         */
        public Retrofit build(){
            if (baseUrl==null)
                throw new NullPointerException("base url required");
            if (callFactory==null){
                callFactory = new OkHttpClient();
            }
            return new Retrofit(this);
        }
    }
}
