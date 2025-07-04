package tmoney.gbi.bms.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // "dbWorkerExecutor" 라는 이름으로 가상 스레드 실행기를 등록
    @Bean(name = "dbWorkerExecutor")
    public Executor dbWorkerExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * MQTT 메시지 처리를 위한 전용 비동기 실행기.
     * @MqttSubscribe 메서드에서 병렬 처리를 위해 사용됩니다.
     */
    @Bean(name = "mqttExecutor")
    public Executor mqttExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * 비동기 작업 중 처리되지 않은 예외를 처리할 핸들러를 반환합니다.
     * @return 커스텀 예외 핸들러
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async method '{}' threw an exception.", method.getName(), ex);
            for (int i = 0; i < params.length; i++) {
                log.error("Parameter [{}]: {}", i, params[i]);
            }
        };
    }
}
