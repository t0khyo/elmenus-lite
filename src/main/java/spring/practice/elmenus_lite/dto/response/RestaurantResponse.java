package spring.practice.elmenus_lite.dto.response;

import lombok.*;

import java.sql.Time;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {
    private Integer id;
    private String name;
    private String description;
    private String phone;
    private Time openTime;
    private Time closeTime;
    private Boolean active;
    private Double averageRating;
    private Integer reviewCount;
    private Set<CategoryResponse> categories;
}
