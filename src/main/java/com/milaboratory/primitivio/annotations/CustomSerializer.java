package com.milaboratory.primitivio.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomSerializer {
    byte id();

    Class<?> type();
}
