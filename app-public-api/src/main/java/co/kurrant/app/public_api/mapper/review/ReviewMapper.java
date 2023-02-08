package co.kurrant.app.public_api.mapper.review;

import co.dalicious.domain.review.entity.Reviews;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReviewMapper {

    Reviews toReviewEntity(ReviewDto reviewDto);
}
