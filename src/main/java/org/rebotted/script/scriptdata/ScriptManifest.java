package org.rebotted.script.scriptdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptManifest {

    String name();

    String author();

    String description() default "";

    double version() default 1.0;

    SkillCategory category() default SkillCategory.MISC;


}