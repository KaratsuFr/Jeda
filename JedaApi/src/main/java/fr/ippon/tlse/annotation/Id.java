package fr.ippon.tlse.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonProperty;

@Retention(RUNTIME)
@JacksonAnnotationsInside
@JsonProperty("_id")
public @interface Id {

}
