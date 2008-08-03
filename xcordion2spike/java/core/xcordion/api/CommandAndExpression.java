package xcordion.api;

public class CommandAndExpression {
	public Command command;
	public String expression;
	public CommandAndExpression(Command command, String expression) {
		this.command = command;
		this.expression = expression;
	}
	public Command getCommand() {
		return command;
	}
	public String getExpression() {
		return expression;
	}
}
