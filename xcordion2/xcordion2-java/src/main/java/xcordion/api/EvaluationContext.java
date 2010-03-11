package xcordion.api;

public interface EvaluationContext<C extends EvaluationContext<C>> {
	C subContext();
	<T extends TestElement<T>> Object eval(String expression, T element);
	<T extends TestElement<T>> Object set(String expression, T element);
	<T extends TestElement<T>> Iterable<C> iterate(String expression, T element);
	Object getVariable(String name);
	void setVariable(String name, Object value);
    <T extends TestElement<T>> Object getValue(T element, Class asClass);
    IgnoreState getIgnoreState();
    C withIgnoreState(IgnoreState ignoreState);
}
