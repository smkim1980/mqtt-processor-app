package tmoney.gbi.bms.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tmoney.gbi.bms.handler.MessageHandler;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionLocationHandler implements MessageHandler<EncryptedLocationDto> {

    private static final String TOPIC_PREFIX="obe/tbus/inb/";

    private final BlockingQueue<EncryptedLocationDto> queue;

    @Override
    public boolean canHandle(String topic) {
        return StringUtils.isBlank(topic) && topic.startsWith(TOPIC_PREFIX);
    }

    @Override
    public void handle(EncryptedLocationDto encryptedLocationDto) {
        try {
            if(!queue.offer(encryptedLocationDto , 5, TimeUnit.MILLISECONDS)){
                log.warn("Message queue is full. Message for device {} dropped.", encryptedLocationDto.toString());
            }
        } catch (InterruptedException e) {
            log.error("[EncryptionLocationHandler] Failed to handle message.", e);
        }
    }
}
