package demo

import org.codehaus.groovy.transform.GroovyASTTransformationClass

@GroovyASTTransformationClass('demo.MyTransformation')
@interface MyAnnotation {
}
