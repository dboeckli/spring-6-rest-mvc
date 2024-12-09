package ch.springframeworkguru.springrestmvc.mapper;

import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderLineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderLineMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDTO beerOrderLineDto);

    BeerOrderLineDTO beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
}
