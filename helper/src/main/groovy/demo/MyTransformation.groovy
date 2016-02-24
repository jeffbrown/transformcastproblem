package demo

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation
class MyTransformation implements ASTTransformation{
    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        def classNode = nodes[1]

        def methods = classNode.getAllDeclaredMethods()
        for(MethodNode methodNode : methods) {
            processNode methodNode
        }

        def properties = classNode.getProperties()
        for(PropertyNode property : properties) {
            Expression expression = property.initialExpression
            if(expression instanceof ClosureExpression) {
                processNode expression
            }
        }
    }

    protected processNode(expression) {
        def parameters = expression.parameters
        if(parameters) {
            def originalCode = expression.code
            def newCode = new BlockStatement()
            def activatingCode = getActivatingCode(parameters[0])
            newCode.addStatement(activatingCode)
            newCode.addStatement(originalCode)
            expression.code = newCode
        }
    }

    /**
     * Return a block of code that will call .activate() on the parameter
     */
    protected getActivatingCode(Parameter parameter) {
        def code = new BlockStatement()
        def parameterType = parameter.getType()
        def activateMethod = parameterType.getDeclaredMethod('activate', new Parameter[0])
        if(activateMethod) {
            def callActivate = new MethodCallExpression(new VariableExpression(parameter.getName()), 'activate', new TupleExpression())

            // the following line causes a problem for closure parameters...
            callActivate.setMethodTarget(activateMethod)

            code.addStatement(new ExpressionStatement(callActivate))
        }
        code
    }
}
