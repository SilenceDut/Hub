package com.silencedut.hub_compiler;


import com.example.hub_annotation.HubInject;
import com.google.auto.service.AutoService;
import com.silencedut.hub_compiler.model.AnnotatedClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(value=SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor{

    private Elements mElementUtils; //元素相关的辅助类
    private Messager mMessager; //日志相关的辅助类

    private List<Element> mSubscribedMethodElement = new ArrayList<>();


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(HubInject.class.getCanonicalName());
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        mSubscribedMethodElement.clear();
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(HubInject.class)) {
            if(annotatedElement.getKind() == ElementKind.CLASS ) {
                mSubscribedMethodElement.add(annotatedElement);
//                info("InjectProcessor process %s",annotatedElement );
            }
        }

        AnnotatedClass annotatedClass = new AnnotatedClass(mSubscribedMethodElement,mElementUtils);

        try {
            annotatedClass.generateFinder().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.getStackTrace();
        }
        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }


    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

}
