package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import ch.dboeckli.spring.restmvc.entity.BeerOrderShipment;
import ch.dboeckli.spring.restmvc.entity.Customer;
import ch.dboeckli.spring.restmvc.mapper.BeerOrderMapper;
import ch.dboeckli.spring.restmvc.repository.BeerOrderRepository;
import ch.dboeckli.spring.restmvc.repository.BeerRepository;
import ch.dboeckli.spring.restmvc.repository.CustomerRepository;
import ch.dboeckli.spring.restmvc.rest.controller.NotFoundException;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderCreateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderUpdateDTO;
import ch.guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
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
@Slf4j
public class BeerOrderServiceJpaImpl implements BeerOrderService {

    public static final int DEFAULT_PAGE_SIZE = 25;

    public static final int MAX_PAGE_SIZE = 100;

    public static final int DEFAULT_PAGE_NUMBER = 0;

    private static final int DELETE_MAX_ATTEMPTS = 3;

    private static final long DELETE_RETRY_DELAY_MS = 100;

    private final BeerOrderRepository beerOrderRepository;

    private final BeerRepository beerRepository;

    private final CustomerRepository customerRepository;

    private final BeerOrderMapper beerOrderMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<BeerOrderDTO> listBeerOrders(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        Page<BeerOrder> beerOrderPages = beerOrderRepository.findAll(pageRequest);
        return beerOrderPages.map(beerOrderMapper::beerOrderToBeerOrderDto);
    }

    @Override
    public Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId) {
        return Optional.ofNullable(
                beerOrderMapper.beerOrderToBeerOrderDto(beerOrderRepository.findById(beerOrderId).orElse(null)));
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
    public BeerOrderDTO editBeerOrder(UUID beerOrderId, BeerOrderUpdateDTO beerOrderUpdateDTO) {
        BeerOrder beerOrder = beerOrderRepository.findById(beerOrderId).orElseThrow(NotFoundException::new);

        beerOrder.setCustomer(
                customerRepository.findById(beerOrderUpdateDTO.getCustomerId()).orElseThrow(NotFoundException::new));
        beerOrder.setCustomerRef(beerOrderUpdateDTO.getCustomerRef());

        beerOrderUpdateDTO.getBeerOrderLines().forEach(beerOrderLine -> {
            if (beerOrderLine.getBeerId() != null) {
                BeerOrderLine foundLine = beerOrder.getBeerOrderLines()
                    .stream()
                    .filter(beerOrderLine1 -> beerOrderLine1.getId().equals(beerOrderLine.getId()))
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
                foundLine
                    .setBeer(beerRepository.findById(beerOrderLine.getBeerId()).orElseThrow(NotFoundException::new));
                foundLine.setOrderQuantity(beerOrderLine.getOrderQuantity());
                foundLine.setQuantityAllocated(beerOrderLine.getQuantityAllocated());
            }
            else {
                beerOrder.getBeerOrderLines()
                    .add(BeerOrderLine.builder()
                        .beer(beerRepository.findById(beerOrderLine.getBeerId()).orElseThrow(NotFoundException::new))
                        .orderQuantity(beerOrderLine.getOrderQuantity())
                        .quantityAllocated(beerOrderLine.getQuantityAllocated())
                        .build());
            }
        });

        if (beerOrderUpdateDTO.getBeerOrderShipment() != null
                && beerOrderUpdateDTO.getBeerOrderShipment().getTrackingNumber() != null) {
            if (beerOrder.getBeerOrderShipment() == null) {
                beerOrder.setBeerOrderShipment(BeerOrderShipment.builder()
                    .trackingNumber(beerOrderUpdateDTO.getBeerOrderShipment().getTrackingNumber())
                    .build());
            }
            else {
                beerOrder.getBeerOrderShipment()
                    .setTrackingNumber(beerOrderUpdateDTO.getBeerOrderShipment().getTrackingNumber());
            }
        }

        BeerOrderDTO beerOrderDTO = beerOrderMapper.beerOrderToBeerOrderDto(beerOrderRepository.save(beerOrder));
        if (beerOrderUpdateDTO.getPaymentAmount() != null) {
            applicationEventPublisher.publishEvent(OrderPlacedEvent.builder().beerOrderDTO(beerOrderDTO).build());
        }

        return beerOrderDTO;
    }

    @Override
    public Optional<BeerOrderDTO> patchBeerOrder(UUID beerOrderId, BeerOrderDTO beerOrder) {
        // TODO: Implement
        log.error("Patch not Yet Implemented");
        return Optional.empty();
    }

    @Override
    public Boolean deleteBeerOrder(UUID beerOrderId) {
        OptimisticLockingFailureException lastException = null;

        for (int attempt = 1; attempt <= DELETE_MAX_ATTEMPTS; attempt++) {
            try {
                if (!beerOrderRepository.existsById(beerOrderId)) {
                    return false;
                }
                beerOrderRepository.deleteById(beerOrderId);
                return true;
            }
            catch (OptimisticLockingFailureException ex) {
                lastException = ex;
                log.warn("Optimistic locking conflict deleting beer order {} (attempt {}/{}); retrying", beerOrderId,
                        attempt, DELETE_MAX_ATTEMPTS);
                sleepBeforeDeleteRetry(attempt);
            }
        }

        throw new IllegalStateException(
                "Unable to delete beer order " + beerOrderId + " due to concurrent modification", lastException);
    }

    private void sleepBeforeDeleteRetry(int attempt) {
        try {
            Thread.sleep(DELETE_RETRY_DELAY_MS * attempt);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while retrying beer order deletion", ex);
        }
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int pageNumberParameter;
        int pageSizeParameter;

        if (pageNumber != null && pageNumber > 0) {
            pageNumberParameter = pageNumber - 1;
        }
        else {
            pageNumberParameter = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            pageSizeParameter = DEFAULT_PAGE_SIZE;
        }
        else {
            if (pageSize > MAX_PAGE_SIZE) {
                pageSizeParameter = MAX_PAGE_SIZE;
            }
            else {
                pageSizeParameter = pageSize;
            }
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "customerRef");
        return PageRequest.of(pageNumberParameter, pageSizeParameter, sort);
    }

}
