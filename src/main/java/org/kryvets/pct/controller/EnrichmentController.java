package org.kryvets.pct.controller;

import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.kryvets.pct.service.EnrichmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller to enrich data with product names based on product ID
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EnrichmentController {
    private static final String MEDIA_TYPE = "text/csv";
    private final EnrichmentService enrichmentService;

    /**
     * Post method for enriching a file with product names
     * @param file target csv-file
     * @return enriched csv-file
     * @throws IOException Will be handled by global handler {@link EnrichmentErrorHandler#handleIOException(IOException)}
     * @throws CsvException Will be handled by global handler {@link EnrichmentErrorHandler#handleCsvException(CsvException)}
     */
    @PostMapping(value = "/enrich", produces = MEDIA_TYPE)
    public ResponseEntity<byte[]> enrichData(@RequestParam("file") MultipartFile file) throws IOException, CsvException {
        byte[] processedCsvBytes = enrichmentService.enrichData(file);
        return new ResponseEntity<>(processedCsvBytes, prepareHeaders(processedCsvBytes), HttpStatus.OK);
    }

    private HttpHeaders prepareHeaders(byte[] processedCsvBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MEDIA_TYPE));
        headers.setContentLength(processedCsvBytes.length);
        return headers;
    }
}
