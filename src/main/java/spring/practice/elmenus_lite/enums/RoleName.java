package spring.practice.elmenus_lite.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
    ROLE_CUSTOMER("ROLE_CUSTOMER");

    private final String name;

}
