package com.example.library;

abstract class ParameterHandler {

    abstract void apply(RequestBuilder builder,String value);

    static final class Query extends ParameterHandler{

        private final String name;

        //传过来是注解的值
        Query(String name){
            if (name.isEmpty()){
                throw new IllegalArgumentException("name== null");
            }
            this.name = name;
        }

        @Override
        void apply(RequestBuilder builder, String value) {
            builder.addQueryParam(name,value);
        }
    }

    static final class Field extends ParameterHandler{
        private final String name;

        Field(String name){
            if (name.isEmpty()){
                throw new IllegalArgumentException("name == null");
            }
            this.name = name;
        }
        @Override
        void apply(RequestBuilder builder, String value) {
            builder.addFormField(name,value);
        }
    }
}
