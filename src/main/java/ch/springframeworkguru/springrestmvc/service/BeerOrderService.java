package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Page<BeerOrderDTO> listBeerOrders(Integer page, Integer pageSize);

    Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId);

    BeerOrderDTO saveNewBeerOrder(BeerOrderDTO newBeerOrder);

    Optional<BeerOrderDTO> editBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder);

    Boolean deleteBeerOrderDTO(UUID beerOrderId);

    Optional<BeerOrderDTO> patchBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder);
    
}
