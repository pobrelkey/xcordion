package xcordion.api;

public interface XcordionEvents<T extends TestElement<T>>  {

    void begin(T target);

    void succesfulSet(T target, String expression, Object value);
	void exception(T target, String expression, Throwable e);
	void succesfulExecute(T target, String expression);
	void insertText(T target, String expression, Object result);
	void successfulAssertBoolean(T target, String expression, boolean value);
	void failedAssertBoolean(T target, String expression, boolean expected, Object actual);
    void successfulAssertEquals(T target, String expression, Object expected);
    void failedAssertEquals(T target, String expression, Object expected, Object actual);
    void successfulAssertContains(T target, String expression, Object expected, Object actual);
    void failedAssertContains(T target, String expression, Object expected, Object actual);

	void missingRow(RowNavigator<T> row);
    void surplusRow(RowNavigator<T> row);

    void end(T target);
}
