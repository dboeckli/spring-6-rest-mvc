package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.bootstrap.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BeerCsvServiceImplTest {

    @Test
    void testConvertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        BeerCsvService beerCsvService = new BeerCsvServiceImpl();

        List<BeerCSVRecord> beerCSVRecordList = beerCsvService.convertCSV(file);

        assertThat(beerCSVRecordList.size()).isEqualTo(251);
    }
}
