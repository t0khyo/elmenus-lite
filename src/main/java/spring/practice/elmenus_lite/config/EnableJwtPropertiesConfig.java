package spring.practice.elmenus_lite.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import spring.practice.elmenus_lite.security.config.JwtConfigProperties;

@Configuration
@EnableConfigurationProperties(JwtConfigProperties.class)
public class EnableJwtPropertiesConfig {
}
