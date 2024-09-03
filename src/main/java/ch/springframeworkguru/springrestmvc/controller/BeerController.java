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

    @RequestMapping(value = "/createBear",
            method = RequestMethod.POST)
    public ResponseEntity<Beer> createBear(@RequestBody Beer newBeer) {
        Beer bear = beerService.saveNewBeer(newBeer);
        return new ResponseEntity<>(bear, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/editBea/{beerId}",
            method = RequestMethod.PUT)
    public ResponseEntity<Beer> editBear(@RequestBody Beer beer, @PathVariable("beerId") UUID beerId) {
        Beer updatedBeer = beerService.editBeer(beerId, beer);
        return new ResponseEntity<>(updatedBeer, HttpStatus.ACCEPTED);
    }


}
