package ch.springframeworkguru.springrestmvc.mapper;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderMapper {
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDTO beerOrderDto);

    BeerOrderDTO beerOrderToBeerOrderDto(BeerOrder beerOrder);
}
