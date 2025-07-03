package tmoney.gbi.bms.handler;

public interface MessageHandler<T> {
    boolean canHandle(String topic);
    void handle(T t);
}
