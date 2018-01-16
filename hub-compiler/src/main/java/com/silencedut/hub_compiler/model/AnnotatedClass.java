package com.silencedut.hub_compiler.model;

import com.silencedut.hub_annotation.HubInject;
import com.silencedut.hub_compiler.TypeUtil;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

public class AnnotatedClass {

    public Elements mElementUtils;
    private Map<String,String> mImplClzByApiClz = new HashMap<>();

    public AnnotatedClass( List<Element> subscribedMethodElements,Elements elementUtils) {
        this.mElementUtils = elementUtils;
        for(Element implClsElement : subscribedMethodElements) {

            HubInject hubInject = implClsElement.getAnnotation(HubInject.class);
            String qualifiedSuperClassName;
            try {
                Class<?> clazz = hubInject.api();
                qualifiedSuperClassName = clazz.getCanonicalName();
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();

            }

            mImplClzByApiClz.put(qualifiedSuperClassName,implClsElement.toString());
        }

    }


    public JavaFile generateFinder() {

        TypeName stringMap = ParameterizedTypeName.get(TypeUtil.MapClz,TypeUtil.StringCls,TypeUtil.StringCls);

        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement("implClzByApiClz = new $T()",TypeUtil.HashMapClz);

        for(Map.Entry entry:mImplClzByApiClz.entrySet()) {
            staticBlock.addStatement("implClzByApiClz.put($S,$S)",entry.getKey(),entry.getValue());
        }



        MethodSpec.Builder getMethodThread = MethodSpec.methodBuilder("getImplClsName")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeUtil.StringCls)
                .addStatement("return implClzByApiClz.get(methodName)")
                .addParameter(String.class, "methodName");

        // generate whole class
        TypeSpec finderClass = TypeSpec.classBuilder("FindImplClzHelper")

            .addSuperinterface(TypeUtil.ImplClsFinder)
            .addField(stringMap,"implClzByApiClz",Modifier.STATIC,Modifier.PRIVATE)
            .addStaticBlock(staticBlock.build())
            .addMethod(getMethodThread.build())
            .build();

        String packageName = "com.silencedut.hub";

        return JavaFile.builder(packageName, finderClass).build();
    }
}
