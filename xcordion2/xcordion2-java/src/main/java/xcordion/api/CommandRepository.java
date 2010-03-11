package xcordion.api;

import java.util.List;

public interface CommandRepository {

    <T extends TestElement<T>> ItemAndExpression<Command> commandForElement(T element, IgnoreState ignoreState);
    <T extends TestElement<T>> List<ItemAndExpression<Pragma>> pragmasForElement(T element);

}

    