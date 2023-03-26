package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.ui.editor.extension.HttpRequestEditorProvider;

import javax.swing.table.TableModel;
 
// https://github.com/PortSwigger/burp-extensions-montoya-api-examples/blob/main/customrequesteditortab/src/main/java/example/customrequesteditortab/MyHttpRequestEditorProvider.java
class MyHttpRequestEditorProvider implements HttpRequestEditorProvider {
    private final MontoyaApi api;
    private final TableModel tableModel;

    MyHttpRequestEditorProvider(MontoyaApi api, TableModel tableModel) {
        this.api = api;
        this.tableModel = tableModel;
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext) {
        return new MyExtensionProvidedHttpRequestEditor(api, creationContext, tableModel);
    }
}