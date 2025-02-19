package org.openrefine.extensions.llmExtension.operations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.EngineConfig;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.expr.*;
import com.google.refine.history.HistoryEntry;
import com.google.refine.model.Cell;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import com.google.refine.model.changes.CellAtRow;
import com.google.refine.model.changes.ColumnAdditionChange;
import com.google.refine.operations.EngineDependentOperation;
import com.google.refine.process.LongRunningProcess;
import com.google.refine.process.Process;
import org.apache.commons.lang.Validate;
import org.openrefine.extensions.llmExtension.LLMConfiguration;
import org.openrefine.extensions.llmExtension.LLMUtils;
import org.openrefine.extensions.llmExtension.service.ChatCompletionService;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ColumnAdditionByLLMProcessingOperation extends EngineDependentOperation {

    final protected String _baseColumnName;
    final protected String _newColumnName;
    final protected int _columnInsertIndex;
    final protected int _delay;
    final protected String _providerLabel;
    final protected String _systemContent;
    final protected String _responseFormat;
    final protected String _jsonSchema;

    @JsonCreator
    public ColumnAdditionByLLMProcessingOperation(
            @JsonProperty("engineConfig") EngineConfig engineConfig,
            @JsonProperty("baseColumnName") String baseColumnName,
            @JsonProperty("newColumnName") String newColumnName,
            @JsonProperty("columnInsertIndex") int columnInsertIndex,
            @JsonProperty("delay") int delay,
            @JsonProperty("providerLabel") String providerLabel,
            @JsonProperty("systemContent") String systemContent,
            @JsonProperty("responseFormat") String responseFormat,
            @JsonProperty("jsonSchema") String jsonSchema
    ) {
         super(engineConfig);

        _baseColumnName = baseColumnName;
        _newColumnName = newColumnName;
        _columnInsertIndex = columnInsertIndex;
        _delay = delay;
        _providerLabel = providerLabel;
        _systemContent = systemContent;
        _responseFormat = responseFormat;
        _jsonSchema = jsonSchema;
    }

    public void validate() {
        Validate.notNull(_baseColumnName, "Missing base column name");
        Validate.notNull(_newColumnName, "Missing new column name");
        Validate.isTrue(_columnInsertIndex >= 0, "Invalid column insert index");
        Validate.notNull(_providerLabel, "Missing LLM provider label");
        Validate.notNull(_systemContent, "Missing System prompt");
        Validate.notNull(_responseFormat, "Missing Response format");
    }

    @JsonProperty("newColumnName")
    public String getNewColumnName() {
        return _newColumnName;
    }

    @JsonProperty("columnInsertIndex")
    public int getColumnInsertIndex() {
        return _columnInsertIndex;
    }

    @JsonProperty("baseColumnName")
    public String getBaseColumnName() {
        return _baseColumnName;
    }

    @JsonProperty("providerLabel")
    public String getProviderLabel() {
        return _providerLabel;
    }

    @JsonProperty("systemContent")
    public String getSystemContent() {
        return _systemContent;
    }

    @JsonProperty("responseFormat")
    public String getResponseFormat() {
        return _responseFormat;
    }

    @JsonProperty("jsonSchema")
    public String getJsonSchema() {
        return _jsonSchema;
    }

    @Override
    protected String getBriefDescription(Project project) {
        return MessageFormat.format("Create column {0} at index {1} by AI process on column {2}", _newColumnName, _columnInsertIndex, _baseColumnName);
    }

    protected String createDescription(Column column, List<CellAtRow> cellsAtRows) {
        return MessageFormat.format("Create new column {0}, filling {1} rows by AI process based on column {2} using LLM {3}",
        _newColumnName, cellsAtRows.size(), column.getName(), _providerLabel);
    }

    @Override
    public Process createProcess(Project project, Properties options) throws Exception {
        Engine engine = createEngine(project);
        engine.initializeFromConfig(_engineConfig);

        return new ColumnAdditionByLLMProcessingProcess(
                project,
                engine,
                getBriefDescription(null)
                );
    }

    public class ColumnAdditionByLLMProcessingProcess extends LongRunningProcess implements Runnable {

        final protected Project _project;
        final protected Engine _engine;
        final protected long _historyEntryID;
        final protected LLMConfiguration _llmConfiguration;

        public ColumnAdditionByLLMProcessingProcess(
                Project project,
                Engine engine,
                String description) {
            super(description);
            _project = project;
            _engine = engine;
            _historyEntryID = HistoryEntry.allocateID();
            _llmConfiguration = LLMUtils.getLLMProvider(_providerLabel);
        }

        @Override
        protected Runnable getRunnable() {
            return this;
        }

        @Override
        public void run() {
            Column column = _project.columnModel.getColumnByName(_baseColumnName);
            if (column == null) {
                _project.processManager.onFailedProcess(this, new Exception("No column named " + _baseColumnName));
                return;
            }
            if (_project.columnModel.getColumnByName(_newColumnName) != null) {
                _project.processManager.onFailedProcess(this, new Exception("Another column already named " + _newColumnName));
                return;
            }

            List<CellAtRow> dataRows = new ArrayList<CellAtRow>(_project.rows.size());

            FilteredRows filteredRows = _engine.getAllFilteredRows();
            filteredRows.accept(_project, createRowVisitor(dataRows));

            int count = dataRows.size();
            List<CellAtRow> responseBodies = new ArrayList<CellAtRow>(count);
            int i = 0;
            for (CellAtRow rowData : dataRows) {
                String userData = rowData.cell.value.toString();

                Serializable response = null;
                response = fetch(userData);
                if (response != null) {
                    CellAtRow cellAtRow = new CellAtRow(
                            rowData.row,
                            new Cell(response, null));

                    responseBodies.add(cellAtRow);
                }
                _progress = i++ * 100 / count;
                if (_canceled) {
                    break;
                }
            }

            if (!_canceled) {
                HistoryEntry historyEntry = new HistoryEntry(
                        _historyEntryID,
                        _project,
                        _description,
                        ColumnAdditionByLLMProcessingOperation.this,
                        new ColumnAdditionChange(
                                _newColumnName,
                                _columnInsertIndex,
                                responseBodies));

                _project.history.addEntry(historyEntry);
                _project.processManager.onDoneProcess(this);
            }
        }

        RowVisitor createRowVisitor(List<CellAtRow> cellsAtRows) {
            return new RowVisitor() {

                int cellIndex;
                Properties bindings;
                List<CellAtRow> cellsAtRows;

                public RowVisitor init(List<CellAtRow> cellsAtRows) {
                    Column column = _project.columnModel.getColumnByName(_baseColumnName);

                    this.cellIndex = column.getCellIndex();
                    this.bindings = ExpressionUtils.createBindings(_project);
                    this.cellsAtRows = cellsAtRows;
                    return this;
                }

                @Override
                public void start(Project project) {
                    // nothing to do
                }

                @Override
                public void end(Project project) {
                    // nothing to do
                }

                @Override
                public boolean visit(Project project, int rowIndex, Row row) {
                    Cell cell = row.getCell(cellIndex);
                    Cell newCell = null;

                    newCell = (Cell) cell;
                    if (newCell != null) {
                        cellsAtRows.add(new CellAtRow(rowIndex, newCell));
                    }

                    return false;
                }
            }.init(cellsAtRows);
        }

        Serializable fetch(String userContent) {
            try {
                return ChatCompletionService.invoke(_llmConfiguration, _systemContent, _responseFormat, _jsonSchema, userContent);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }

}
