package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.event.events.BeerCreatedEvent;
import ch.springframeworkguru.springrestmvc.event.events.BeerDeleteEvent;
import ch.springframeworkguru.springrestmvc.event.events.BeerPatchEvent;
import ch.springframeworkguru.springrestmvc.mapper.BeerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import static ch.springframeworkguru.springrestmvc.service.BeerServiceJpaImpl.MAX_PAGE_SIZE;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class BeerControllerIT {

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${controllers.beer-controller.request-path}")
    private String requestPath;

    @Autowired
    WebApplicationContext webApplicationContext;

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> {
            jwt.claims(claims -> {
                    claims.put("scope", "message.read");
                    claims.put("scope", "message.write");
                })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5l));
        });

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testDeleteBeerById() {
        Beer beer = beerRepository.findAll().getFirst();
        beerController.deleteBeer(beer.getId());

        assertFalse(beerRepository.findById(beer.getId()).isPresent());
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testDeleteBeerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> beerController.deleteBeer(UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testSaveBeer() {
        BeerDTO newBeerDTO = BeerDTO.builder()
                .beerName("verynewBeer")
                .beerStyle(BeerStyle.GOSE)
                .upc("upc")
                .price(BigDecimal.valueOf(55))
                .quantityOnHand(2)
                .build();

        BeerDTO createdBeer = beerController.createBeer(newBeerDTO).getBody();
        assertAll(() -> {
            assertNotNull(createdBeer);
            assertEquals("verynewBeer", createdBeer.getBeerName());
            assertNotNull(beerController.getBeerById(createdBeer.getId()));
        });
    }

    @Test
    void testSaveBeerWithMockMVC() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        
        BeerDTO newBeerDTO = BeerDTO.builder()
            .beerName("verynewBeer")
            .beerStyle(BeerStyle.GOSE)
            .upc("upc")
            .price(BigDecimal.valueOf(55))
            .quantityOnHand(2)
            .build();

        MvcResult result = mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBeerDTO)))
            .andExpect(status().isCreated())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerDTO beerDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertAll(() -> {
            assertNotNull(beerDTO);
            assertEquals("verynewBeer", beerDTO.getBeerName());
            assertEquals(1, applicationEvents.stream(BeerCreatedEvent.class).count());
            assertEquals("verynewBeer", applicationEvents.stream(BeerCreatedEvent.class).findFirst().get().getBeer().getBeerName());
            assertEquals("messaging-client", applicationEvents.stream(BeerCreatedEvent.class).findFirst().get().getAuthentication().getName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testUpdateExistingBeer() {
        Beer beer = beerRepository.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);

        beerDTO.setBeerName("UPDATED BEER");
        beerDTO.setId(null);
        beerDTO.setVersion(null);

        BeerDTO editedBeer = beerController.editBeer(beerDTO, beer.getId()).getBody();
        assertAll(() -> {
            assert editedBeer != null;
            assertEquals("UPDATED BEER", editedBeer.getBeerName());
        });
    }

    @Test
    void testUpdateBeerWithMockMVC() throws Exception {
        Beer beerToUpdate = beerRepository.findAll().getFirst();
        BeerDTO beerToUpdateDTO = beerMapper.beerToBeerDto(beerToUpdate);
        beerToUpdateDTO.setBeerName("UPDATED BEER");
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        MvcResult result = mockMvc.perform(put(requestPath + "/editBeer/{beerId}", beerToUpdate.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerToUpdateDTO)))
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerDTO beerDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertAll(() -> {
            assertNotNull(beerDTO);
            assertEquals("UPDATED BEER", beerDTO.getBeerName());
            assertEquals(1, applicationEvents.stream(BeerCreatedEvent.class).count());
            assertEquals("UPDATED BEER", applicationEvents.stream(BeerCreatedEvent.class).findFirst().get().getBeer().getBeerName());
            assertEquals("messaging-client", applicationEvents.stream(BeerCreatedEvent.class).findFirst().get().getAuthentication().getName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testUpdateExistingBeerButNotFound() {
        assertThrows(NotfoundException.class, () -> 
            beerController.editBeer(BeerDTO.builder().build(), UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testPatchBeer() {
        Beer givenBeer = beerRepository.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(givenBeer);
        beerDTO.setBeerName("Well");

        BeerDTO editedBeerDTO = beerController.patchBeer(beerDTO, beerDTO.getId()).getBody();

        assertAll(() -> {
            assertNotNull(editedBeerDTO);
            assertEquals("Well", editedBeerDTO.getBeerName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testPatchBeerDoesNotExist() {
        BeerDTO beerDTO = BeerDTO.builder().build();

        assertThrows(NotfoundException.class, () -> beerController.patchBeer(beerDTO, UUID.randomUUID()));
    }

    @Test
    void testPatchBeerWithMockMVC() throws Exception {
        Beer beerToUpdate = beerRepository.findAll().getFirst();
        BeerDTO beerToUpdateDTO = beerMapper.beerToBeerDto(beerToUpdate);
        beerToUpdateDTO.setBeerName("PATCHED BEER");

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        MvcResult result = mockMvc.perform(patch(requestPath + "/patchBeer/{beerId}", beerToUpdate.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerToUpdateDTO)))
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerDTO beerDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertAll(() -> {
            assertNotNull(beerDTO);
            assertEquals("PATCHED BEER", beerDTO.getBeerName());
            assertEquals(1, applicationEvents.stream(BeerPatchEvent.class).count());
            assertEquals("PATCHED BEER", applicationEvents.stream(BeerPatchEvent.class).findFirst().get().getBeer().getBeerName());
            assertEquals("messaging-client", applicationEvents.stream(BeerPatchEvent.class).findFirst().get().getAuthentication().getName());
        });
    }

    @Test
    void testListBeers() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, null, null, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(2413, beersDtos.getTotalElements()); 
            assertEquals(97, beersDtos.getTotalPages());
            assertEquals(25, beersDtos.getNumberOfElements());
            assertEquals(0, beersDtos.getNumber());
        });
    }

    @Test
    void testListBeersWithMaxPageSize() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, null, null, null, MAX_PAGE_SIZE+1);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(2413, beersDtos.getTotalElements());
            assertEquals(3, beersDtos.getTotalPages());
            assertEquals(MAX_PAGE_SIZE, beersDtos.getNumberOfElements());
            assertEquals(0, beersDtos.getNumber());
        });
    }

    @Test
    void testListBeerByName() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("IPA", null, null, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(336, beersDtos.getTotalElements()); 
        });
    }

    @Test
    void testListBeersByName() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        
        mockMvc.perform(get(requestPath + "/listBeers")
                //.with(httpBasic(username, password))
                .with(jwtRequestPostProcessor)
                .queryParam("beerName", "IPA")
                .queryParam("pageSize", "800"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(336)));
    }

    @Test
    void testListBeersByNameWithWrongCredentials() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        mockMvc.perform(get(requestPath + "/listBeers")
                .with(httpBasic("wrongwusername", "wrongpassword"))
                .queryParam("beerName", "IPA")
                .queryParam("pageSize", "800"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testListBeerByNamePage2() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("IPA", null, null, 2, 50);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(336, beersDtos.getTotalElements());
            assertEquals(1, beersDtos.getNumber());
            assertEquals(7, beersDtos.getTotalPages());
            assertEquals(50, beersDtos.getNumberOfElements());
        });
    }

    @Test
    void testListBeerByStyleAndBeerName() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("IPA", BeerStyle.IPA, null, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(310, beersDtos.getTotalElements());
        });
    }

    @Test
    void testListBeerNameWithShowInventory() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, true, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.getTotalElements());
            assertEquals("Ninja Porter", beersDtos.getContent().getFirst().getBeerName());
            assertEquals(140, beersDtos.getContent().getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerNameWithoutShowInventory() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, false, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.getTotalElements());
            assertEquals("Ninja Porter", beersDtos.getContent().getFirst().getBeerName());
            assertNull(beersDtos.getContent().getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerNameWithNullShowInventory() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, null, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.getTotalElements());
            assertEquals("Ninja Porter", beersDtos.getContent().getFirst().getBeerName());
            assertNull(beersDtos.getContent().getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerByStyle() {
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, BeerStyle.IPA, null, null, null);
        Page<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(548, beersDtos.getTotalElements());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testEmptyListBeer() {
        beerRepository.deleteAll();

        // we need to clear the cache, because the deleteAll (in the repository class) does not evict the cache
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        
        ResponseEntity<Page<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, null, null, null, null);
        Page<BeerDTO> beerDtos = beersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDtos != null;
                    assertEquals(0, beerDtos.getTotalElements());
                }
        );
    }

    @Test
    @Transactional
    @Rollback(true) // we roll back to deletion to assure that the other tests are not failing
    void testDeleteBeerWithMockMVC() throws Exception {
        Beer beerToDelete = beerRepository.findAll().getFirst();

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        mockMvc.perform(delete(requestPath + "/deleteBeer/{beerId}", beerToDelete.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertAll(() -> {
            assertEquals(1, applicationEvents.stream(BeerDeleteEvent.class).count());
            assertNull(applicationEvents.stream(BeerDeleteEvent.class).findFirst().get().getBeer().getBeerName());
            assertEquals("messaging-client", applicationEvents.stream(BeerDeleteEvent.class).findFirst().get().getAuthentication().getName());
        });
    }

    @Test
    void testGetBeerById() {
        UUID givenBeerId = beerRepository.findAll().getFirst().getId();

        ResponseEntity<BeerDTO> beerDTOResponseEntity = beerController.getBeerById(givenBeerId);
        BeerDTO beerDTO = beerDTOResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDTO != null;
                    assertEquals(givenBeerId, beerDTO.getId());
                }
        );
    }

    @Test
    void testGetBeerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }
}
