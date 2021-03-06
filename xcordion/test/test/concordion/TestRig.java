package test.concordion;

import org.concordion.Concordion;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.OgnlValidatingEvaluator;

import java.io.IOException;


public class TestRig {

    private Object fixture = null;
    private EvaluatorFactory evaluatorFactory = new OgnlValidatingEvaluator.Factory();
    private StubSource stubSource = new StubSource();

    public TestRig withFixture(Object fixture) {
        this.fixture = fixture;
        return this;
    }

    public ProcessingResult processFragment(String fragment) {
        return process(wrapFragment(fragment));
    }

    public ExceptionResult processExceptionThrowingFragment(String fragment) {
        try {
            process(wrapFragment(fragment));
            throw new RuntimeException("No exception was thrown in executed fragment");
        } catch (Exception e) {
            return new ExceptionResult(e);
        }
    }

    public ProcessingResult process(Resource resource) {
        EventRecorder eventRecorder = new EventRecorder();
        StubTarget stubTarget = new StubTarget();
        Concordion concordion = new ConcordionBuilder()
            .withAssertEqualsListener(eventRecorder)
            .withThrowableListener(eventRecorder)
            .withSource(stubSource)
            .withEvaluatorFactory(evaluatorFactory)
            .withTarget(stubTarget)
            .build();
        
        try {
            ResultSummary resultSummary = concordion.process(resource, fixture);
            String xml = stubTarget.getWrittenString(resource);
            return new ProcessingResult(resultSummary, eventRecorder, xml);
        } catch (IOException e) {
            throw new RuntimeException("Test rig failed to process specification", e);
        } 
    }

    public ProcessingResult process(String html) {
        Resource resource = new Resource("/testrig");
        withResource(resource, html);
        return process(resource);
    }

    private String wrapFragment(String fragment) {
        fragment = "<body><fragment>" + fragment + "</fragment></body>";
        return wrapWithNamespaceDeclaration(fragment);
    }
    
    private String wrapWithNamespaceDeclaration(String fragment) {
        return "<html xmlns:concordion='"
            + ConcordionBuilder.NAMESPACE_CONCORDION_2007 + "'>"
            + fragment
            + "</html>";
    }

    public TestRig withStubbedEvaluationResult(Object evaluationResult) {
        this.evaluatorFactory = new StubEvaluator().withStubbedResult(evaluationResult);
        return this;
    }
    
    public TestRig withResource(Resource resource, String content) {
        stubSource.addResource(resource, content);
        return this;
    }
}
