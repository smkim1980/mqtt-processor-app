package tmoney.gbi.bms.config.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter; // 로그 메시지 포맷팅을 위해 추가

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class DbBatchProcessor<T> implements Runnable {

    private final BlockingQueue<T> queue;
    private final Consumer<List<T>> batchInsertFunction;
    private final int batchSize;
    private final String processorName;

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        log.debug("[{}] - Worker thread for '{}' started.", threadName, processorName);
        List<T> batchList = new ArrayList<>(batchSize);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // take()는 큐가 비어있으면 스레드를 blocking 시키므로 효율적입니다.
                    T message = queue.take();
                    batchList.add(message);
                    // drainTo로 큐에 쌓인 데이터를 한번에 가져와 성능에 유리합니다.
                    queue.drainTo(batchList, batchSize - 1);

                    if (!batchList.isEmpty()) {
                        batchInsertFunction.accept(batchList);
                        batchList.clear(); // 성공 시에만 비웁니다.
                    }
                } catch (InterruptedException e) {
                    log.warn("[{}] - Worker thread for '{}' interrupted. Will process remaining {} items before shutting down.", threadName, processorName, batchList.size());
                    // **개선점 1: 루프를 중단하고 finally 블록으로 넘어가 남은 데이터를 처리합니다.**
                    Thread.currentThread().interrupt(); // 인터럽트 상태를 다시 설정합니다.
                } catch (Exception e) {
                    // **개선점 2: 실패한 배치의 정보를 로그에 남겨 추적을 돕습니다.**
                    log.error("[{}] - Error occurred during DB batch processing for '{}'. Failed batch size: {}. Error: {}",
                            threadName, processorName, batchList.size(), e.getMessage());
                    // 실패한 배치는 비워 다음 루프에 영향을 주지 않도록 합니다.
                    // (필요에 따라 이 데이터를 별도의 파일이나 데드-레터-큐에 저장하는 로직 추가 가능)
                    batchList.clear();

                    // DB 장애 시 과도한 재시도를 막기 위해 잠시 대기합니다.
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            // Thread.sleep() 중에 인터럽트가 발생할 경우를 대비합니다.
            log.warn("[{}] - Worker thread for '{}' was interrupted during sleep.", threadName, processorName);
            Thread.currentThread().interrupt();
        } finally {
            // **개선점 1: '우아한 종료' 로직**
            if (!batchList.isEmpty()) {
                log.info("[{}] - Shutting down. Processing final batch of {} items for '{}'.", threadName, batchList.size(), processorName);
                try {
                    batchInsertFunction.accept(batchList);
                } catch (Exception e) {
                    log.error("[{}] - Failed to process final batch for '{}' on shutdown. {} items may be lost. Error: {}",
                            threadName, processorName, batchList.size(), e.getMessage());
                }
            }
            log.info("[{}] - Worker thread for '{}' finished.", threadName, processorName);
        }
    }
}