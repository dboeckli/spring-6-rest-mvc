package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.config.SpringSecurityConfigRest;
import ch.springframeworkguru.springrestmvc.service.BeerService;
import ch.springframeworkguru.springrestmvc.service.BeerServiceImpl;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BeerController.class)
@Import(SpringSecurityConfigRest.class)
@ActiveProfiles("test")
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${controllers.beer-controller.request-path}")
    private String requestPath;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> {
            jwt.claims(claims -> {
                    claims.put("scope", "message.read");
                    claims.put("scope", "message.write");
                })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5l));
        });

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void testGetBeerById() throws Exception {
        BeerDTO givenBeer = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        given(beerService.getBeerById(givenBeer.getId())).willReturn(Optional.of(givenBeer));

        mockMvc.perform(get(requestPath + "/getBeerById/" + givenBeer.getId())
                .with(jwtRequestPostProcessor))
                /*
                .with(httpBasic(username,password))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(givenBeer.getBeerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeer)));  // oder das ganze Object
    }

    @Test
    void testGetBeerByIdWithWrongUsernameAndPassword() throws Exception {
        BeerDTO givenBeer = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        given(beerService.getBeerById(givenBeer.getId())).willReturn(Optional.of(givenBeer));

        mockMvc.perform(get(requestPath + "/getBeerById/" + givenBeer.getId())
                .with(httpBasic("wrongusername","wrongpassword"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());  
    }

    @Test
    void testGetBeerByIdAndThrowsNotFoundException() throws Exception {
        //given(beerService.getBeerById(any())).willThrow(NotfoundException.class);
        given(beerService.getBeerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(requestPath + "/getBeerById/" + UUID.randomUUID())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListBeers() throws Exception {
        Page<BeerDTO> givenBeers = beerServiceImpl.listBeers(null, null, null, null, null);
        given(beerService.listBeers(null, null, null, null, null)).willReturn(givenBeers);

        mockMvc.perform(get(requestPath + "/listBeers")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", is((Long.valueOf(givenBeers.getTotalElements())).intValue())));
    }

    @Test
    void testListBeerByName() throws Exception {
        Page<BeerDTO> givenBeers = beerServiceImpl.listBeers(null, null, null, null, null);
        given(beerService.listBeers("IPA", null, null, null, null)).willReturn(givenBeers);
        
        mockMvc.perform(get(requestPath + "/listBeers")
            .with(jwtRequestPostProcessor)
            //.with(httpBasic(username,password))
            .queryParam("beerName", "IPA"))
            
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalElements", is((Long.valueOf(givenBeers.getTotalElements())).intValue())));
    }

    @Test
    void testCreateBeer() throws Exception {
        BeerDTO givenBeer = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        givenBeer.setVersion(null);
        givenBeer.setId(UUID.randomUUID());

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(givenBeer);

        mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password)) 
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeer)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/api/v1/beer/getBeerById/" + givenBeer.getId().toString()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateBeerNullBeername() throws Exception {
        BeerDTO givenBeer = BeerDTO.builder()
            .beerName(null)
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(5))
            .build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(givenBeer);

        MvcResult mvcResult = mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeer)))
                .andExpect(status().isBadRequest()).andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), Matchers
            .either(Matchers.is("[{beerName=must not be blank}, {beerName=must not be null}]"))
                .or(Matchers.is("[{beerName=must not be null}, {beerName=must not be blank}]")));
    }

    @Test
    void testCreateBeerEmptyBeername() throws Exception {
        BeerDTO givenBeer = BeerDTO.builder()
            .beerName("")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(5))
            .build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(givenBeer);

        MvcResult mvcResult = mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeer)))
            .andExpect(status().isBadRequest()).andReturn();

        assertEquals("[{beerName=must not be blank}]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testCreateBeerBlankBeername() throws Exception {
        BeerDTO givenBeer = BeerDTO.builder()
            .beerName("          ")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(5))
            .build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(givenBeer);

        MvcResult mvcResult = mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeer)))
            .andExpect(status().isBadRequest()).andReturn();

        assertEquals("[{beerName=must not be blank}]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testEditBeer() throws Exception {
        BeerDTO givenBeerToEdit = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        givenBeerToEdit.setBeerName("veryveryNew Beer");

        given(beerService.editBeer(givenBeerToEdit.getId(), givenBeerToEdit)).willReturn(Optional.of(givenBeerToEdit));

        mockMvc.perform(put(requestPath + "/editBeer/" + givenBeerToEdit.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToEdit)));  // oder das ganze Object;
    }

    @Test
    void testEditBeerNullBeername() throws Exception {
        BeerDTO givenBeerToEdit = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        givenBeerToEdit.setBeerName(null);

        given(beerService.editBeer(givenBeerToEdit.getId(), givenBeerToEdit)).willReturn(Optional.of(givenBeerToEdit));

        MvcResult mvcResult = mockMvc.perform(put(requestPath + "/editBeer/" + givenBeerToEdit.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenBeerToEdit)))
            .andExpect(status().isBadRequest()).andReturn();

       
        assertThat(mvcResult.getResponse().getContentAsString(), Matchers
            .either(Matchers.is("[{beerName=must not be blank}, {beerName=must not be null}]"))
            .or(Matchers.is("[{beerName=must not be null}, {beerName=must not be blank}]")));
    }

    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO givenBeerToDelete = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();

        given(beerService.deleteBeer(any())).willReturn(true);

        mockMvc.perform(delete(requestPath + "/deleteBeer/" + givenBeerToDelete.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToDelete)))
                .andExpect(status().isOk());  // oder das
    }

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO givenBeerToPatch = beerServiceImpl.listBeers(null, null, null, null, null).getContent().getFirst();
        givenBeerToPatch.setBeerName("patchedBeerName");

        given(beerService.patchBeer(givenBeerToPatch.getId(), givenBeerToPatch)).willReturn(Optional.of(givenBeerToPatch));

        mockMvc.perform(patch(requestPath + "/patchBeer/" + givenBeerToPatch.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenBeerToPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenBeerToPatch)));  // oder das
    }
}
