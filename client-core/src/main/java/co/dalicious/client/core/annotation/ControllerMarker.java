package co.dalicious.client.core.annotation;

import co.dalicious.client.core.enums.ControllerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ControllerMarker {
    ControllerType value();
}
