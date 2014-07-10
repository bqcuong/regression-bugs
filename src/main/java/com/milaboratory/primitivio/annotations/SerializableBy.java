package com.milaboratory.primitivio.annotations;

import com.milaboratory.primitivio.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SerializableBy {
    Class<? extends Serializer> value();
}
