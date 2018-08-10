package com.silencedut.hub_compiler;

import com.silencedut.hub_annotation.HubActivity;
import com.silencedut.hub_annotation.IFindActivity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class HubActivityProcessor extends BaseHubProcessor {
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

                    info("HubActivityProcessor mte.getTypeMirrors() %s",mte.getTypeMirrors());

                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirrors().get(0);

                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                    String  apiActivityName = classTypeElement.getQualifiedName().toString();

                    try {
                        generateActivityFinder(hubActivity.methodName(), apiActivityName, ClassName.get((TypeElement) annotatedElement)).writeTo(processingEnv.getFiler());

                    } catch (Exception e) {
                        info("HubImplProcessor process error %s",e);
                        e.getStackTrace();
                    }
                }
            }
        }
    }

    private JavaFile generateActivityFinder(String path, String qualifiedSuperClzName, ClassName activityClass ) {


        info("HubActivityProcessor  qualifiedSuperClzName  %s ",qualifiedSuperClzName);
//        MethodSpec.Builder methodPath = MethodSpec.methodBuilder(TypeUtils.METHOD_ACTIVITYPATH)
//                .addModifiers(Modifier.PUBLIC)
//                .returns(String.class)
//                .addStatement("return $S",(qualifiedSuperClzName+"$"+path));

        MethodSpec.Builder targetActivity = MethodSpec.methodBuilder(TypeUtils.METHOD_GETActivityField)
                .addModifiers(Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return "+ activityClass +".class");

        String packageName = qualifiedSuperClzName.substring(0, qualifiedSuperClzName.lastIndexOf("."));
        String apiSimpleName =  qualifiedSuperClzName.substring(qualifiedSuperClzName.lastIndexOf(".")+1, qualifiedSuperClzName.length());

        TypeSpec impl = TypeSpec.classBuilder(apiSimpleName+"_"+path+"_ImplHelper")
                .addSuperinterface(TypeName.get(IFindActivity.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(targetActivity.build())
                .build();


        info("InjectProcessor process end ");

        return JavaFile.builder(packageName, impl).build();

    }
}
