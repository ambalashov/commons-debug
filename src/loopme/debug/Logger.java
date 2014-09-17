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

    private static String trimToEmpty(String message) {
        if(message == null) {
            return "";
        }
        return message;
    }

    public static void print(Throwable e) throws IOException {
        if(e == null) {
            return;
        }
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        LOGGER.get().append(stringWriter.toString());
    }

    public static void print(String message) throws IOException {
        LOGGER.get().append(trimToEmpty(message));
    }

    public static void println() throws IOException {
        LOGGER.get().append('\n');
    }

    public static void println(String message) throws IOException {
        LOGGER.get().append(trimToEmpty(message)).append('\n');
    }

    public static void end() throws IOException {
        try {
            LOCK.lock();
            System.out.write(LOGGER.get().append('\n').toString().getBytes());
            LOGGER.get().setLength(0);
        } finally {
            LOCK.unlock();
        }
    }
}
