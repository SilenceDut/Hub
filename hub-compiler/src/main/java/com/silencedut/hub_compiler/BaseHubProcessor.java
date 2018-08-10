package com.silencedut.hub_compiler;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public abstract class BaseHubProcessor {


    private Messager mMessager; //日志相关的辅助类
    protected ProcessingEnvironment processingEnv;
    BaseHubProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        mMessager = processingEnv.getMessager();

    }

    abstract void process(Set<? extends Element> hubElements);

    protected void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }


    protected void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

}
