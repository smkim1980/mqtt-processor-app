package tmoney.gbi.bms.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.handler.MessageHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicMessageRouter<T> { // <T> 제네릭 타입을 클래스 레벨로 이동

    private final List<MessageHandler<T>> handlers; // MessageHandler도 제네릭 타입으로 지정

    public void route(String topic, T t) {
        boolean isHandled = false;
        for (MessageHandler<T> handler : handlers) {
            if (handler.canHandle(topic)) {
                handler.handle(t);
                isHandled = true; // 처리할 핸들러가 하나라도 있었음을 표시
            }
        }

        if (!isHandled) {
            log.warn("No handler found for topic: {}", topic);
        }
    }
}
