package org.mule.tooling.devkit.assist.context;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.AddAnnotationProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.IsFieldType;
import org.mule.tooling.devkit.assist.rules.Negation;

public class FieldDeclarationContext extends SmartContext {

    public FieldDeclarationContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtFieldVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, Stack<ASTNode> stackNodes) {
        int selectionOffset = getOffset();
        HasAnnotation hasAnnotation = new HasAnnotation("Configurable", selectionOffset);
        HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
        HasAnnotation hasInject = new HasAnnotation("Inject", selectionOffset);
        IsFieldType inyectable = new IsFieldType("MuleContext", selectionOffset).addType("ObjectStoreManager").addType("ObjectStore").addType("TransactionManager")
                .addType("QueueManager").addType("MuleConfiguration").addType("LifecycleManager").addType("ClassLoader").addType("ExpressionManager").addType("EndpointFactory")
                .addType("MuleClient").addType("SystemExceptionHandler").addType("SecurityManager").addType("WorkManager").addType("Registry").addType("MuleRegistry");

        AST ast = AST.newAST(AST.JLS4);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(stackNodes, hasAnnotation);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(stackNodes, hasDefault);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(stackNodes, hasInject);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(stackNodes, inyectable);
        if (!hasAnnotation.applies() && !hasInject.applies() && !inyectable.applies()) {
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.configurable", 0, context));
        } else {
            Negation negation = new Negation();
            negation.addRule(hasDefault);
            if (negation.applies() && !hasInject.applies() && !inyectable.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.default", 0, context));
            }
        }
        if (!hasInject.applies() && inyectable.applies()) {
            // TODO: Check ASTRewriteCorrectionProposal rewriteProposal = new ASTRewriteCorrectionProposal();
            // proposals.add(new AddAnnotationProposal("Add Inject", 0, context, ast.newQualifiedName(ast.newName("javax.inject"), ast.newSimpleName("Inject"))));
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.inject", 0, context));
        }
    }

}
