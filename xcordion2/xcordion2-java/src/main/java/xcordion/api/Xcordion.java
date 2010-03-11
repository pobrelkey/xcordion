package xcordion.api;

public interface Xcordion<T extends TestElement<T>> {
    <C extends EvaluationContext<C>> void run(TestDocument<T> doc, C rootContext);

    CommandRepository getCommandRepository();

    XcordionEventListener<T> getBroadcaster();
}
