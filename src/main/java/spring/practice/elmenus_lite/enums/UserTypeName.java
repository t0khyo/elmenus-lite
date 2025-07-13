package spring.practice.elmenus_lite.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTypeName {
    CUSTOMER("customer");

    private final String name;
}
