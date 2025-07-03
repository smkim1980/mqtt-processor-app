package tmoney.gbi.bms.config.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class DbBatchProcessor<T> {

    private final BlockingQueue<T> queue;
    private final Consumer<List<T>> batchInsertFunction;
    private final int batchSize;

    public void processMessages() {
        List<T> batchList = new ArrayList<>(batchSize);
        String threadName = Thread.currentThread().getName();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                T message = queue.take();
                batchList.add(message);
                queue.drainTo(batchList, batchSize - 1);

                if (!batchList.isEmpty()) {
                    batchInsertFunction.accept(batchList);
                    batchList.clear();
                }
            } catch (InterruptedException e) {
                log.warn("[{}] Worker thread interrupted.", threadName);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("[{}] Error occurred during DB batch processing", threadName, e);
                // 에러 발생 시 잠시 대기하여 반복적인 에러 방지
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("[{}] Worker thread finished.", threadName);
    }
}
