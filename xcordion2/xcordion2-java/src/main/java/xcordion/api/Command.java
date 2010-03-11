package xcordion.api;

public interface Command {

	CommandType getCommandType();
	<T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression);

}
