package org.openrefine.extensions.llmExtension;

public class LLMConfiguration {
    private String label;
    private String apiURL;
    private String modelName;
    private double temperature;
    private int maxTokens;
    private String apiKey;

    public LLMConfiguration(String label, String apiURL, String modelName, double temperature, int maxTokens, String apiKey) {
        this.label = label;
        this.apiURL = apiURL;
        this.modelName = modelName;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.apiKey = apiKey;
    }

    public LLMConfiguration() {

    }

    public String getLabel() {
        return label;
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getModelName() {
        return modelName;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "LLMConfiguration{" +
                ", label=" + label +  '\'' +
                ", apiURL=" + apiURL + '\'' +
                ", modelName='" + modelName + '\'' +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
