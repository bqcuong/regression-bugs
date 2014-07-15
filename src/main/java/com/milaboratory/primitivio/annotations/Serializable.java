package com.milaboratory.primitivio.annotations;

import com.milaboratory.primitivio.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Serializable {
    Class<? extends Serializer> by() default Serializer.class;

    CustomSerializer[] custom() default {};

    boolean asJson() default false;
}
