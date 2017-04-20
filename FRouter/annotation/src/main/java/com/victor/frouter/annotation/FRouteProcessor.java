package com.victor.frouter.annotation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static com.victor.frouter.annotation.Consts.FRAGMENT_FULL_NAME;
import static com.victor.frouter.annotation.Consts.IROUTERTABLE_NAME;
import static com.victor.frouter.annotation.Consts.ROUTE_ANNOTATION_TYPE;
import static com.victor.frouter.annotation.Consts.ROUTE_TABLE;
import static com.victor.frouter.annotation.Consts.ROUTE_TABLE_METHOD_NAME;


/**
 * Created by Administrator on 2017/4/7.
 */
@SupportedAnnotationTypes(ROUTE_ANNOTATION_TYPE)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FRouteProcessor extends AbstractProcessor {

    private Elements elementUtils = null;
    private Filer filer = null;
    private Messager messager = null;


    private void log(String fileName, String message) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:/" + fileName + ".txt"));
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FragmentRoute.class);
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        // 合法的TypeElement集合
        Set<TypeElement> typeElements = new HashSet<>();
        for (Element element : elements) {
            if (validateElement(element)) {
                typeElements.add((TypeElement) element);
            }
        }
//        generateModuleInfoTable(typeElements);
        generateRouteTable(typeElements);

        return false;
    }

    /**
     * Verify the annotated class.
     */
    private boolean validateElement(Element typeElement) {
        if (!processingEnv.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnv.getElementUtils().getTypeElement(FRAGMENT_FULL_NAME).asType())) {
            error(typeElement, "%s is not a subclass of `%s`.",
                    typeElement.getSimpleName().toString(), FRAGMENT_FULL_NAME);
            return false;
        }
        Set<Modifier> modifiers = typeElement.getModifiers();
        // non-public class.
        if (!modifiers.contains(Modifier.PUBLIC)) {
            error(typeElement, "The class %s is not public.", ((TypeElement) typeElement).getQualifiedName());
            return false;
        }
        // abstract class.
        if (modifiers.contains(Modifier.ABSTRACT)) {
            error(typeElement, "The class %s is abstract. You can't annotate abstract classes with @%s.",
                    ((TypeElement) typeElement).getQualifiedName(), FragmentRoute.class.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }

    private void generateRouteTable(Set<TypeElement> elements) {
        // Map<String, Class<? extends Fragment>> map
        TypeElement activityType = elementUtils.getTypeElement(FRAGMENT_FULL_NAME);
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(activityType))));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();

        MethodSpec.Builder handleActivityTable = MethodSpec.methodBuilder(ROUTE_TABLE_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);
        List<String> modules = new ArrayList<>();
        String moduleName = "";
        for (TypeElement element : elements) {
            if (element != null) {
                FragmentRoute route = element.getAnnotation(FragmentRoute.class);
                moduleName = route.moduleName();
                modules.add(moduleName);
                String[] paths = route.value();
                for (String path : paths) {
                    handleActivityTable.addStatement("map.put($S, $T.class)", path , ClassName.get(element));
                }
            }
        }

        TypeElement interfaceType = elementUtils.getTypeElement(IROUTERTABLE_NAME);
        TypeSpec type = TypeSpec.classBuilder(moduleName + ROUTE_TABLE)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(handleActivityTable.build())
                .addJavadoc("Generated by FragmentRoute. Do not edit it!\n")
                .build();
        try {
            JavaFile.builder(Consts.PACKAGE_NAME, type).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
