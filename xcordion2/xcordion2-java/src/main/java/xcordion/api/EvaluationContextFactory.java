package xcordion.api;

public interface EvaluationContextFactory<C extends EvaluationContext<C>> {
	C newContext(String languageName, Object rootObject);
}
