# RAG with Spring AI and Ollama

This project is a starting point for importing large context files and use them for RAG to Spring AI and Ollama

## Installation

1. Install [Ollama](https://ollama.com/)
2. Download a model, for instance ```ollama run llama3.1:8b```
3. Download an embedding model, for instance ```ollama pull nomic-embed-text```
3. Start the pgVector container. ```docker compose up``` in the root directory
4. Start the application

Orientated at the repo of [benayat](https://github.com/benayat/rag-with-spring-ai/tree/master)