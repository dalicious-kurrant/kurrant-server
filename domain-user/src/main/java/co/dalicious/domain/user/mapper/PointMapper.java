package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PointMapper {

    default PointPolicyResDto.ReviewPointPolicy toReviewPointPolicyResponseDto(ReviewPointPolicy reviewPointPolicy) {
        PointPolicyResDto.ReviewPointPolicy dto = new PointPolicyResDto.ReviewPointPolicy();

        dto.setMaxPrice(Integer.valueOf(reviewPointPolicy.getMaxPrice()));
        dto.setMinPrice(Integer.valueOf(reviewPointPolicy.getMinPrice()));
        dto.setImagePoint(BigDecimal.valueOf(Integer.parseInt(reviewPointPolicy.getImagePrice())));
        dto.setContentPoint(BigDecimal.valueOf(Integer.parseInt(reviewPointPolicy.getMaxPrice())));

        return dto;
    }
}
