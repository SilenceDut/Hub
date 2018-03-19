package com.silencedut.hub_compiler;


import com.google.auto.service.AutoService;
import com.silencedut.hub_annotation.HubInject;
import com.silencedut.hub_annotation.IFindImplClz;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(value=SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor{

    private Elements mElementUtils; //元素相关的辅助类
    private Messager mMessager; //日志相关的辅助类



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


        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(HubInject.class)) {
            if(annotatedElement.getKind() == ElementKind.CLASS ) {

                info("InjectProcessor process %s",annotatedElement);
                HubInject hubInject = annotatedElement.getAnnotation(HubInject.class);
                String qualifiedSuperClassName;

                try {
                    Class<?> clazz = hubInject.api();
                    info("InjectProcessor process api %s",clazz);
                    qualifiedSuperClassName = clazz.getCanonicalName();
                } catch (MirroredTypeException mte) {
                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                    qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                    info("InjectProcessor process clazz %s", TypeName.get(annotatedElement.asType()));
                }

                try {
                    generateFinder(TypeName.get(annotatedElement.asType()),qualifiedSuperClassName,annotatedElement.toString()).writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        }


        return false;
    }

    private JavaFile generateFinder(TypeName typeName,String qualifiedSuperClzName,String implClzName) {


        MethodSpec.Builder getMethodThread = MethodSpec.methodBuilder("newImplInstance")
                .addModifiers(Modifier.PUBLIC)
                .returns(typeName)
                .addStatement("return new "+implClzName+"()");

        // generate whole class

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiName =  qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".")+1, qualifiedSuperClzName.length());

        TypeSpec impl = TypeSpec.classBuilder(apiName+"_ImplHelper")
                .addSuperinterface(TypeName.get(IFindImplClz.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getMethodThread.build())
                .build();

        return JavaFile.builder(packageName, impl).build();
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
