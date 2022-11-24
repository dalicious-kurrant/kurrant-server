package co.dalicious.system.util.exception;

public enum ExceptionClass {
    ORDER("주문"),
    ADMIN("백오피스"),
    GROUP("고객사"),
    MAKERS("메이커스"),
    KURRANT("앱");

    private final String exceptionClass;

    ExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    @Override
    public String toString() {
        return getExceptionClass() + " 오류 발생. ";
    }
}
