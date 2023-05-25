package co.dalicious.client.core.interceptor.holder;

import co.dalicious.client.core.enums.ControllerType;

public class RequestContextHolder {
    private static final ThreadLocal<Integer> currentPort = new ThreadLocal<>();
    private static final ThreadLocal<String> currentEndpoint = new ThreadLocal<>();
    private static final ThreadLocal<String> currentBaseUrl = new ThreadLocal<>();
    private static final ThreadLocal<ControllerType> currentControllerType = new ThreadLocal<>();


    public static void setCurrentPort(Integer port) {
        currentPort.set(port);
    }

    public static Integer getCurrentPort() {
        return currentPort.get();
    }

    public static void setCurrentEndpoint(String endpoint) {
        currentEndpoint.set(endpoint);
    }

    public static String getCurrentEndpoint() {
        return currentEndpoint.get();
    }

    public static void setCurrentBaseUrl(String baseUrl) {
        currentBaseUrl.set(baseUrl);
    }

    public static void setCurrentControllerType(ControllerType controllerType) {
        currentControllerType.set(controllerType);
    }

    public static ControllerType getCurrentControllerType() {
        return currentControllerType.get();
    }

    public static String getCurrentBaseUrl() {
        return currentBaseUrl.get();
    }

    public static void clear() {
        currentPort.remove();
        currentEndpoint.remove();
        currentBaseUrl.remove();
        currentControllerType.remove();
    }
}