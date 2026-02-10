package hello.jdbc.repository.ex;

public class MyDuplicateEception extends MyDbException{

    public MyDuplicateEception() {
    }

    public MyDuplicateEception(String message) {
        super(message);
    }

    public MyDuplicateEception(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateEception(Throwable cause) {
        super(cause);
    }

}
