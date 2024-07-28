package de.test.llama.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitRunner implements ApplicationRunner {

    @Value("classpath:context/delonghi.pdf")
    private Resource contextResource;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.update("delete from vector_store");

        log.info("Read documents.");
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(contextResource, PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfBottomTextLinesToDelete(3)
                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                        .build())
                .withPagesPerDocument(1)
                .build());

        var tokenTextSplitter = new TokenTextSplitter(400, 200, 4, 10000000, true);
        List<Document> splittedDocuments = tokenTextSplitter.apply(pdfReader.get());
        for (Document document : splittedDocuments) {
            vectorStore.accept(List.of(document));
        }

//        this.vectorStore.accept(tokenTextSplitter.apply(pdfReader.get()));


        // alternative for plain text files
//        TextReader textReader = new TextReader(contextResource);
//        textReader.getCustomMetadata().put("filename", "beetlejuice.txt");
//        vectorStore.accept(textReader.read());

        log.info("Data stored to the vector storage.");
    }
}