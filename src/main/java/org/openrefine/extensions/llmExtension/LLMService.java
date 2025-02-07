package org.openrefine.extensions.llmExtension;

import java.util.List;

public abstract class LLMService {

    public abstract List<String> getModels();

    public abstract String chatCompletion(LLMConfiguration llmConfig, String systemContent, String userContent);

    public abstract String summarization(LLMConfiguration llmConfig, String userContent);

    public abstract String translate(LLMConfiguration llmConfig, String userContent, String sourceLanguage, String targetLanguage);

    public abstract String testService(LLMConfiguration llmConfig);
}
