package ngoc.connect_gemini_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "GOOGLE_GENAI_USE_VERTEXAI=True",
        "GOOGLE_CLOUD_PROJECT=spring-boot-smart-blog", // Thay bằng Project ID của bạn
        "GOOGLE_CLOUD_LOCATION=us-central1"
})
class ConnectGeminiApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
