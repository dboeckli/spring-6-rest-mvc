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
@RequestMapping("/api/v1/beer")
@Slf4j
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @DeleteMapping(value="/deleteBeer/{beerId}")
    public ResponseEntity<Beer> deleteBeer(@PathVariable("beerId") UUID beerId) {
        Beer deletedBeer = beerService.deleteBeer(beerId);
        return new ResponseEntity<>(deletedBeer, HttpStatus.FOUND);
    }

    @RequestMapping(value="/listBears",
            method = RequestMethod.GET)
    public List<Beer> listBeers(){
        return beerService.listBeers();
    }

    @RequestMapping(value = "/getBeerById/{beerId}",
                    method = RequestMethod.GET)
    public Beer getBeerById(@PathVariable("beerId") UUID beerId){

        log.debug("Get Beer by Id - in controller");

        return beerService.getBeerById(beerId);
    }

    @RequestMapping(value = "/createBeer",
            method = RequestMethod.POST)
    public ResponseEntity<Beer> createBeer(@RequestBody Beer newBeer) {
        Beer bear = beerService.saveNewBeer(newBeer);
        return new ResponseEntity<>(bear, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/editBeer/{beerId}",
            method = RequestMethod.PUT)
    public ResponseEntity<Beer> editBeer(@RequestBody Beer beer, @PathVariable("beerId") UUID beerId) {
        Beer updatedBeer = beerService.editBeer(beerId, beer);
        return new ResponseEntity<>(updatedBeer, HttpStatus.NO_CONTENT);
    }


}
