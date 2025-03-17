package org.openrefine.extensions.llmExtension.cmd;
import com.google.refine.commands.Command;
import com.google.refine.commands.HttpUtilities;

import org.openrefine.extensions.llmExtension.LLMConfiguration;
import org.openrefine.extensions.llmExtension.LLMUtils;
import org.openrefine.extensions.llmExtension.PromptHistory;
import org.openrefine.extensions.llmExtension.service.ChatCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class LLMPromptCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger("LLMPromptCommand");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String param = request.getParameter("project");
        Long projectId;
        try {
            projectId = Long.parseLong(param);
        } catch (NumberFormatException e) {
            projectId = 0L;
        }
        List<PromptHistory> promptHistoryList = LLMUtils.getPromptHistory(projectId);
        respondJSON(response, promptHistoryList);
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

        String param = request.getParameter("projectId");
        Long projectId;
        try {
            projectId = Long.parseLong(param);
        } catch (NumberFormatException e) {
            projectId = 0L;
        }
        String systemContent = request.getParameter("systemContent");
        String jsonSchema = request.getParameter("jsonSchema");
        String responseFormat = request.getParameter("responseFormat");

        LLMConfiguration llmConfiguration = getLLMConfiguration(request);


        LLMUtils.addPromptToPromptHistory(projectId, llmConfiguration.getLabel(),responseFormat, systemContent, jsonSchema);
        List<PromptHistory> promptHistoryList = LLMUtils.getPromptHistory(projectId);
        respondJSON(response, promptHistoryList);
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
