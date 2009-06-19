package xcordion.api;

public interface Pragma {
    <T extends TestElement<T>, C extends EvaluationContext<C>> C evaluate(Xcordion<T> xcordion, T target, C context, String expression);
}
