package de.test.llama.service;

import de.test.llama.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final DocumentRepository documentRepository;

    @Value("classpath:prompts/context-info-de.txt")
    private Resource systemNdaPrompt;
    @Value("${queries.top-k:2}")
    private int topK;

    public Prompt generatePromptFromClientPrompt(String clientPrompt) {
        List<Document> docs = documentRepository.similaritySearchWithTopK(clientPrompt, topK);
        Message systemMessage = getSystemMessage(docs);
        log.info("System message: {}", systemMessage.getContent());
        UserMessage userMessage = new UserMessage(clientPrompt);
        return new Prompt(List.of(systemMessage, userMessage));
    }

    private Message getSystemMessage(List<Document> similarDocuments) {
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemNdaPrompt);
        return systemPromptTemplate.createMessage(Map.of("documents", documents));
    }
}