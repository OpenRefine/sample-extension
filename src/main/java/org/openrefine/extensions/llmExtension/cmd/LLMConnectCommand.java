package org.openrefine.extensions.llmExtension.cmd;

import com.google.refine.commands.Command;
import com.google.refine.commands.HttpUtilities;
import org.openrefine.extensions.llmExtension.LLMConfiguration;
import org.openrefine.extensions.llmExtension.LLMUtils;
import org.openrefine.extensions.llmExtension.serrvice.ChatCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;

public class LLMConnectCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger("LLMConnectCommand");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtilities.respond(response, "error", "GET not supported");
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtilities.respond(response, "error", "DELETE not supported");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!hasValidCSRFToken(request)) {
            respondCSRFError(response);
            return;
        }

        String subCommand = request.getParameter("subCommand");
        String systemContent = request.getParameter("systemContent");;
        String jsonSchema = request.getParameter("jsonSchema");;
        String userContent = request.getParameter("userContent");
        String responseFormat = request.getParameter("responseFormat");

        LLMConfiguration llmConfiguration = getLLMConfiguration(request);

        if ( ChatCompletionService.ResponseFormat.json_schema.name().equals(responseFormat) && (jsonSchema == null || jsonSchema.isEmpty()) ) {
            respondJSON(response, "Invalid request. Json schema not provided.");
        }

        String llmResponse = null;
        if ("test".equals(subCommand)) {
            llmResponse = ChatCompletionService.test(llmConfiguration);
        } else if ( "preview".equals(subCommand)) {
            try {
                llmResponse = ChatCompletionService.invoke(llmConfiguration, systemContent, responseFormat, jsonSchema, userContent);
            } catch (Exception e) {
                llmResponse = e.getMessage();
            }
        }

        respondJSON(response, llmResponse);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpUtilities.respond(response, "error", "PUT not supported");
    }

    protected LLMConfiguration getLLMConfiguration(HttpServletRequest request) {
        String providerLabel = request.getParameter("providerLabel");
        return LLMUtils.getLLMProvider(providerLabel);
    }
}
