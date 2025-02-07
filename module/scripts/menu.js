I18NUtil.init("llm-extension");

/* Add menu to extension bar */
ExtensionBar.MenuItems.push({
  id: "llm-extension-configure",
  label: $.i18n('llm-extension/menu-label'),
  submenu: [
    {
      id   : "llm-extension/configuration",
      label: $.i18n('llm-extension/manage-llm'),
      click: function () {
          new ManageLLMSettingsUI();
        }
    }
  ]
});

/* Add submenu to column header menu */
DataTableColumnHeaderUI.extendMenu(function (column, columnHeaderUI, menu) {
  MenuSystem.appendTo(menu, "", [
    { /* separator */ },
    {
      id: "llm-extension/chat",
      label: $.i18n('llm-extension/chat'),
      click: function () {
        new LLMChatCompletionDialog(column);
      }
    },
  ]);
});
