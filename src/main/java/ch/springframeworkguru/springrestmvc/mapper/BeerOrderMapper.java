package ch.springframeworkguru.springrestmvc.mapper;

import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderMapper {
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDTO beerOrderDto);

    BeerOrderDTO beerOrderToBeerOrderDto(BeerOrder beerOrder);
}
