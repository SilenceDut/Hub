package com.silencedut.hub_compiler;


import com.google.auto.service.AutoService;
import com.silencedut.hub_annotation.HubInject;
import com.silencedut.hub_annotation.IFindImplClz;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
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


                HubInject hubInject = annotatedElement.getAnnotation(HubInject.class);
                String qualifiedSuperClassName ;

                info("InjectProcessor process %s",annotatedElement);

                try {
                    hubInject.api();
                } catch (MirroredTypesException mte) {

                    Set<String> apiClassNames = new HashSet<>();

                    for(TypeMirror typeMirror : mte.getTypeMirrors()) {

                        DeclaredType classTypeMirror = (DeclaredType) typeMirror;

                        TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                        qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                        apiClassNames.add(qualifiedSuperClassName);
                        info("InjectProcessor process clazz %s", TypeName.get(annotatedElement.asType()));

                    }

                    for(String apiClassName : apiClassNames) {

                        try {
                            generateFinder(TypeName.get(annotatedElement.asType()),apiClassName
                                    ,annotatedElement.toString(),apiClassNames).writeTo(processingEnv.getFiler());
                        } catch (IOException e) {
                            e.getStackTrace();
                        }
                    }
                }
            }
        }


        return false;
    }


    private JavaFile generateFinder(TypeName typeName,String qualifiedSuperClzName,String implClzName,Set<String> sameImplApiClass) {

        TypeName stringSet = ParameterizedTypeName.get(TypeUtils.SET,TypeUtils.STRINGCLS);

        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement(TypeUtils.METHOD_GETAPIField+" = new $T()",  TypeUtils.HASHSETCLS);

        for(String api : sameImplApiClass) {
            staticBlock.addStatement(TypeUtils.METHOD_GETAPIField+".add($S)",api);
        }

        MethodSpec.Builder getSameImplApis = MethodSpec.methodBuilder(TypeUtils.METHOD_GETAPIS)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeUtils.SET)
                .addStatement("return "+TypeUtils.METHOD_GETAPIField);


        MethodSpec.Builder newInstance= MethodSpec.methodBuilder("newImplInstance")
                .addModifiers(Modifier.PUBLIC)
                .returns(typeName)
                .addStatement("return new "+implClzName+"()");

        // generate whole class

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName =  qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".")+1, qualifiedSuperClzName.length());

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName+"_ImplHelper")
                .addSuperinterface(TypeName.get(IFindImplClz.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(newInstance.build())
                .addField(stringSet,TypeUtils.METHOD_GETAPIField,Modifier.STATIC,Modifier.PRIVATE)
                .addStaticBlock(staticBlock.build())
                .addMethod(getSameImplApis.build())
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
