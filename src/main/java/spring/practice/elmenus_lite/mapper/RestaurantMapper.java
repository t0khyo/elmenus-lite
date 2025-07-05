package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import spring.practice.elmenus_lite.dto.response.CategoryResponse;
import spring.practice.elmenus_lite.dto.request.RestaurantRequest;
import spring.practice.elmenus_lite.dto.response.RestaurantResponse;
import spring.practice.elmenus_lite.model.Restaurant;
import spring.practice.elmenus_lite.model.RestaurantCategory;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring" ,uses = {CategoryMapper.class})
public interface RestaurantMapper {


    @Mapping(target = "active", expression = "java(true)") // Default active to true on creation
    @Mapping(target = "restaurantDetails.description", source = "description")
    @Mapping(target = "restaurantDetails.phone", source = "phone")
    @Mapping(target = "restaurantDetails.openTime", source = "openTime")
    @Mapping(target = "restaurantDetails.closeTime", source = "closeTime")
    Restaurant toRestaurantEntity(RestaurantRequest request);


    @Mapping(target = "description", source = "restaurantDetails.description")
    @Mapping(target = "phone", source = "restaurantDetails.phone")
    @Mapping(target = "openTime", source = "restaurantDetails.openTime")
    @Mapping(target = "closeTime", source = "restaurantDetails.closeTime")
    @Mapping(target = "categories", source = "restaurantCategories", qualifiedByName = "mapRestaurantCategoriesToCategoryResponses")
    // averageRating will be set in the service layer as it's not a direct entity field
    @Mapping(target = "averageRating", ignore = true)
    RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    @Named("mapRestaurantCategoriesToCategoryResponses")
    default Set<CategoryResponse> mapRestaurantCategoriesToCategoryResponses(Set<RestaurantCategory> restaurantCategories) {
        if (restaurantCategories == null) {
            return null;
        }

         return restaurantCategories.stream()
                .map(rc ->
                                new CategoryResponse(rc.getCategory().getId(),rc.getCategory().getName())
                        )
                .collect(Collectors.toSet());
    }

}
