package xcordion.api;

public interface RowNavigator<T extends TestElement<T>> {
    T get(int column);
    T getSidecarCell();
    T getRowElement();
}
