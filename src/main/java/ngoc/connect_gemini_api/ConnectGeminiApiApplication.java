package ngoc.connect_gemini_api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@MapperScan("ngoc.connect_gemini_api.repository")
public class ConnectGeminiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectGeminiApiApplication.class, args);
	}

}
