package tmoney.gbi.bms.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tmoney.gbi.bms.mapper.CommonInsertMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SampleService {

    public <T> void insert(CommonInsertMapper mapper , T t){
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> mapper.insert(t));
        }
    }

}
