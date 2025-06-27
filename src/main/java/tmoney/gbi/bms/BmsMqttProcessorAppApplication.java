package tmoney.gbi.bms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
public class BmsMqttProcessorAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmsMqttProcessorAppApplication.class, args);
    }

}
