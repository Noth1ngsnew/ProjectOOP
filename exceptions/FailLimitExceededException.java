package exceptions;
public class FailLimitExceededException extends Exception {
    public FailLimitExceededException(String message) {
        super(message);
    }
}
