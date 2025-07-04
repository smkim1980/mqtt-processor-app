package tmoney.gbi.bms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tmoney.gbi.bms.config.processor.DbBatchProcessor;
import tmoney.gbi.bms.config.properties.AppProperties;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DbBatchProcessor의 대용량 동시성 처리 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("local")
public class DbBatchProcessorIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(DbBatchProcessorIntegrationTest.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    @Qualifier("encryptedLocationQueue")
    private BlockingQueue<EncryptedLocationDto> queue;

    @Test
    @DisplayName("여러 생산자가 동시에 대용량 데이터를 큐에 넣고, 소비자가 모두 정상 처리하는지 검증")
    void test_concurrent_high_volume_processing() throws InterruptedException {
        // --- 1. 테스트 환경 설정 ---
        final int TOTAL_MESSAGES = 100_000;
        final int PRODUCER_THREADS = 10;

        final AtomicInteger processedCount = new AtomicInteger(0);
        final Consumer<List<EncryptedLocationDto>> fakeDbInsert = (batch) -> {
            processedCount.addAndGet(batch.size());
        };

        // **개선점 1: 테스트 전용 소비자 실행기를 만들어 라이프사이클을 직접 제어합니다.**
        ExecutorService consumerExecutor = Executors.newVirtualThreadPerTaskExecutor();

        // --- 2. 소비자(DbBatchProcessor) 실행 ---
        int consumerThreads = appProperties.getDbWorker().getThreads();
        int batchSize = appProperties.getDbWorker().getBatchSize();

        log.info("테스트 시작: 총 메시지={}, 생산자 스레드={}, 소비자 스레드={}",
                TOTAL_MESSAGES, PRODUCER_THREADS, consumerThreads);

        for (int i = 0; i < consumerThreads; i++) {
            DbBatchProcessor<EncryptedLocationDto> processor = new DbBatchProcessor<>(
                    queue,
                    fakeDbInsert,
                    batchSize,
                    "Test-Processor"
            );
            consumerExecutor.execute(processor); // 테스트용 실행기 사용
        }

        // --- 3. 생산자(Producer) 실행 ---
        ExecutorService producerExecutor = Executors.newFixedThreadPool(PRODUCER_THREADS);
        CountDownLatch producerLatch = new CountDownLatch(PRODUCER_THREADS);

        for (int i = 0; i < PRODUCER_THREADS; i++) {
            producerExecutor.submit(() -> {
                try {
                    IntStream.range(0, TOTAL_MESSAGES / PRODUCER_THREADS).forEach(j -> {
                        try {
                            queue.put(new EncryptedLocationDto());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                } finally {
                    producerLatch.countDown();
                }
            });
        }

        // --- 4. 모든 작업이 완료될 때까지 대기 (개선된 방식) ---
        log.info("모든 생산자가 큐에 데이터를 추가할 때까지 대기 중...");
        producerLatch.await(1, TimeUnit.MINUTES);
        producerExecutor.shutdown();
        log.info("모든 생산자 작업 완료.");

        // **개선점 2: 큐가 완전히 비워질 때까지 기다립니다.**
        // 이는 소비자들이 모든 데이터를 가져갔음을 의미합니다.
        log.info("큐의 모든 데이터가 소비될 때까지 대기 중...");
        while (!queue.isEmpty()) {
            Thread.sleep(200); // 0.2초마다 큐 상태 확인
        }
        log.info("큐가 비어있음을 확인.");

        // **개선점 3: 소비자 실행기를 종료하고, 모든 스레드가 완전히 끝날 때까지 기다립니다.**
        // shutdown()은 실행 중인 스레드에 interrupt를 보내 '우아한 종료' 로직을 실행시킵니다.
        consumerExecutor.shutdown();
        // awaitTermination()은 모든 스레드가 완전히 종료될 때까지 대기합니다.
        if (!consumerExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
            log.warn("소비자 스레드가 시간 내에 완전히 종료되지 않았습니다.");
            consumerExecutor.shutdownNow(); // 강제 종료
        }
        log.info("모든 소비자 스레드 종료 완료.");

        // --- 5. 최종 결과 검증 ---
        log.info("최종 결과 검증: 총 메시지={}, 최종 처리된 메시지={}", TOTAL_MESSAGES, processedCount.get());
        assertEquals(TOTAL_MESSAGES, processedCount.get(), "큐에 삽입된 총 메시지 수와 최종 처리된 수가 일치해야 합니다.");
    }
}