package tmoney.gbi.bms.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tmoney.gbi.bms.config.processor.DbBatchProcessor;
import tmoney.gbi.bms.config.properties.AppProperties;
import tmoney.gbi.bms.mapper.SampleMapper;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AppProperties appProperties;
    private final Executor dbWorkerExecutor;
    // --- Processor 1 Dependencies ---
    private final SampleMapper sampleMapper;

    @Qualifier("encryptedLocationQueue")
    private final BlockingQueue<EncryptedLocationDto> encryptedLocationQueue;

    /*
    // --- Processor 2 Dependencies ---
    private final AnotherMapper anotherMapper;
    @Qualifier("anotherQueue")
    private final BlockingQueue<AnotherDto> anotherQueue;
    */

    /**
     * 애플리케이션 시작 시 모든 DB 워커를 초기화하고 실행합니다.
     */
    @Bean
    public ApplicationRunner dbWorkers() {
        return args -> {
            // 1. EncryptedLocationDto 처리기 실행
            startBatchProcessors("EncryptedLocationProcessor", encryptedLocationQueue, sampleMapper::insert);

            /*
            // 2. 새로운 타입의 처리기가 필요할 경우, 이 메서드를 간단히 한번 더 호출하면 됩니다.
            startBatchProcessors(
                    "AnotherDtoProcessor",
                    anotherQueue,
                    anotherMapper::insert
            );
            */
        };
    }

    /**
     * 특정 타입(T)의 메시지를 처리하는 DbBatchProcessor들을 생성하고 가상 스레드 실행기에 제출하는
     * 범용 헬퍼 메서드입니다.
     *
     * @param processorName       프로세서의 동작을 식별하기 위한 이름
     * @param queue               처리할 메시지가 담긴 BlockingQueue
     * @param batchInsertFunction List<T>를 받아 DB에 저장하는 함수 (e.g., mapper::insert)
     * @param <T>                 처리할 메시지의 데이터 타입
     */
    private <T> void startBatchProcessors(
            String processorName,
            BlockingQueue<T> queue,
            Consumer<List<T>> batchInsertFunction
    ) {
        int threadCount = appProperties.getDbWorker().getThreads();
        int batchSize = appProperties.getDbWorker().getBatchSize();

        log.debug("[{}] Initializing with {} threads...", processorName, threadCount);

        for (int i = 0; i < threadCount; i++) {
            DbBatchProcessor<T> processor = new DbBatchProcessor<>(
                    queue,
                    batchInsertFunction,
                    batchSize,
                    processorName
            );
            dbWorkerExecutor.execute(processor);
        }

        log.debug("[{}] All {} threads have been submitted.", processorName, threadCount);
    }
}
