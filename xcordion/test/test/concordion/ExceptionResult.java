package test.concordion;

public class ExceptionResult {
    private Exception exception;

    public ExceptionResult(Exception exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return exception.getMessage();
    }

    public String getName() {
        return exception.getClass().getName();
    }
}
