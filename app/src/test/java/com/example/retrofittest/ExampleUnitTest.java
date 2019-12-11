package com.example.retrofittest;

import android.util.Log;

import com.example.library.Retrofit;
import com.example.library.http.GET;
import com.example.library.http.Query;

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    interface Host{
        @GET("/salesman/banner/selectMediaBanner")
        Call get(@Query("shopId") String shopId);
    }

    @Test
    public void addition_isCorrect() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://gateway.test.cef0e73c879624990a12fcf7c3cd3ea9d.cn-shanghai.alicontainer.com/").build();
        Host host = retrofit.create(Host.class);
        Call call = host.get("101093");
        try {
            Response response = call.execute();
            if (response!=null&&response.body()!=null){
                System.out.print("===>"+response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Host host = (Host) Proxy.newProxyInstance(Host.class.getClassLoader(), new Class[]{Host.class}, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println("获取方法名称>>>"+method.getName());
//                GET get = method.getAnnotation(GET.class);
//                System.out.println("获取方法的注解值>>>"+get.value());
//                Annotation[][] parameter = method.getParameterAnnotations();
//                for (Annotation[] annotation:parameter){
//                    System.out.println("获取方法的参数注解>>>"+ Arrays.toString(annotation));
//                }
//                System.out.println("获取方法的值>>>"+Arrays.toString(args));
//                return null;
//            }
//        });
//        host.get("11111");
    }
}