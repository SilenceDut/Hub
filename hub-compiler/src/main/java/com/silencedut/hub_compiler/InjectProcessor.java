package com.silencedut.hub_compiler;


import com.google.auto.service.AutoService;
import com.silencedut.hub_annotation.HubActivity;
import com.silencedut.hub_annotation.HubInject;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedSourceVersion(value=SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor{

    private HubImplProcessor mHubImplProcessor;
    private HubActivityProcessor mHubActivityProcessor;


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> processAnnotation = new HashSet<>();
        processAnnotation.add(HubInject.class.getCanonicalName());
        processAnnotation.add(HubActivity.class.getCanonicalName());
        return processAnnotation;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mHubImplProcessor = new HubImplProcessor(processingEnv);
        mHubActivityProcessor = new HubActivityProcessor(processingEnv);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        mHubImplProcessor.process(roundEnvironment.getElementsAnnotatedWith(HubInject.class));
        mHubActivityProcessor.process(roundEnvironment.getElementsAnnotatedWith(HubActivity.class));

        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
