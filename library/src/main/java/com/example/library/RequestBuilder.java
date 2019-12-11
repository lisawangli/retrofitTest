package com.example.library;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {

    //方法的请求方式（"GET", "POST")
    private final String method;
    //接口请求地址 http://www.163.com
    private final HttpUrl baseUrl;
    //方法的注解值
    private String relativeUrl;
    //请求url构建者
    private HttpUrl.Builder urlBuilder;
    //form表单构建者
    private FormBody.Builder formBuilder;
    //构建完整请求
    private final Request.Builder requestBuilder;

    public RequestBuilder(String method,HttpUrl baseUrl,String relativeUrl,boolean hasBody){
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        requestBuilder = new Request.Builder();
        if (hasBody)
            formBuilder = new FormBody.Builder();
    }

    /**
     *
     * @param name 注解的值
     * @param value 参数的值
     */
    public void addQueryParam(String name,String value){
        if (relativeUrl!=null){
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            if (urlBuilder==null){
                throw new IllegalArgumentException(baseUrl+"========"+relativeUrl);
            }
            relativeUrl = null;
        }

        urlBuilder.addQueryParameter(name,value);
    }

    public void addFormField(String name,String value){
        formBuilder.add(name,value);
    }

    public Request build() {
        HttpUrl url;
        if (urlBuilder!=null){
            url = urlBuilder.build();
        }else{
            url = baseUrl.resolve(relativeUrl);
        }
        RequestBody body = null;
        if (formBuilder!=null)
            body = formBuilder.build();
        return requestBuilder.url(url).method(method,body).build();
    }
}
