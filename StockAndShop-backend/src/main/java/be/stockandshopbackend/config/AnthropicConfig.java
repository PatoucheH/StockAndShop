package be.stockandshopbackend.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Manually instantiates the Anthropic SDK client using the OkHttp transport.
 * This sidesteps Spring AI's autoconfiguration (which uses a different client abstraction)
 * so the app can call the Anthropic SDK directly for full control over model params.
 */
@Configuration
public class AnthropicConfig {

    @Value("${spring.ai.anthropic.api-key}")
    private String apiKey;

    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
