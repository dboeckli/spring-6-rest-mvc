package ch.springframeworkguru.springrestmvc.mapper;

import ch.springframeworkguru.springrestmvc.entity.BeerOrderShipment;
import ch.springframeworkguru.springrestmvc.service.dto.create.BeerOrderShipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerOrderShipmentMapper {
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDtoToBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDto);

    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDto(BeerOrderShipment beerOrderShipment);
}
