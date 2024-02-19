package org.kryvets.pct.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TradeEnrichmentServiceTest {
    @Autowired
    private TradeEnrichmentService tradeEnrichmentService;
    private static final String TEST_1_RESULT = "Treasury Bills Domestic";
    private static final String TEST_2_RESULT = "Missing Product Name";

    @Test
    public void testEnrichmentData_positive() throws IOException, CsvException {
        Resource resource = new ClassPathResource("trades.csv");

        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        MultipartFile multipartFile = new MockMultipartFile("file", "trades.csv", "text/csv", content);

        byte[] enrichData = tradeEnrichmentService.enrichData(multipartFile);
        String enrichedDataAsString = new String(enrichData, StandardCharsets.UTF_8);

        try (CSVReader reader = new CSVReader(new StringReader(enrichedDataAsString))) {
            reader.skip(1);
            String[] result = reader.readNext();
            assertEquals(TEST_1_RESULT, result[1]);
        }
    }

    @Test
    public void testEnrichmentData_missingProductIdReplacement() throws IOException, CsvException {
        Resource resource = new ClassPathResource("trades2.csv");

        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        MultipartFile multipartFile = new MockMultipartFile("file", "trades2.csv", "text/csv", content);

        byte[] enrichData = tradeEnrichmentService.enrichData(multipartFile);
        String enrichedDataAsString = new String(enrichData, StandardCharsets.UTF_8);

        try (CSVReader reader = new CSVReader(new StringReader(enrichedDataAsString))) {
            reader.skip(1);
            String[] result = reader.readNext();
            assertEquals(TEST_2_RESULT, result[1]);
        }
    }

    @Test
    public void testEnrichmentData_wrongDateFormat_stringDeleted() throws IOException, CsvException {
        Resource resource = new ClassPathResource("trades3.csv");

        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        MultipartFile multipartFile = new MockMultipartFile("file", "trades3.csv", "text/csv", content);

        byte[] enrichData = tradeEnrichmentService.enrichData(multipartFile);
        String enrichedDataAsString = new String(enrichData, StandardCharsets.UTF_8);

        try (CSVReader reader = new CSVReader(new StringReader(enrichedDataAsString))) {
            reader.skip(1);
            assertFalse(reader.iterator().hasNext());
        }
    }

}