package tmoney.gbi.bms.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private DbWorker dbWorker = new DbWorker();
    private Queue queue = new Queue();

    @Getter
    @Setter
    public static class DbWorker {
        private int threads;
        private int batchSize;
    }

    @Getter
    @Setter
    public static class Queue {
        private int size;
    }

}
