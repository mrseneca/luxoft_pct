package org.kryvets.pct.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EnrichmentService {
    byte[] enrichData(MultipartFile file) throws IOException, CsvException;
}
