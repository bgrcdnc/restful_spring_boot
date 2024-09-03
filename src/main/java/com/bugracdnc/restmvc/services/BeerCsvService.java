package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.models.BeerCSVRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File file);
}
