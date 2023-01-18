package co.kurrant.app.public_api.mapper.board;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.board.entity.CustomerService;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerServiceMapper {
    CustomerServiceDto toDto(CustomerService customerService);
}
