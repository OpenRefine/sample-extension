package org.openrefine.extensions.llmExtension.cmd;

import com.google.refine.browsing.EngineConfig;
import com.google.refine.commands.EngineDependentCommand;
import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Project;
import org.openrefine.extensions.llmExtension.operations.ColumnAdditionByLLMProcessingOperation;

import javax.servlet.http.HttpServletRequest;

public class AddColumnByLLMProcessingCommand extends EngineDependentCommand {

    @Override
    protected AbstractOperation createOperation(Project project,
                                                HttpServletRequest request, EngineConfig engineConfig) throws Exception {

        String baseColumnName = request.getParameter("baseColumnName");
        String columnAction = request.getParameter("columnAction");
        String newColumnName = request.getParameter("newColumnName");
        int columnInsertIndex = Integer.parseInt(request.getParameter("columnInsertIndex"));
        int delay = Integer.parseInt(request.getParameter("delay"));
        String providerLabel = request.getParameter("providerLabel");
        String systemContent = request.getParameter("systemContent");;
        String jsonSchema = request.getParameter("jsonSchema");;
        String responseFormat = request.getParameter("responseFormat");

        return new ColumnAdditionByLLMProcessingOperation(
                engineConfig,
                baseColumnName,
                columnAction,
                newColumnName,
                columnInsertIndex,
                delay,
                providerLabel,
                systemContent,
                responseFormat,
                jsonSchema);
    }
}
