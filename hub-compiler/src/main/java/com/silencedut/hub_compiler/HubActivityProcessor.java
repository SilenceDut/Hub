package com.silencedut.hub_compiler;

import com.silencedut.hub_annotation.HubActivity;
import com.silencedut.hub_annotation.IFindActivity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class HubActivityProcessor extends BaseHubProcessor {
    private static final String ASSIGN_TARGET = "assignTarget";
    private static final String ASSIGN_TARGET_DOT = "assignTarget.";

    HubActivityProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    void process(Set<? extends Element> hubElements) {
        for (Element annotatedElement : hubElements) {
            if(annotatedElement.getKind() == ElementKind.CLASS ) {

                HubActivity hubActivity = annotatedElement.getAnnotation(HubActivity.class);

                info("HubActivityProcessor process %s",annotatedElement);

                try {
                    info("hubActivity.methodName() %s",hubActivity.methodName());

                    hubActivity.activityApi();
                } catch (MirroredTypesException mte) {

                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirrors().get(0);

                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();


                    try {
                        generateActivityFinder(hubActivity.methodName(), classTypeElement, ClassName.get((TypeElement) annotatedElement)).writeTo(processingEnv.getFiler());

                    } catch (Exception e) {
                        info("HubImplProcessor process error %s",e);
                        e.getStackTrace();
                    }
                }
            }
        }
    }

    private JavaFile generateActivityFinder(String path,  TypeElement apiElement, ClassName activityClass ) throws Exception{
        String  qualifiedSuperClzName = apiElement.getQualifiedName().toString();



        TypeName stringList = ParameterizedTypeName.get(ClassName.get(List.class),ClassName.get(String.class));

        CodeBlock.Builder staticBlock = CodeBlock.builder()
                .addStatement(Constants.Filed_ParamName+" = new $T()",  ArrayList.class);

        MethodSpec.Builder getParamNames = MethodSpec.methodBuilder(Constants.Filed_ParamName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(List.class)
                .addStatement("return "+ Constants.Filed_ParamName);

        info("HubActivityProcessor  qualifiedSuperClzName  %s ",qualifiedSuperClzName);
        MethodSpec.Builder methodInject = MethodSpec.methodBuilder(Constants.METHOD_INJECT)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class,"target")
                .addAnnotation(Override.class);


        methodInject.addStatement("$T "+ ASSIGN_TARGET +"= ($T) target", activityClass, activityClass);

        for(Element element : apiElement.getEnclosedElements()) {

            if(element.getSimpleName().equals(element.getSimpleName())) {
                for(VariableElement variableElement : ((ExecutableElement)element).getParameters()) {
                    info("HubActivityProcessor parameter %s",variableElement.getSimpleName());

                    staticBlock.addStatement(Constants.Filed_ParamName+".add($S)",variableElement.getSimpleName());
                    String originalValue = ASSIGN_TARGET_DOT + variableElement.getSimpleName();
                    String assignStatement =  ASSIGN_TARGET_DOT + variableElement.getSimpleName() +
                            " = "+ASSIGN_TARGET_DOT+"getIntent().";
                    assignStatement = buildStatement(originalValue,assignStatement,variableElement.asType().toString(),variableElement.asType().getKind());

                    if(assignStatement.startsWith("com.silencedut.hub.navigation.HubJsonHelper")){
                        assignStatement = ASSIGN_TARGET_DOT + variableElement.getSimpleName() + " = " +assignStatement;
                        methodInject.addStatement(assignStatement,variableElement.getSimpleName(),ClassName.get(variableElement.asType()));
                    } else {
                        methodInject.addStatement(assignStatement,variableElement.getSimpleName());
                    }

                }
            }
        }


        MethodSpec.Builder targetActivity = MethodSpec.methodBuilder(Constants.METHOD_GETActivityField)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Class.class)
                .addStatement("return "+ activityClass +".class");

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName =  qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".")+1, qualifiedSuperClzName.length());

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName+Constants.CLASS_NAME_SEPARATOR+path+Constants.ACTIVITY_HELPER_SUFFIX)
                .addSuperinterface(TypeName.get(IFindActivity.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(stringList, Constants.Filed_ParamName,Modifier.STATIC,Modifier.PRIVATE)
                .addStaticBlock(staticBlock.build())
                .addMethod(getParamNames.build())
                .addMethod(methodInject.build())
                .addMethod(targetActivity.build())

                .build();


        info("InjectProcessor process end ");

        return JavaFile.builder(packageName, impl).build();

    }

    private String buildStatement(String originalValue, String assignStatement,String type,TypeKind typeKind) {
        if (Constants.BOOLEAN.equals(type) || typeKind == TypeKind.BOOLEAN ) {
            assignStatement +=  "getBooleanExtra($S, " + originalValue + ")" ;
        } else if (Constants.BYTE.equals(type) ||typeKind == TypeKind.BYTE ) {
            assignStatement += "getByteExtra($S, " + originalValue + ")";
        } else if (Constants.SHORT.equals(type) ||typeKind == TypeKind.SHORT ) {
            assignStatement += "getShortExtra($S, " + originalValue + ")";
        } else if (Constants.INTEGER.equals(type) ||typeKind == TypeKind.INT ) {
            assignStatement +="getIntExtra($S, " + originalValue + ")";
        } else if (Constants.LONG.equals(type) ||typeKind == TypeKind.LONG ) {
            assignStatement +="getLongExtra($S, " + originalValue + ")";
        }else if(typeKind == TypeKind.CHAR ){
            assignStatement += "getCharExtra($S, " + originalValue + ")";
        } else if (Constants.FLOAT.equals(type)  ||typeKind == TypeKind.FLOAT ) {
            assignStatement += "getFloatExtra($S, " + originalValue + ")";
        } else if (Constants.DOUBEL.equals(type) || typeKind == TypeKind.DOUBLE) {
            assignStatement +="getDoubleExtra($S, " + originalValue + ")";
        } else if (Constants.STRING.equals(type)) {
            assignStatement +="getStringExtra($S)";
        } else {
            assignStatement ="com.silencedut.hub.navigation.HubJsonHelper.fromJson("+ASSIGN_TARGET_DOT+"getIntent().getStringExtra($S),new com.silencedut.hub_annotation.TypeHelper<$T>(){}.getType())";
        }

        return assignStatement;
    }

}
