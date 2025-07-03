package tmoney.gbi.bms.config.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tmoney.gbi.bms.model.EncryptedLocationDto;
import tmoney.gbi.bms.proto.EncryptedLocation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Configuration
public class QueueConfig {

    @Value("${queue.obe.size:10000}")
    private Integer obeSize;

    // --- Message Queues ---
    @Bean
    @Qualifier("encryptedLocationQueue")
    public BlockingQueue<EncryptedLocationDto> encryptedLocationQueue() {
        return new LinkedBlockingQueue<>(obeSize); // 큐 사이즈는 메모리 상황에 맞게 조절
    }

}
