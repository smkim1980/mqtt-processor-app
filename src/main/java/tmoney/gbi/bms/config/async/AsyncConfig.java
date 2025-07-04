package tmoney.gbi.bms.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    // "dbWorkerExecutor" 라는 이름으로 가상 스레드 실행기를 등록
    @Bean(name = "dbWorkerExecutor")
    public Executor dbWorkerExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
