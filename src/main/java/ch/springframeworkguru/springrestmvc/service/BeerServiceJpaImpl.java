package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.mapper.BeerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class BeerServiceJpaImpl implements BeerService {

    BeerRepository beerRepository;

    BeerMapper beerMapper;

    public BeerServiceJpaImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    public List<BeerDTO> listBeers() {
        return beerRepository
                .findAll()
                .stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper
                .beerToBeerDto(beerRepository
                        .findById(id)
                        .orElse(null)));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO newBeer) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(newBeer)));
    }

    @Override
    public Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer) {
        beerRepository.findById(beerId).ifPresent(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setPrice(beer.getPrice());
            beerMapper.beerToBeerDto(beerRepository.save(foundBeer));
        });
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }

    @Override
    public Boolean deleteBeer(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public BeerDTO patchBeer(UUID beerId, BeerDTO beer) {
        return null;
    }
}
