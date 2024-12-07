package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BeerOrderServiceImpl implements BeerOrderService {
    @Override
    public Page<BeerOrderDTO> listBeerOrders(Integer page, Integer pageSize) {
        // TODO: Implement
        return Page.empty();
    }

    @Override
    public Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId) {
        // TODO: Implement
        return Optional.of(BeerOrderDTO
            .builder()
                .id(beerOrderId)
                .customerRef("todo")
            .build());
    }

    @Override
    public BeerOrderDTO saveNewBeerOrder(BeerOrderDTO newBeerOrder) {
        // TODO: Implement
        return null;
    }

    @Override
    public Optional<BeerOrderDTO> editBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder) {
        // TODO: Implement
        return Optional.empty();
    }

    @Override
    public Boolean deleteBeerOrderDTO(UUID beerOrderId) {
        // TODO: Implement
        return null;
    }

    @Override
    public Optional<BeerOrderDTO> patchBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder) {
        // TODO: Implement
        return Optional.empty();
    }
}
