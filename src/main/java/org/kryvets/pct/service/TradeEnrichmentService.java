package org.kryvets.pct.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.kryvets.pct.controller.EnrichmentErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to enrich data with product names based on product ID
 */
@Service
public class TradeEnrichmentService implements EnrichmentService {
    private static final String MISSING_REPLACEMENT_STRING = "Missing Product Name";
    private static final String INIT_ERROR_STRING = "Can't load product sample file";
    private static final String ERROR_STRING = "Wrong date format: {}, string will be deleted";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentService.class);
    private final Map<String, String> dataMap = new HashMap<>();
    @Value("${sample.csv.file.path}")
    private String csvFilePath;

    /**
     * Init method for dataMap initialization
     *
     */
    @PostConstruct
    public void init() {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            reader.skip(1);
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length == 2) {
                    dataMap.put(line[0], line[1]);
                }
            }
        } catch (Exception e) {
            logger.error(INIT_ERROR_STRING);
        }
    }

    /**
     * Method for enriching a file with product names
     * @param file target csv-file
     * @return enriched csv-file
     * @throws IOException Will be handled by global handler {@link EnrichmentErrorHandler#}
     * @throws CsvException Will be handled by global handler {@link EnrichmentErrorHandler}
     */
    @Override
    public byte[] enrichData(MultipartFile file) throws IOException, CsvException {
        try (InputStream inputStream = file.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream)); CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            String[] headerRow = reader.readNext();

            List<String[]> processedCsv = reader.readAll().stream()
                    .filter(this::isValidDate)
                    .map(this::replaceProductId)
                    .collect(Collectors.toList());

            processedCsv.add(0, headerRow);

            writer.writeAll(processedCsv);
            writer.flush();

            return outputStream.toByteArray();
        }
    }

    private boolean isValidDate(String[] data) {
        try {
            LocalDate.parse(data[0], DateTimeFormatter.ofPattern(DATE_PATTERN));
            return true;
        } catch (Exception e) {
            logger.warn(ERROR_STRING, data[0]);
            return false;
        }
    }
    private String[] replaceProductId(String[] data) {
        data[1] = dataMap.getOrDefault(data[1], MISSING_REPLACEMENT_STRING);
        return data;
    }
}
