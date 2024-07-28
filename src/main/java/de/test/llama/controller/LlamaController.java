package de.test.llama.controller;

import de.test.llama.service.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("llama")
public class LlamaController {

    private final RagService ragService;
    private final ChatClient chatClient;

    public LlamaController(ChatClient.Builder chatClientBuilder, RagService ragService) {
        this.chatClient = chatClientBuilder.build();
        this.ragService = ragService;
    }

    @PostMapping("rag")
    public String promptWithRag(@RequestBody String userInput) {
        Prompt prompt = ragService.generatePromptFromClientPrompt(userInput);
        return chatClient.prompt(prompt)
                .call().
                content();
    }

    @PostMapping("chat")
    public String prompt(@RequestBody String userInput) {
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }
}
