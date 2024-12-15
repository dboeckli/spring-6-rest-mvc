package ch.springframeworkguru.springrestmvc.mapper;

import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderShipmentDTO;
import ch.springframeworkguru.springrestmvc.entity.BeerOrderShipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderShipmentMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDtoToBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDto);

    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDto(BeerOrderShipment beerOrderShipment);
}
