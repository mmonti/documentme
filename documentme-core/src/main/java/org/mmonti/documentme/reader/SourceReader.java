package org.mmonti.documentme.reader;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.Searcher;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import org.mmonti.documentme.model.Endpoint;
import org.mmonti.documentme.model.Return;
import org.mmonti.documentme.reader.parser.ParamDocletAnnotation;
import org.mmonti.documentme.reader.parser.ReturnDocletAnnotation;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.aol.cyclops.matcher.builders.Matching.when;
import static java.util.stream.Collectors.*;

/**
 * Created by mmonti on 11/21/15.
 */
public class SourceReader {

    public static final String CONST_VALUE = "value";
    public static final String CONST_METHOD = "method";

    public static final String DOCLET_RESPONSES = "responses";
    public static final String DOCLET_HEADERS = "headers";
    public static final String DOCLET_PARAM = "param";
    public static final String DOCLET_RETURN = "return";

    public static final String CONST_ATTR_REQUIRED = "required";
    public static final String CONST_ATTR_VALUE = "value";
    public static final String EMPTY = "";
    public static final String CONST_DOT = ".";

    private JsonParser jsonParser = new JacksonJsonParser();

    private File sourcePath;
    private String sourcePattern;

    private Set<Endpoint> endpointSet;

    private SourceReader(final String source, final String sourcePattern) {
        this(new File(source), sourcePattern);
    }

    private SourceReader(final File sourcePath, final String sourcePattern) {
        this.sourcePath = sourcePath;
        this.sourcePattern = sourcePattern;
    }

    public static SourceReader getInstance(final String source, final String sourcePattern){
        return new SourceReader(source, sourcePattern);
    }

    public static SourceReader getInstance(final File source, final String sourcePattern){
        return new SourceReader(source, sourcePattern);
    }

    public void read(final File source, final String sourcePattern) {
        final JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(source);

        final Collection<JavaClass> classes = builder.search(currentClass -> currentClass.getPackage().getName().equals(sourcePattern));
        this.endpointSet = classes.stream().map(currentClass -> readClassInfo(new HashSet(), currentClass)).collect(toSet());
    }

    /**
     *
     * @param endpoint
     * @param javaClass
     */
    private Set<Endpoint> readClassInfo(final Endpoint endpoint, final JavaClass javaClass) {
        // = Class information.
        endpoint.setClassName(javaClass.getFullyQualifiedName());

        // = Class Annotation.
        final Optional<JavaAnnotation> classAnnotation = javaClass.getAnnotations().stream().filter(current -> current.getType().getClass().equals(RequestMapping.class)).findFirst();
        classAnnotation.ifPresent(annotation -> {
            final AnnotationValue value = annotation.getProperty(CONST_VALUE);
            endpoint.setBase(Optional.ofNullable(value.getParameterValue()).orElse(EMPTY).toString());
        });

        // = Methods Information.
        return javaClass.getMethods().stream().map(javaMethod -> readMethodInfo(new Endpoint(), javaMethod)).collect(toSet());
    }

    /**
     *
     * @param endpoint
     * @param javaMethods
     */
    private Endpoint readMethodsInfo(final Endpoint endpoint, final List<JavaMethod> javaMethods) {
        javaMethods.stream().map(javaMethod -> readMethodInfo(new Endpoint(), javaMethod)).collect(toSet());

        return endpoint;
    }

    /**
     *
     * @param endpoint
     * @param javaMethod
     */
    private Endpoint readMethodInfo(final Endpoint endpoint, final JavaMethod javaMethod) {
        // = Method information.
        endpoint.setMethodName(javaMethod.getName());

        // = Method Comments.
        endpoint.setComment(javaMethod.getComment());

        // = Method parameters
        final List<JavaParameter> javaParameters = javaMethod.getParameters();
        endpoint.setMethodParameters(javaParameters.stream().collect(toMap(
                p -> p.getName(),
                p -> p.getType().getFullyQualifiedName())
        ));

        // = Annotations.
        final List<JavaAnnotation> annotations = javaMethod.getAnnotations();

        // = Add the method parameter annotations.
        annotations.addAll(javaParameters.stream()
                .map(p -> p.getAnnotations())
                .flatMap(x -> x.stream())
                .collect(toList())
        );

        when()
                .isTrue((JavaAnnotation annotation) -> annotation.getType().getFullyQualifiedName().equals(RequestMapping.class.getCanonicalName()))
                .thenConsume(matchingAnnotation -> {
                    final AnnotationValue method = matchingAnnotation.getProperty(CONST_METHOD);
                    final AnnotationValue value  = matchingAnnotation.getProperty(CONST_VALUE);

                    Object methodParameterValue = method.getParameterValue();
                    String methodName = methodParameterValue.toString();

                    Object parameterValue = value.getParameterValue();
                    String parameterName = parameterValue.toString();

                    endpoint.setMethod(RequestMethod.valueOf(methodName.substring(methodName.lastIndexOf(CONST_DOT)+1)));
                    endpoint.setPath(parameterName);
                }).
        when()
                .isTrue((JavaAnnotation annotation) -> annotation.getType().getFullyQualifiedName().equals(PathVariable.class.getCanonicalName()))
                .thenConsume(matchingAnnotation -> {
                    final AnnotationValue value = matchingAnnotation.getProperty(CONST_VALUE);
                    final Object parameterValue = value.getParameterValue();
                    final String parameterName = parameterValue.toString();

                    endpoint.addPathParam(parameterName, parameterName);
                }).
        when()
                .isTrue((JavaAnnotation annotation) -> annotation.getType().getFullyQualifiedName().equals(RequestParam.class.getCanonicalName()))
                .thenConsume(matchingAnnotation -> {
                    final AnnotationValue value = matchingAnnotation.getProperty(CONST_VALUE);
                    final AnnotationValue required = matchingAnnotation.getProperty(CONST_ATTR_REQUIRED);

                    final Object parameterValue = value.getParameterValue();
                    final String parameterName = parameterValue.toString();

                    final Object requiredParameterValue = required.getParameterValue();
                    final String requiredParameterName = requiredParameterValue.toString();

                    endpoint.addQueryParam(parameterName, (requiredParameterName == null) ? Boolean.FALSE.toString() : requiredParameterName);
                })
                .matchFromStream(annotations.stream()).collect(Collectors.toList());

        when()
                .isTrue((DocletTag tag) -> tag.getName().equals(DOCLET_RETURN))
                .thenConsume(matchingTag -> {
                    final ReturnDocletAnnotation tag = new ReturnDocletAnnotation(matchingTag.getValue());
                    endpoint.setReturns(new Return(tag.getParameter(), tag.getDescription()));
                }).
        when()
                .isTrue((DocletTag tag) -> tag.getName().equals(DOCLET_HEADERS))
                .thenConsume(matchingTag -> {
                    final String headers = matchingTag.getValue();
                    endpoint.setResponseHeaders(jsonParser.parseMap(headers));
                }).
        when()
                .isTrue((DocletTag tag) -> tag.getName().equals(DOCLET_PARAM))
                .thenConsume(matchingTag -> {
                    final ParamDocletAnnotation annotation = new ParamDocletAnnotation(matchingTag.getValue());
                    endpoint.addPathParam(annotation.getParameter(), annotation.getDescription());
                }).
        when()
                .isTrue((DocletTag tag) -> tag.getName().equals(DOCLET_RESPONSES))
                .thenConsume(matchingTag -> {
                    final String responseStatus = matchingTag.getValue();
                    endpoint.setResponseStatus(jsonParser.parseMap(responseStatus));
                })
                .matchFromStream(javaMethod.getTags().stream()).collect(Collectors.toList());

        return endpoint;
    }

    public String getSourcePattern() {
        return sourcePattern;
    }

    public File getSourcePath() {
        return sourcePath;
    }

    /**
     *
     * @return
     */
    public Set<Endpoint> getEndpointSet() {
        if (this.endpointSet == null) {
            read(this.sourcePath, this.sourcePattern);
        }
        return endpointSet;
    }

    public static void main(String[] args) {
        String base = "/home/mmonti/Development/workspace/documentme/documentme-sample/src/main/java/";
        String pattern = "**/sample/*.java";
        String packageStr = "org.mmonti.documentme.sample";
//        final SourceReader reader = SourceReader.getInstance(base, pattern);
//        final Set<Endpoint> endpointSet = reader.getEndpointSet();
//
//        try {
//            System.out.println(new ObjectMapper().writeValueAsString(endpointSet));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

//        new AntPathMatcher().match(pattern, base);

        JavaProjectBuilder builder = new JavaProjectBuilder();

        builder.addSourceTree( new File( base ) );
        Collection<JavaClass> classes = builder.search(new Searcher() {
            @Override
            public boolean eval(JavaClass javaClass) {
                return javaClass.getPackage().getName().equals(packageStr);
            }
        });
        for (JavaClass aClass : builder.getClasses()) {
            System.out.println(aClass.getMethods());
        }
    }
}
