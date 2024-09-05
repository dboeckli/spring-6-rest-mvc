package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.model.Beer;
import ch.springframeworkguru.springrestmvc.service.BeerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${controllers.beer-controller.request-path}")
@Slf4j
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @DeleteMapping(value="/deleteBeer/{beerId}")
    public ResponseEntity<Beer> deleteBeer(@PathVariable("beerId") UUID beerId) {
        Beer deletedBeer = beerService.deleteBeer(beerId);
        return new ResponseEntity<>(deletedBeer, HttpStatus.OK);
    }

    @GetMapping(value="/listBears")
    public List<Beer> listBeers(){
        return beerService.listBeers();
    }

    @GetMapping(value = "/getBeerById/{beerId}")
    public Beer getBeerById(@PathVariable("beerId") UUID beerId){

        log.debug("Get Beer by Id - in controller");

        return beerService.getBeerById(beerId);
    }

    @PostMapping(value = "/createBeer")
    public ResponseEntity<Beer> createBeer(@RequestBody Beer newBeer) {
        Beer beer = beerService.saveNewBeer(newBeer);
        return new ResponseEntity<>(beer, HttpStatus.CREATED);
    }

    @PutMapping(value = "/editBeer/{beerId}")
    public ResponseEntity<Beer> editBeer(@RequestBody Beer beer, @PathVariable("beerId") UUID beerId) {
        Beer updatedBeer = beerService.editBeer(beerId, beer);
        return new ResponseEntity<>(updatedBeer, HttpStatus.OK);
    }

    @PatchMapping(value = "/patchBeer/{beerId}")
    public ResponseEntity<Beer> patchBeer(@RequestBody Beer beer, @PathVariable("beerId") UUID beerId) {
        Beer patchedBeer = beerService.patchBeer(beerId, beer);
        return new ResponseEntity<>(patchedBeer, HttpStatus.OK);
    }
}
