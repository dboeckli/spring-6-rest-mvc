package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.Beer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
@Slf4j
@ActiveProfiles("mysql")
class MySql2IT {
    @Container
    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.4.5"));


    @Autowired
    BeerRepository beerRepository;


    @Test
    void testListBeers() {
        List<Beer> beers = beerRepository.findAll();
        assertFalse(beers.isEmpty());
    }
}
