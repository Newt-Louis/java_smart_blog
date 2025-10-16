package ngoc.connect_gemini_api.config;

import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class GcsEmulatorConfig {

    @Bean
    public Storage storage() {
        return StorageOptions.newBuilder()
                .setHost("http://localhost:4443") // Emulator endpoint
                .setProjectId("spring-boot-smart-blog")
                .setCredentials(NoCredentials.getInstance()) // Không cần auth
                .build()
                .getService();
    }
}
