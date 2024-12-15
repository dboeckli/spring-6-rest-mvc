package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.bootstrap.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}
