package ngoc.connect_gemini_api.config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Bean
    public Client geminiClient() {
        return Client.builder()
                .vertexAI(true) // Báo cho client dùng ADC thay vì API Key
                .httpOptions(HttpOptions.builder().apiVersion("v1").build())
                .build();
    }
}