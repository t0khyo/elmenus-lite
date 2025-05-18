package spring.practice.elmenus_lite.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: should be replaced with actual user info
        return Optional.of("system");
    }
}
