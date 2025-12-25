package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.event.events.BeerCreatedEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerDeleteEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerPatchEvent;
import ch.dboeckli.spring.restmvc.mapper.BeerMapper;
import ch.dboeckli.spring.restmvc.repository.BeerRepository;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Slf4j
public class BeerServiceJpaImpl implements BeerService {

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    public BeerServiceJpaImpl(BeerRepository beerRepository, BeerMapper beerMapper, CacheManager cacheManager, ApplicationEventPublisher eventPublisher) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Cacheable(cacheNames = "beerListCache")
    public Page<@NonNull BeerDTO> listBeers(String beerName,
                                            BeerStyle beerStyle,
                                            Boolean showInventory,
                                            Integer pageNumber,
                                            Integer pageSize) {

        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        Page<@NonNull Beer> beerPages;
        if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerPages = listBeerByNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if (StringUtils.hasText(beerName)) {
            beerPages = listBeerByName(beerName, pageRequest);
        } else if (beerStyle != null) {
            beerPages = listBeerByStyle(beerStyle, pageRequest);
        } else {
            beerPages = beerRepository.findAll(pageRequest);
        }

        if (showInventory == null || !showInventory) {
            beerPages.forEach(beer -> beer.setQuantityOnHand(null));
        }
        return beerPages.map(beerMapper::beerToBeerDto);
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

        Sort sort = Sort.by(Sort.Direction.ASC, "beerName");

        return PageRequest.of(pageNumberParameter, pageSizeParameter, sort);
    }

    private Page<@NonNull Beer> listBeerByName(String beerName, PageRequest pageRequest) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageRequest);
    }

    private Page<@NonNull Beer> listBeerByStyle(BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
    }

    private Page<@NonNull Beer> listBeerByNameAndBeerStyle(String beerName, BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(beerStyle, "%" + beerName + "%", pageRequest);
    }

    @Override
    @Cacheable(cacheNames = "beerCache")
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper
            .beerToBeerDto(beerRepository
                .findById(id)
                .orElse(null)));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public BeerDTO saveNewBeer(BeerDTO newBeer) {
        if (cacheManager.getCache("beerListCache") != null) {
            cacheManager.getCache("beerListCache").clear();
        }

        Beer savedBeer = beerRepository.save(beerMapper.beerDtoToBeer(newBeer));

        // publish an event
        log.info("Current Thread name: " + Thread.currentThread().getName());
        log.info("Current Thread ID: " + Thread.currentThread().threadId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        eventPublisher.publishEvent(new BeerCreatedEvent(savedBeer, authentication));

        return beerMapper.beerToBeerDto(savedBeer);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer) {
        clearCache(beerId);

        beerRepository.findById(beerId).ifPresent(foundBeer -> {
            if (StringUtils.hasText(beer.getBeerName())) {
                foundBeer.setBeerName(beer.getBeerName());
            }
            if (beer.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beer.getBeerStyle());
            }
            if (StringUtils.hasText(beer.getUpc())) {
                foundBeer.setUpc(beer.getUpc());
            }
            if (beer.getPrice() != null) {
                foundBeer.setPrice(beer.getPrice());
            }
            Beer updatedBeer = beerRepository.save(foundBeer);

            // publish an event
            log.info("Current Thread name: " + Thread.currentThread().getName());
            log.info("Current Thread ID: " + Thread.currentThread().threadId());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            eventPublisher.publishEvent(new BeerCreatedEvent(updatedBeer, authentication));
        });
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }


    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer) {
        clearCache(beerId);

        beerRepository.findById(beerId).ifPresent(foundBeer -> {
            if (StringUtils.hasText(beer.getBeerName())) {
                foundBeer.setBeerName(beer.getBeerName());
            }
            if (beer.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beer.getBeerStyle());
            }
            if (StringUtils.hasText(beer.getUpc())) {
                foundBeer.setUpc(beer.getUpc());
            }
            if (beer.getPrice() != null) {
                foundBeer.setPrice(beer.getPrice());
            }
            Beer patchedBeer = beerRepository.save(foundBeer);
            // publish an event
            log.info("Current Thread name: " + Thread.currentThread().getName());
            log.info("Current Thread ID: " + Thread.currentThread().threadId());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            eventPublisher.publishEvent(new BeerPatchEvent(patchedBeer, authentication));
        });
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public Boolean deleteBeer(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            clearCache(beerId);
            beerRepository.deleteById(beerId);

            // publish an event
            log.info("Current Thread name: " + Thread.currentThread().getName());
            log.info("Current Thread ID: " + Thread.currentThread().threadId());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            eventPublisher.publishEvent(new BeerDeleteEvent(Beer.builder().id(beerId).build(), authentication));

            return true;
        }
        return false;
    }

    private void clearCache(UUID beerId) {
        if (cacheManager.getCache("beerCache") != null) {
            cacheManager.getCache("beerCache").evict(beerId);
        }
        if (cacheManager.getCache("beerListCache") != null) {
            cacheManager.getCache("beerListCache").clear();
        }
    }

}
