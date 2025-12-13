package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.BeerOrderShipment;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderShipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderShipmentMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDtoToBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDto);

    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDto(BeerOrderShipment beerOrderShipment);
}
