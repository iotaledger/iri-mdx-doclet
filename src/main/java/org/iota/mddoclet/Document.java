package org.iota.mddoclet;

public @interface Document {

    String name() default "";

    String returnParam() default "";

}
