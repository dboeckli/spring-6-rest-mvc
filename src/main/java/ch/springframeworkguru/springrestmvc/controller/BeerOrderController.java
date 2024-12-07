package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.service.BeerOrderService;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${controllers.beer-order-controller.request-path}")
@Slf4j
@RequiredArgsConstructor
public class BeerOrderController {

    @Value("${controllers.beer-order-controller.request-path}")
    private String requestPath;
    
    public static final String LIST_BEER_ORDERS = "/listBeerOrders";
    public static final String GET_BEER_ORDER_BY_ID = "/getBeerOrderyById/{beerOrderId}";

    private final BeerOrderService beerOrderService;

    @GetMapping(value=LIST_BEER_ORDERS)
    public ResponseEntity<Page<BeerOrderDTO>> listBeerOrder(@RequestParam(required = false) Integer pageNumber,
                                                             @RequestParam(required = false) Integer pageSize) {
        return new ResponseEntity<>(beerOrderService.listBeerOrders(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = GET_BEER_ORDER_BY_ID)
    public ResponseEntity<BeerOrderDTO> getBeerById(@PathVariable("beerOrderId") UUID beerOrderId){
        return new ResponseEntity<>(beerOrderService.getBeerOrderById(beerOrderId).orElseThrow(NotfoundException::new), HttpStatus.OK);
    }
}
