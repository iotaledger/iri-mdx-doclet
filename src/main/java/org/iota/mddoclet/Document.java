package org.iota.mddoclet;

public @interface Document {

    /**
     * 
     * @return The name used to display this method
     */
    String name() default "";

    /**
     * 
     * @return the return parameter name used to display the response of this method
     */
    String returnParam() default "";

}
