package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import ch.springframeworkguru.springrestmvc.service.dto.create.BeerOrderCreateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.update.BeerOrderUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Page<BeerOrderDTO> listBeerOrders(Integer page, Integer pageSize);

    Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId);

    BeerOrderDTO saveNewBeerOrder(BeerOrderCreateDTO newBeerOrder);

    BeerOrderDTO editBeerOrder(UUID beerOrderId, BeerOrderUpdateDTO BeerOrderUpdateDTO);

    Optional<BeerOrderDTO> patchBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder);

    Boolean deleteBeerOrder(UUID beerOrderId);
}
