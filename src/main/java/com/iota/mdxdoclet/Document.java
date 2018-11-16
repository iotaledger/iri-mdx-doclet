package com.iota.mdxdoclet;

public @interface Document {

    String name();

    String returnParam() default "";

}
