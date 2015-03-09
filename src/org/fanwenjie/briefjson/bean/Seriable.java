package org.fanwenjie.briefjson.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fan Wen Jie
 * @version 2015-03-01
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Seriable {

    /**
     * The order of serialization and deserialization of a member.
     *
     * @return The numeric order of serialization or deserialization.
     */
    int order() default Integer.MAX_VALUE;

    /**
     * The json member name.
     *
     * @return The name of the json member. The default is the name of the target that the attribute is applied to.
     */
    String name() default "";

    /**
     * The value that instructs the serialization engine that the member
     * must be present when reading or deserializing.
     *
     * @return true, if the member is required; otherwise, false.
     */
    boolean isRequired() default false;

}
