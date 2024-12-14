package ch.springframeworkguru.springrestmvc.bootstrap;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.BeerCsvService;
import ch.springframeworkguru.springrestmvc.service.dto.BeerCSVRecord;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;
    private final BeerOrderRepository beerOrderRepository;

    private final CacheManager cacheManager;

    @Override
    @Transactional
    // Eviction does not work. We are using explicitly the cachemanager
    @Caching(evict = {
        @CacheEvict(cacheNames = "customerCache"),
        @CacheEvict(cacheNames = "customerListCache"),
        @CacheEvict(cacheNames = "beerCache"),
        @CacheEvict(cacheNames = "beerListCache")
    })
    public void run(String... args) throws Exception {
        clearCache();
        loadBeerData();
        loadBeerCsvData();
        loadCustomerData();
        loadOrderData();
    }

    private void clearCache() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    private void loadBeerCsvData() throws FileNotFoundException {
        if (beerRepository.count() < 10) {
            File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
            List<BeerCSVRecord> beerCSVRecordList = beerCsvService.convertCSV(file);

            for (BeerCSVRecord beerCSVRecord : beerCSVRecordList) {
                BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                    case "American Pale Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" -> BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };

                Beer beer = Beer.builder()
                    .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                    .beerStyle(beerStyle)
                    .price(BigDecimal.TEN)
                    .upc(beerCSVRecord.getRow().toString())
                    .quantityOnHand(beerCSVRecord.getCount())
                    .build();

                beerRepository.save(beer);
            }
        }
        List<Beer> beers = beerRepository.findAll();
        log.info("Created via csv list {} beers", beers.size());
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer1 = Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .build();

            Beer beer2 = Beer.builder()
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

            Beer beer3 = Beer.builder()
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .build();

            beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));

            List<Beer> beers = beerRepository.findAll();
            log.info("Created {} Beers: {}", beers.size(), beers);
        }

    }

    private void loadCustomerData() {

        if (customerRepository.count() == 0) {
            Customer customer1 = Customer.builder()
                .name("Customer 1")
                .build();

            Customer customer2 = Customer.builder()
                .name("Customer 2")
                .build();

            Customer customer3 = Customer.builder()
                .name("Customer 3")
                .build();

            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));

            List<Customer> customers = customerRepository.findAll();
            log.info("Created {} Customers: {}", customers.size(), customers);
        }
    }

    private void loadOrderData() {
        if (beerOrderRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<Beer> beers = beerRepository.findAll();

            Iterator<Beer> beerIterator = beers.iterator();

            customers.forEach(customer -> {

                beerOrderRepository.save(BeerOrder.builder()
                    .customer(customer)
                    .customerRef("bootstrap1")
                    .beerOrderLines(Set.of(
                        BeerOrderLine.builder()
                            .beer(beerIterator.next())
                            .orderQuantity(1)
                            .build(),
                        BeerOrderLine.builder()
                            .beer(beerIterator.next())
                            .orderQuantity(2)
                            .build()
                    )).build());

                beerOrderRepository.save(BeerOrder.builder()
                    .customer(customer)
                    .customerRef("bootstrap2")
                    .beerOrderLines(Set.of(
                        BeerOrderLine.builder()
                            .beer(beerIterator.next())
                            .orderQuantity(1)
                            .build(),
                        BeerOrderLine.builder()
                            .beer(beerIterator.next())
                            .orderQuantity(2)
                            .build()
                    )).build());
            });

            beerOrderRepository.flush();
            List<BeerOrder> orders = beerOrderRepository.findAll();
            log.info("Created {} BeerOrders: {}", orders.size(), orders);
        }
    }
}
