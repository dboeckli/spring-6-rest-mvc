package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.bootstrap.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}
