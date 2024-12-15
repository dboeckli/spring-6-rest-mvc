package ch.springframeworkguru.springrestmvc.mapper;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderLineMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDTO beerOrderLineDto);

    BeerOrderLineDTO beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
}
