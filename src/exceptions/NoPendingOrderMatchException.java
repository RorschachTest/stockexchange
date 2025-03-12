package exceptions;

public class NoPendingOrderMatchException extends Exception {
    public NoPendingOrderMatchException() {
        super("No pending order match found");
    }
}
