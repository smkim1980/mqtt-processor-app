package tmoney.gbi.bms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import tmoney.gbi.bms.mapper.SampleMapper;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DbBatchProcessor의 대용량 동시성 처리 통합 테스트 (H2 DB 연동)
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

    @Autowired
    private SampleMapper sampleMapper; // DB 작업을 위한 매퍼

    /**
     * 각 테스트 실행 전 테이블을 정리하여 테스트 격리성을 보장합니다.
     */
    @BeforeEach
    void setUp() {
        sampleMapper.deleteAll();
        log.info("테스트 시작 전 테이블 데이터를 모두 삭제했습니다.");
    }

    /**
     * @AfterEach 도 동일한 목적으로 사용할 수 있습니다.
     */
    @AfterEach
    void tearDown() {
        // queue.clear(); // 필요하다면 큐도 비울 수 있습니다.
    }


    @Test
    @DisplayName("여러 생산자가 동시에 대용량 데이터를 큐에 넣고, 소비자가 모두 DB에 정상 처리하는지 검증")
    void test_concurrent_high_volume_processing_with_h2() throws InterruptedException {
        // --- 1. 테스트 환경 설정 ---
        final int TOTAL_MESSAGES = 10000;
        final int PRODUCER_THREADS = 10;
        final int MESSAGES_PER_PRODUCER = TOTAL_MESSAGES / PRODUCER_THREADS;

        ExecutorService consumerExecutor = Executors.newVirtualThreadPerTaskExecutor();

        // --- 2. 소비자(DbBatchProcessor) 실행 ---
        int consumerThreads = appProperties.getDbWorker().getThreads();
        int batchSize = appProperties.getDbWorker().getBatchSize();

        log.info("테스트 시작: 총 메시지={}, 생산자 스레드={}, 소비자 스레드={}",
                TOTAL_MESSAGES, PRODUCER_THREADS, consumerThreads);

        for (int i = 0; i < consumerThreads; i++) {
            // 이제 DbBatchProcessor는 실제 DB에 삽입하는 sampleMapper::insert를 사용합니다.
            DbBatchProcessor<EncryptedLocationDto> processor = new DbBatchProcessor<>(
                    queue,
                    sampleMapper::insert,
                    batchSize,
                    "Test-Processor-" + i
            );
            consumerExecutor.execute(processor);
        }

        // --- 3. 생산자(Producer) 실행 ---
        ExecutorService producerExecutor = Executors.newFixedThreadPool(PRODUCER_THREADS);
        CountDownLatch producerLatch = new CountDownLatch(PRODUCER_THREADS);

        for (int i = 0; i < PRODUCER_THREADS; i++) {
            producerExecutor.submit(() -> {
                try {
                    for (int j = 0; j < MESSAGES_PER_PRODUCER; j++) {
                        // EncryptedLocationDto 객체 생성 (실제 DTO 구조에 맞게 수정 필요)
                        queue.put(new EncryptedLocationDto(/* DTO 생성자 파라미터 */));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    producerLatch.countDown();
                }
            });
        }

        // --- 4. 모든 작업이 완료될 때까지 대기 ---
        log.info("모든 생산자가 큐에 데이터를 추가할 때까지 대기 중...");
        producerLatch.await(1, TimeUnit.MINUTES);
        producerExecutor.shutdown();
        log.info("모든 생산자 작업 완료.");

        log.info("큐의 모든 데이터가 소비될 때까지 대기 중...");
        while (!queue.isEmpty()) {
            Thread.sleep(200);
        }
        log.info("큐가 비어있음을 확인.");

        consumerExecutor.shutdown();
        if (!consumerExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
            log.warn("소비자 스레드가 시간 내에 완전히 종료되지 않았습니다.");
            consumerExecutor.shutdownNow();
        }
        log.info("모든 소비자 스레드 종료 완료.");

        // --- 5. 최종 결과 검증 (DB count 확인) ---
        long finalDbCount = sampleMapper.count();
        log.info("최종 결과 검증: 총 메시지={}, 최종 DB 저장된 메시지={}", TOTAL_MESSAGES, finalDbCount);
        assertEquals(TOTAL_MESSAGES, finalDbCount, "큐에 삽입된 총 메시지 수와 최종 DB에 저장된 수가 일치해야 합니다.");
    }
}