package com.silencedut.hub_compiler;

import com.silencedut.hub_annotation.HubInject;
import com.silencedut.hub_annotation.IFindImplClz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class HubImplProcessor extends BaseHubProcessor{

    HubImplProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    void process(Set<? extends Element> hubElements) {
        for (Element annotatedElement : hubElements) {
            if(annotatedElement.getKind() == ElementKind.CLASS ) {

                HubInject hubInject = annotatedElement.getAnnotation(HubInject.class);
                String qualifiedSuperClassName ;

                info("HubImplProcessor process %s",annotatedElement);

                try {
                    hubInject.api();
                } catch (MirroredTypesException mte) {

                    Set<String> apiClassNames = new HashSet<>();

                    for (TypeMirror typeMirror : mte.getTypeMirrors()) {

                        DeclaredType classTypeMirror = (DeclaredType) typeMirror;

                        TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                        qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                        apiClassNames.add(qualifiedSuperClassName);
                        info("HubImplProcessor process clazz %s", TypeName.get(annotatedElement.asType()));

                    }

                    for (String apiClassName : apiClassNames) {
                        try {

                            generateApiFinder(TypeName.get(annotatedElement.asType()), apiClassName, annotatedElement.toString(), apiClassNames).writeTo(processingEnv.getFiler());
                        } catch (Exception e) {
                            info("HubImplProcessor process error %s",e);
                            e.getStackTrace();
                        }
                    }
                }
            }
        }
    }

    private JavaFile generateApiFinder(TypeName typeName,String qualifiedSuperClzName,String implClzName,Set<String> sameImplApiClass) {

        TypeName stringSet = ParameterizedTypeName.get(ClassName.get(Set.class),ClassName.get(Class.class));

        TypeName newStaticInstance = TypeName.get(Object.class);

        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement(Constants.METHOD_GETAPIField+" = new $T()",  HashSet.class)
                .addStatement(Constants.IMPL_INSTANCE+" = new "+implClzName+"()");

        for(String api : sameImplApiClass) {
            staticBlock.addStatement(Constants.METHOD_GETAPIField+".add("+api+".class)");
        }

        MethodSpec.Builder getSameImplApis = MethodSpec.methodBuilder(Constants.METHOD_GETAPIS)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Set.class)
                .addStatement("return "+ Constants.METHOD_GETAPIField);


        MethodSpec.Builder newInstance = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addStatement("return "+Constants.IMPL_INSTANCE);

        // generate whole class

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName =  qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".")+1);

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName+Constants.CLASS_NAME_SEPARATOR+Constants.IMPL_HELPER_SUFFIX)
                .addSuperinterface(TypeName.get(IFindImplClz.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(newInstance.build())
                .addField(stringSet, Constants.METHOD_GETAPIField,Modifier.FINAL,Modifier.STATIC,Modifier.PRIVATE)
                .addField(newStaticInstance, Constants.IMPL_INSTANCE,Modifier.FINAL,Modifier.STATIC,Modifier.PRIVATE)
                .addStaticBlock(staticBlock.build())
                .addMethod(getSameImplApis.build())
                .build();

        return JavaFile.builder(packageName, impl).build();
    }
}
