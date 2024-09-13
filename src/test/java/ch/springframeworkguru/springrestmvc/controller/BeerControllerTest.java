package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.service.BeerService;
import ch.springframeworkguru.springrestmvc.service.BeerServiceImpl;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerController.class)
@ActiveProfiles("test")
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${controllers.beer-controller.request-path}")
    private String requestPath;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void testGetBeerById() throws Exception {
        BeerDTO givenBeer = beerServiceImpl.listBeers().getFirst();
        given(beerService.getBeerById(givenBeer.getId())).willReturn(Optional.of(givenBeer));

        mockMvc.perform(get(requestPath + "/getBeerById/" + givenBeer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(givenBeer.getBeerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeer)));  // oder das ganze Object
    }

    @Test
    void testGetBearByIdAndThrowsNotFoundException() throws Exception {
        //given(beerService.getBeerById(any())).willThrow(NotfoundException.class);
        given(beerService.getBeerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(requestPath + "/getBeerById/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListBeers() throws Exception {
        List<BeerDTO> givenBeers = beerServiceImpl.listBeers();
        given(beerService.listBeers()).willReturn(givenBeers);

        mockMvc.perform(get(requestPath + "/listBears")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(givenBeers.size())));
    }

    @Test
    void testCreateBeer() throws Exception {
        BeerDTO givenBeer = beerServiceImpl.listBeers().getFirst();
        givenBeer.setVersion(null);
        givenBeer.setId(null);

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(givenBeer);

        mockMvc.perform(post(requestPath + "/createBeer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void testEditBeer() throws Exception {
        BeerDTO givenBeerToEdit = beerServiceImpl.listBeers().getFirst();
        givenBeerToEdit.setBeerName("veryveryNew Bear");

        given(beerService.editBeer(givenBeerToEdit.getId(), givenBeerToEdit)).willReturn(Optional.of(givenBeerToEdit));

        mockMvc.perform(put(requestPath + "/editBeer/" + givenBeerToEdit.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToEdit)));  // oder das ganze Object;
    }

    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO givenBeerToDelete = beerServiceImpl.listBeers().getFirst();

        given(beerService.deleteBeer(any())).willReturn(true);

        mockMvc.perform(delete(requestPath + "/deleteBeer/" + givenBeerToDelete.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToDelete)))
                .andExpect(status().isOk());  // oder das
    }

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO givenBeerToPatch = beerServiceImpl.listBeers().getFirst();
        givenBeerToPatch.setBeerName("patchedBeerName");

        given(beerService.patchBeer(givenBeerToPatch.getId(), givenBeerToPatch)).willReturn(givenBeerToPatch);

        mockMvc.perform(patch(requestPath + "/patchBeer/" + givenBeerToPatch.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToPatch)));  // oder das
    }
}