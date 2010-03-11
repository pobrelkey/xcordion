package xcordion.api;

import java.util.List;

public interface RowNavigator<T extends TestElement<T>> {
    T get(int column);
    T getSidecarCell();
    T getRowElement();
    List<ItemAndExpression<Pragma>> getPragmas();
}
