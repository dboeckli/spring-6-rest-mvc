package ch.springframeworkguru.springrestmvc.controller;

import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.springframeworkguru.springrestmvc.service.BeerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static ch.springframeworkguru.springrestmvc.config.OpenApiConfiguration.SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("${controllers.beer-controller.request-path}")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
public class BeerController {

    private final BeerService beerService;
    @Value("${controllers.beer-controller.request-path}")
    private String requestPath;

    @DeleteMapping(value = "/deleteBeer/{beerId}")
    public ResponseEntity<@NonNull BeerDTO> deleteBeer(@PathVariable("beerId") UUID beerId) {
        if (!beerService.deleteBeer(beerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/listBeers")
    public ResponseEntity<@NonNull Page<@NonNull BeerDTO>> listBeers(@RequestParam(required = false) String beerName,
                                                   @RequestParam(required = false) BeerStyle beerStyle,
                                                   @RequestParam(required = false) Boolean showInventory,
                                                   @RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {
        return new ResponseEntity<>(beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = "/getBeerById/{beerId}")
    public ResponseEntity<@NonNull BeerDTO> getBeerById(@PathVariable("beerId") UUID beerId) {
        return new ResponseEntity<>(beerService.getBeerById(beerId).orElseThrow(NotFoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = "/createBeer")
    public ResponseEntity<@NonNull BeerDTO> createBeer(@Validated @RequestBody BeerDTO newBeer) {
        BeerDTO savedBeer = beerService.saveNewBeer(newBeer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", requestPath + "/getBeerById/" + savedBeer.getId().toString());

        return new ResponseEntity<>(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/editBeer/{beerId}")
    public ResponseEntity<@NonNull BeerDTO> editBeer(@Validated @RequestBody BeerDTO beer, @PathVariable("beerId") UUID beerId) {
        Optional<BeerDTO> updatedBeer = beerService.editBeer(beerId, beer);
        if (updatedBeer.isEmpty()) {
            throw new NotFoundException();
        } else {
            return new ResponseEntity<>(updatedBeer.get(), HttpStatus.OK);
        }
    }

    @PatchMapping(value = "/patchBeer/{beerId}")
    public ResponseEntity<@NonNull BeerDTO> patchBeer(@RequestBody BeerDTO beer, @PathVariable("beerId") UUID beerId) {
        Optional<BeerDTO> patchedBeer = beerService.patchBeer(beerId, beer);
        if (patchedBeer.isEmpty()) {
            throw new NotFoundException();
        } else {
            return new ResponseEntity<>(patchedBeer.get(), HttpStatus.OK);
        }
    }
}
