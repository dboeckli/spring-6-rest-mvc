package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerCSVRecord;
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

        assertThat(beerCSVRecordList.size()).isEqualTo(2410);
    }
}
