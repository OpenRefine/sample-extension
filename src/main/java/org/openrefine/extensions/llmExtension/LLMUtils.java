package org.openrefine.extensions.llmExtension;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.refine.ProjectManager;
import com.google.refine.io.FileProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LLMUtils {
    private static final Logger logger = LoggerFactory.getLogger("LLMUtils");

    public final static String LLM_EXTENSION_DIR = "llm-extension";
    public final static String SETTINGS_FILE_NAME = ".saved-llm-connections.json";
    public final static String SAVED_CONNECTION_KEY = "savedConnections";

    private static SimpleTextEncryptor textEncryptor = new SimpleTextEncryptor("Aa1Gb@tY7_Y");

    public static void addLLMProvider(LLMConfiguration llmConfig) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String savedConnectionFile = getExtensionFilePath();
            SavedConnectionContainer savedConnectionContainer = mapper.readValue(new File(savedConnectionFile),
                    SavedConnectionContainer.class);
            savedConnectionContainer.getSavedConnections().add(llmConfig);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(savedConnectionFile), savedConnectionContainer);

        } catch (JsonGenerationException e1) {
            logger.error("JsonGenerationException: {}", e1);
            // e1.printStackTrace();
        } catch (JsonMappingException e1) {
            logger.error("JsonMappingException: {}", e1);
            // e1.printStackTrace();
        } catch (IOException e1) {
            logger.error("IOException: {}", e1);
            // e1.printStackTrace();
        }
    }

    public static void updateLLMProvider(String updateKey, LLMConfiguration llmConfig) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String savedConnectionFile = getExtensionFilePath();
            SavedConnectionContainer savedConnectionContainer = mapper.readValue(new File(savedConnectionFile),
                    SavedConnectionContainer.class);
            savedConnectionContainer.getSavedConnections()
                    .removeIf(config -> updateKey.equals(config.getLabel()));
            savedConnectionContainer.getSavedConnections().add(llmConfig);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(savedConnectionFile), savedConnectionContainer);

        } catch (JsonGenerationException e1) {
            logger.error("JsonGenerationException: {}", e1);
            // e1.printStackTrace();
        } catch (JsonMappingException e1) {
            logger.error("JsonMappingException: {}", e1);
            // e1.printStackTrace();
        } catch (IOException e1) {
            logger.error("IOException: {}", e1);
            // e1.printStackTrace();
        }
    }

    public static void deleteLLMProvider(LLMConfiguration llmConfig) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String savedConnectionFile = getExtensionFilePath();
            SavedConnectionContainer savedConnectionContainer = mapper.readValue(new File(savedConnectionFile),
                    SavedConnectionContainer.class);
            savedConnectionContainer.getSavedConnections()
                    .removeIf(config -> llmConfig.getLabel().equals(config.getLabel()));

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(savedConnectionFile), savedConnectionContainer);

        } catch (JsonGenerationException e1) {
            logger.error("JsonGenerationException: {}", e1);
            // e1.printStackTrace();
        } catch (JsonMappingException e1) {
            logger.error("JsonMappingException: {}", e1);
            // e1.printStackTrace();
        } catch (IOException e1) {
            logger.error("IOException: {}", e1);
            // e1.printStackTrace();
        }
    }

    public static List<LLMConfiguration> getLLMProviders() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String filename = getExtensionFilePath();
            File file = new File(filename);
            if (!file.exists()) {
                String dirPath = getExtensionFolder();
                File dirFile = new File(dirPath);
                boolean dirExists = true;
                if (!dirFile.exists()) {
                    dirExists = dirFile.mkdir();
                }
                if (dirExists) {
                    SavedConnectionContainer sc = new SavedConnectionContainer(new ArrayList<LLMConfiguration>());
                    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), sc);
                    return sc.getSavedConnections();
                }
            }
            SavedConnectionContainer savedConnectionContainer = mapper.readValue(new File(filename), SavedConnectionContainer.class);
            return savedConnectionContainer.getSavedConnections();
        } catch (JsonParseException e) {
            logger.error("JsonParseException: {}", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: {}", e);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
        }
        return null;
    }

    public static LLMConfiguration getLLMProvider(String label) {
        LLMConfiguration matchingConfig = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String savedConnectionFile = getExtensionFilePath();
            SavedConnectionContainer savedConnectionContainer = mapper.readValue(new File(savedConnectionFile),
                    SavedConnectionContainer.class);
            matchingConfig = savedConnectionContainer.getSavedConnections()
                    .stream()
                    .filter(config -> label.equals(config.getLabel()))
                    .findFirst()
                    .orElse(null);
        } catch (JsonGenerationException e1) {
            logger.error("JsonGenerationException: {}", e1);
        } catch (JsonMappingException e1) {
            logger.error("JsonMappingException: {}", e1);
        } catch (IOException e1) {
            logger.error("IOException: {}", e1);
        }
        return matchingConfig;
    }

    public static String getExtensionFilePath() {
        File dir = ((FileProjectManager) ProjectManager.singleton).getWorkspaceDir();
        String fileSep = System.getProperty("file.separator");
        String filename = dir.getPath() + fileSep + LLM_EXTENSION_DIR + fileSep + SETTINGS_FILE_NAME;

        logger.debug("** extension file name: {} **", filename);
        return filename;
    }

    public static String getExtensionFolder() {
        File dir = ((FileProjectManager) ProjectManager.singleton).getWorkspaceDir();
        String fileSep = System.getProperty("file.separator");
        String filename = dir.getPath() + fileSep + LLM_EXTENSION_DIR;
        return filename;
    }
}
