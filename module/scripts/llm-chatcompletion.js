function LLMChatCompletionDialog(column) {
  this.launch(column);
}


LLMChatCompletionDialog.prototype.launch = function (column) {
  var columnIndex = Refine.columnNameToColumnIndex(column.name);
  var frame = $(
    DOM.loadHTML("llm-extension", "scripts/dialogs/llm-chatcompletion-dialog.html")
  );

  var elmts = DOM.bind(frame);
  var _llmProviders = [];
  const selectOneLabel = $.i18n('llm-chatcompletion/select-one');

  elmts.dialogHeader.text($.i18n('llm-chatcompletion/add-by-llm') + column.name);
  elmts.or_views_newCol.text($.i18n('llm-chatcompletion/new-col-name'));
  elmts.or_views_llmSelector.text($.i18n('llm-chatcompletion/llm-selector'))
  elmts.or_views_responeFormatSelector.text($.i18n("llm-chatcompletion/responseformat-selector"))
  elmts.systemPromptLabel.text($.i18n('llm-chatcompletion/system-prompt'));
  elmts.jsonSchemaLabel.text($.i18n('llm-chatcompletion/json-schema'));
  elmts.jsonSchemaHint.text($.i18n("llm-chatcompletion/json-schema-hint"));
  elmts.previewResponseLabel.text($.i18n('llm-chatcompletion/preview-response'));
  elmts.previewRequestLabel.text($.i18n('llm-chatcompletion/preview-request'));
  elmts.okButton.html($.i18n('core-buttons/ok'));
  elmts.cancelButton.text($.i18n('core-buttons/cancel'));
  elmts.previewButton.html($.i18n('llm-chatcompletion/preview'));
  elmts.llmPromptHelp.text($.i18n("llm-chatcompletion/prompt-help"));
  elmts.previewData.text($.i18n("llm-chatcompletion/preview-label"));
  elmts.previewResponseTextareaId.text($.i18n("llm-chatcompletion/response-help-text"));

  const llmSelector = elmts.llmselector;
  LLMManager.loadAllLLMProviders().then(llmProviders => {
    llmSelector.empty();
    llmSelector.append('<option value="" disabled selected>' + selectOneLabel + '</option>');
    for (let llmProvider of llmProviders) {
      llmSelector.append('<option value="' + llmProvider.label + '">' + llmProvider.label + '</option>');
      _llmProviders.push(llmProvider);
    }
  });

  const responseformatSelector = elmts.responseformatselector;
  responseformatSelector.append('<option value="" disabled selected>' + selectOneLabel + '</option>');
  responseformatSelector.append('<option value="' + "text" + '">' + "Text" + '</option>');
  responseformatSelector.append('<option value="' + "json_schema" + '">' + "JSON according to provided schema" + '</option>');
  responseformatSelector.append('<option value="' + "json_object" + '">' + "JSON object" + '</option>');

  var o = DataTableView.sampleVisibleRows(column);
  elmts.previewRequestTextareaId.text(o.values[0]);

  var level = DialogSystem.showDialog(frame);
  var dismiss = function () { DialogSystem.dismissUntil(level - 1); };

  elmts.llmselector.on('change', function() {
    elmts.previewResponseTextareaId.text($.i18n("llm-chatcompletion/response-help-text"));
  });

  elmts.responseformatselector.on('change', function () {
    const selectedValue = $(this).val();
    const jsonSchemaTextarea = document.getElementById("jsonSchemaTextareaId");
    if (selectedValue === "json_schema") {
      jsonSchemaTextarea.removeAttribute("disabled");
    } else {
      jsonSchemaTextarea.setAttribute("disabled", "true");
    }
  });

  elmts.cancelButton.on('click', dismiss);

  elmts.okButton.on('click', function (event)  {
        const llmSelector = elmts.llmselector;
        var selectedLLM = llmSelector.val();
        const responseformatSelector = elmts.responseformatselector;
        var selectedresponseFormat = responseformatSelector.val();
        var systemContent = jQueryTrim(elmts.systemPromptTextarea.val());
        var jsonSchema = jQueryTrim(elmts.jsonSchemaTextarea.val());

        if (selectedLLM === '' || selectedresponseFormat === '' || systemContent === '') {
          window.alert($.i18n('llm-chatcompletion/preview-missing-info'));
          return;
        }
        if ( selectedresponseFormat === 'json_schema' &&  jsonSchema === '' ) {
          window.alert($.i18n('llm-chatconpletion/preview-missing-schema'));
          return;
        }

        event.preventDefault();
        var columnName = jQueryTrim(elmts.columnNameInput[0].value);
        if (!columnName.length) {
          alert($.i18n('llm-chatcompletion/warning-col-name'));
          return;
        }

        let delay = 1;

        Refine.postProcess(
          "llm-extension",
          "add-column-by-llm-processing",
          {},
          {
            baseColumnName: column.name,
            newColumnName: columnName,
            columnInsertIndex: columnIndex + 1,
            delay: delay,
            providerLabel: selectedLLM,
            responseFormat: selectedresponseFormat,
            systemContent: systemContent,
            jsonSchema: jsonSchema
          },
          { includeEngine: true, modelsChanged: true, cellsChanged: true, columnStatsChanged: true, rowIdsPreserved: true, recordIdsPreserved: true },
          { onDone: function() { dismiss(); } }
        );
  });

  elmts.previewButton.on('click', async function () {

        const llmSelector = elmts.llmselector;
        var selectedLLM = llmSelector.val();
        const responseformatSelector = elmts.responseformatselector;
        var selectedresponseFormat = responseformatSelector.val();

        if (selectedLLM === '' || selectedresponseFormat === '' || jQueryTrim(elmts.systemPromptTextarea.val()) === '') {
            window.alert($.i18n('llm-chatcompletion/preview-missing-info'));
            return;
        }
        if ( selectedresponseFormat === 'json_schema' && jQueryTrim(elmts.jsonSchemaTextarea.val()) === '' ) {
            window.alert($.i18n('llm-chatconpletion/preview-missing-schema'));
            return;
        }

        var dismissBusy = DialogSystem.showBusy($.i18n('llm-extension/processing'));

        //var llmProviderInfo =  _llmProviders.find(provider => provider.label === selectedLLM);
        var llmProviderInfo = {}
        llmProviderInfo.providerLabel = selectedLLM;
        llmProviderInfo.subCommand = "preview";
        llmProviderInfo.responseFormat = selectedresponseFormat;
        llmProviderInfo.systemContent = jQueryTrim(elmts.systemPromptTextarea.val());
        llmProviderInfo.jsonSchema = jQueryTrim(elmts.jsonSchemaTextarea.val());
        llmProviderInfo.userContent = elmts.previewRequestTextareaId.val();

        var response = "";
        try {
             response = await LLMManager.ProcessLLMRequest(llmProviderInfo);
             elmts.previewResponseTextareaId.text(response);
        } catch (error) {
            alert(error);
        }
        dismissBusy();
    });

};