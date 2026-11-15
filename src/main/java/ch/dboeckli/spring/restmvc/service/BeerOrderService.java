package ch.dboeckli.spring.restmvc.service;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderCreateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Page<BeerOrderDTO> listBeerOrders(Integer page, Integer pageSize);

    Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId);

    BeerOrderDTO saveNewBeerOrder(BeerOrderCreateDTO newBeerOrder);

    BeerOrderDTO editBeerOrder(UUID beerOrderId, BeerOrderUpdateDTO beerOrderUpdateDTO);

    Optional<BeerOrderDTO> patchBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder);

    boolean deleteBeerOrder(UUID beerOrderId);

}
