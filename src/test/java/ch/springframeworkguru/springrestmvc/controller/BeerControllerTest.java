package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.model.Beer;
import ch.springframeworkguru.springrestmvc.service.BeerService;
import ch.springframeworkguru.springrestmvc.service.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerController.class)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void testGetBeerById() throws Exception {
        Beer givenBeer = beerServiceImpl.listBeers().getFirst();
        given(beerService.getBeerById(givenBeer.getId())).willReturn(givenBeer);

        mockMvc.perform(get("/api/v1/beer/getBeerById/" + givenBeer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(givenBeer.getBeerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeer)));  // oder das ganze Object
    }

    @Test
    void testListBeers() throws Exception {
        List<Beer> givenBeers = beerServiceImpl.listBeers();
        given(beerService.listBeers()).willReturn(givenBeers);

        mockMvc.perform(get("/api/v1/beer/listBears")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(givenBeers.size())));
    }

    @Test
    void testCreateBeer() throws Exception {
        Beer givenBeer = beerServiceImpl.listBeers().getFirst();
        givenBeer.setVersion(null);
        givenBeer.setId(null);

        given(beerService.saveNewBeer(any(Beer.class))).willReturn(givenBeer);

        mockMvc.perform(post("/api/v1/beer/createBeer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void testEditBeer() throws Exception {
        Beer givenBeerToEdit = beerServiceImpl.listBeers().getFirst();
        givenBeerToEdit.setBeerName("veryveryNew Bear");

        given(beerService.editBeer(givenBeerToEdit.getId(), givenBeerToEdit)).willReturn(givenBeerToEdit);

        mockMvc.perform(put("/api/v1/beer/editBeer/" + givenBeerToEdit.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToEdit)));  // oder das ganze Object;
    }

    @Test
    void testDeleteBeer() throws Exception {
        Beer givenBeerToDelete = beerServiceImpl.listBeers().getFirst();

        given(beerService.deleteBeer(givenBeerToDelete.getId())).willReturn(givenBeerToDelete);

        mockMvc.perform(delete("/api/v1/beer/deleteBeer/" + givenBeerToDelete.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToDelete)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToDelete)));  // oder das
    }

    @Test
    void testPatchBeer() throws Exception {
        Beer givenBeerToPatch = beerServiceImpl.listBeers().getFirst();
        givenBeerToPatch.setBeerName("patchedBeerName");

        given(beerService.patchBeer(givenBeerToPatch.getId(), givenBeerToPatch)).willReturn(givenBeerToPatch);

        mockMvc.perform(patch("/api/v1/beer/patchBeer/" + givenBeerToPatch.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToPatch)));  // oder das
    }
}