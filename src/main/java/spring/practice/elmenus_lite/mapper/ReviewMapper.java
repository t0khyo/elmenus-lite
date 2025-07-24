package spring.practice.elmenus_lite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.practice.elmenus_lite.dto.request.ReviewRequest;
import spring.practice.elmenus_lite.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toReviewEntity(ReviewRequest request);

}
