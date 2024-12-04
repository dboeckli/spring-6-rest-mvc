package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.mapper.BeerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Slf4j
public class BeerServiceJpaImpl implements BeerService {

    BeerRepository beerRepository;

    BeerMapper beerMapper;
    
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 1000;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    public BeerServiceJpaImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    @Cacheable(cacheNames = "beerListCache")
    public Page<BeerDTO> listBeers(String beerName,
                                   BeerStyle beerStyle,
                                   Boolean showInventory,
                                   Integer pageNumber,
                                   Integer pageSize) {
        
        log.info("Service: listBeers");
        
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        
        Page<Beer> beerPages;
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
    
    private Page<Beer> listBeerByName(String beerName, PageRequest pageRequest) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageRequest);
    }

    private Page<Beer> listBeerByStyle(BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
    }

    private Page<Beer> listBeerByNameAndBeerStyle(String beerName, BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(beerStyle, "%" + beerName + "%", pageRequest);
    }

    @Override
    @Cacheable(cacheNames = "beerCache")
    public Optional<BeerDTO> getBeerById(UUID id) {
        log.info("Service getBeerById");
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
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(newBeer)));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer) {
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
            beerMapper.beerToBeerDto(beerRepository.save(foundBeer));
        });
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }


    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer) {
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
            beerMapper.beerToBeerDto(beerRepository.save(foundBeer));
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
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

}
