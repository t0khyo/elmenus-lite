package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.request.MenuCreateRequest;
import spring.practice.elmenus_lite.dto.response.MenuResponse;
import spring.practice.elmenus_lite.model.Menu;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    Menu toMenuEntity(MenuCreateRequest request);


    @Mapping(target = "restaurantId", source = "restaurant.id")
    MenuResponse toMenuResponse(Menu menu);
}
