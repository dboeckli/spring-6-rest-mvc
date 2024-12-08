package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.controller.NotFoundException;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.mapper.BeerOrderMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderCreateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerOrderServiceJpaImpl implements BeerOrderService {

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 1000;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    private final BeerOrderRepository beerOrderRepository;

    private final BeerRepository beerRepository;

    private final CustomerRepository customerRepository;
    
    private final BeerOrderMapper beerOrderMapper;
    
    @Override
    public Page<BeerOrderDTO> listBeerOrders(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        Page<BeerOrder> beerOrderPages = beerOrderRepository.findAll(pageRequest);
        return beerOrderPages.map(beerOrderMapper::beerOrderToBeerOrderDto);
    }

    @Override
    public Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId) {
        return Optional.ofNullable(beerOrderMapper
            .beerOrderToBeerOrderDto(beerOrderRepository
                .findById(beerOrderId)
                .orElse(null)));
    }

    @Override
    public BeerOrderDTO saveNewBeerOrder(BeerOrderCreateDTO beerOrderCreateDTO) {
        Customer customer = customerRepository.findById(beerOrderCreateDTO.getCustomerId())
            .orElseThrow(NotFoundException::new);

        Set<BeerOrderLine> beerOrderLines = new HashSet<>();

        beerOrderCreateDTO.getBeerOrderLines().forEach(beerOrderLine -> {
            beerOrderLines.add(BeerOrderLine.builder()
                .beer(beerRepository.findById(beerOrderLine.getBeerId()).orElseThrow(NotFoundException::new))
                .orderQuantity(beerOrderLine.getOrderQuantity())
                .build());
        });

        BeerOrder savedBeerOrder = beerOrderRepository.save(BeerOrder.builder()
            .customer(customer)
            .beerOrderLines(beerOrderLines)
            .customerRef(beerOrderCreateDTO.getCustomerRef())
            .build());
        
        return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
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

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int pageNumberParameter;
        int pageSizeParameter;

        if (pageNumber != null && pageNumber > 0) {
            pageNumberParameter = pageNumber - 1;
        } else {
            pageNumberParameter = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            pageSizeParameter = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > MAX_PAGE_SIZE) {
                pageSizeParameter = MAX_PAGE_SIZE;
            } else {
                pageSizeParameter = pageSize;
            }
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "customerRef");
        return PageRequest.of(pageNumberParameter, pageSizeParameter, sort);
    }
}
