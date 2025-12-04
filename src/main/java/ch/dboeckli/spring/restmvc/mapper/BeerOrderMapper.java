package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderMapper {
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDTO beerOrderDto);

    BeerOrderDTO beerOrderToBeerOrderDto(BeerOrder beerOrder);
}
