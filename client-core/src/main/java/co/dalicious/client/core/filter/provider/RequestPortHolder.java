package co.dalicious.client.core.filter.provider;

public class RequestPortHolder {
    private static final ThreadLocal<Integer> currentPort = new ThreadLocal<>();

    public static void setCurrentPort(Integer port) {
        currentPort.set(port);
    }

    public static Integer getCurrentPort() {
        return currentPort.get();
    }

    public static void clear() {
        currentPort.remove();
    }
}
