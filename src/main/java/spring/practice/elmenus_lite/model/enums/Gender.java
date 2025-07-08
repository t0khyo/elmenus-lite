package spring.practice.elmenus_lite.model.enums;

import lombok.Getter;

/**
 * Gender codes based on ISO/IEC 5218:
 * 0 = Unspecified
 * 1 = Male
 * 2 = Female
 */
@Getter
public enum Gender {
    UNSPECIFIED(0),
    MALE(1),
    FEMALE(2);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    public static Gender fromCode(int code) {
        for (Gender gender : values()) {
            if (gender.code == code) return gender;
        }
        return UNSPECIFIED; // default fallback
    }
}
