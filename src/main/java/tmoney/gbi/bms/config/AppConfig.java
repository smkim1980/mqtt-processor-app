package tmoney.gbi.bms.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tmoney.gbi.bms.config.processor.DbBatchProcessor;
import tmoney.gbi.bms.mapper.SampleMapper;
import tmoney.gbi.bms.model.EncryptedLocationDto;
import tmoney.gbi.bms.proto.EncryptedLocation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class AppConfig {

    // --- DB Workers ---
    @Bean
    public ApplicationRunner obeDbWorker(
            SampleMapper sampleMapper,
            @Qualifier("encryptedLocationQueue") BlockingQueue<EncryptedLocationDto> queue,
            @Value("${app.db-worker.threads}") int threads,
            @Value("${app.db-worker.batch-size}") int batchSize) {
        return args -> {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) {
                DbBatchProcessor<EncryptedLocationDto> processor = new DbBatchProcessor<>(queue, sampleMapper::insert, batchSize);
                executor.submit(processor::processMessages);
            }
            log.info("[ObeDbWorker] Started with {} threads.", threads);
        };
    }

}
