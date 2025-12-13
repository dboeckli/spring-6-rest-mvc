package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderLineMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDTO beerOrderLineDto);

    BeerOrderLineDTO beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
}
