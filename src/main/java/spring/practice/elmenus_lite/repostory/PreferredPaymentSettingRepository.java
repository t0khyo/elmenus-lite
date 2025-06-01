package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.practice.elmenus_lite.model.PreferredPaymentSetting;

public interface PreferredPaymentSettingRepository extends JpaRepository<PreferredPaymentSetting, Integer> {
}