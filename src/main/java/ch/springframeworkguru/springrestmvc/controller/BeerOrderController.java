package ch.springframeworkguru.springrestmvc.controller;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderCreateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderUpdateDTO;
import ch.springframeworkguru.springrestmvc.service.BeerOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static ch.springframeworkguru.springrestmvc.config.OpenApiConfiguration.SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("${controllers.beer-order-controller.request-path}")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
public class BeerOrderController {

    @Value("${controllers.beer-order-controller.request-path}")
    private String requestPath;
    
    public static final String LIST_BEER_ORDERS = "/listBeerOrders";
    
    public static final String GET_BEER_ORDER = "/getBeerOrderyById";
    public static final String BEER_ORDER_ID_PATH_VARIABLE = "beerOrderId";
    
    public static final String GET_BEER_ORDER_BY_ID = GET_BEER_ORDER + "/" + "{" + BEER_ORDER_ID_PATH_VARIABLE + "}";
    
    public static final String CREATE_BEER_ORDER = "/createBeerOrder";
    
    public static final String UPDATE_BEER_ORDER = "/updateBeerOrder";
    public static final String UPDATE_BEER_ORDER_BY_ID = UPDATE_BEER_ORDER + "/" + "{" + BEER_ORDER_ID_PATH_VARIABLE + "}";
    
    public static final String DELETE_BEER_ORDER = "/deleteBeerOrder";
    public static final String DELETE_BEER_ORDER_BY_ID = DELETE_BEER_ORDER + "/" + "{" + BEER_ORDER_ID_PATH_VARIABLE + "}";

    private final BeerOrderService beerOrderService;

    @GetMapping(value=LIST_BEER_ORDERS)
    public ResponseEntity<Page<BeerOrderDTO>> listBeerOrder(@RequestParam(required = false) Integer pageNumber,
                                                            @RequestParam(required = false) Integer pageSize) {
        return new ResponseEntity<>(beerOrderService.listBeerOrders(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = GET_BEER_ORDER_BY_ID)
    public ResponseEntity<BeerOrderDTO> getBeerById(@PathVariable("beerOrderId") UUID beerOrderId){
        return new ResponseEntity<>(beerOrderService.getBeerOrderById(beerOrderId).orElseThrow(NotFoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = CREATE_BEER_ORDER)
    public ResponseEntity<BeerOrderDTO> createBeerOrder(@Validated @RequestBody BeerOrderCreateDTO newBeerOrder) {
        BeerOrderDTO savedBeerOrder = beerOrderService.saveNewBeerOrder(newBeerOrder);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", requestPath + GET_BEER_ORDER +"/" + savedBeerOrder.getId().toString());

        return new ResponseEntity<>(savedBeerOrder, headers, HttpStatus.CREATED);
    }

    @PutMapping(value = UPDATE_BEER_ORDER_BY_ID)
    public ResponseEntity<BeerOrderDTO> updateBeerOrder(@RequestBody BeerOrderUpdateDTO updateBeerOrderDTO, @PathVariable("beerOrderId") UUID beerOrderId) { 
        BeerOrderDTO updatedBeerOrder = beerOrderService.editBeerOrder(beerOrderId, updateBeerOrderDTO);
        return new ResponseEntity<>(updatedBeerOrder, HttpStatus.OK);
    }

    @DeleteMapping(value = DELETE_BEER_ORDER_BY_ID)
    public ResponseEntity<Void> deleteBeerOrder(@PathVariable("beerOrderId") UUID beerOrderId) { 
        if (!beerOrderService.deleteBeerOrder(beerOrderId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
