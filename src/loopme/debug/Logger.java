package loopme.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logger implements Serializable {

    private static final long serialVersionUID = 42L;
    private static final ThreadLocal<StringBuilder> LOGGER;
    private static final Lock LOCK;

    static {
        LOGGER = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder();
            }
        };
        LOCK = new ReentrantLock();
    }

    private static String parseMessage(String message) {
        if(message == null) {
            return "";
        }
        return message;
    }

    public static void print() throws IOException {
        print("");
    }

    public static void print(Throwable e) throws IOException {
        if(e == null) {
            print();
            return;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        LOGGER.get().append(stringWriter.toString());
    }

    public static void print(String message) throws IOException {
        LOGGER.get().append(parseMessage(message)).append("\n");
    }

    public static void end() throws IOException {
        try {
            LOCK.lock();
            System.out.println(LOGGER.get().toString());
            LOGGER.get().setLength(0);
        } finally {
            LOCK.unlock();
        }
    }
}
