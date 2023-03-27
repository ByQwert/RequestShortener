package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.table.TableModel;

// https://raw.githubusercontent.com/PortSwigger/burp-extensions-montoya-api-examples/main/customrequesteditortab/src/main/java/example/customrequesteditortab/MyExtensionProvidedHttpRequestEditor.java
class MyExtensionProvidedHttpRequestEditor implements ExtensionProvidedHttpRequestEditor {
    private final HttpRequestEditor requestEditor;
    private HttpRequestResponse requestResponse;
    private final MontoyaApi api;
    private TableModel tableModel;

    private List<HttpParameter> parametersToSnortenBefore = new ArrayList<HttpParameter>();
    private List<HttpParameter> parametersToShortenAfter = new ArrayList<HttpParameter>();
    
    MyExtensionProvidedHttpRequestEditor(MontoyaApi api, EditorCreationContext creationContext, TableModel tableModel) {
        this.api = api;
        this.tableModel = tableModel;

        if (creationContext.editorMode() == EditorMode.READ_ONLY)
        {
            requestEditor = api.userInterface().createHttpRequestEditor(EditorOptions.READ_ONLY);
        }
        else {
            requestEditor = api.userInterface().createHttpRequestEditor();
        }
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String parameterName = (String) tableModel.getValueAt(row, 0);
            HttpParameterType parameterType = (HttpParameterType) tableModel.getValueAt(row, 1);
            if (requestResponse.request().parameters().stream().filter(p-> p.value().compareTo("[...]") != 0).anyMatch(p -> p.name().equals(parameterName) && p.type().equals(parameterType))) {
                return true;
            }
        }

        return false;
    }

    // Set custom tab content
    // Sets the provided HttpRequestResponse object within the editor component.
    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        HttpRequest shortenedRequest;

        this.requestResponse = requestResponse;

        parametersToShortenAfter.clear();
        parametersToSnortenBefore.clear();

        for (ParsedHttpParameter parsedHttpParameter: requestResponse.request().parameters()) {
            for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                String parameterNameFromTable = (String) tableModel.getValueAt(rowIndex, 0);
                HttpParameterType parameterTypeFromTable = (HttpParameterType) tableModel.getValueAt(rowIndex, 1);
                if (parsedHttpParameter.name().equals(parameterNameFromTable) && parsedHttpParameter.type().equals(parameterTypeFromTable)) {
                    parametersToSnortenBefore.add(HttpParameter.parameter(parsedHttpParameter.name(), parsedHttpParameter.value(), parsedHttpParameter.type()));
                    parametersToShortenAfter.add(HttpParameter.parameter(parsedHttpParameter.name(), "[...]", parsedHttpParameter.type()));
                    break;
                }
            }    
        }

        shortenedRequest = requestResponse.request().withUpdatedParameters(parametersToShortenAfter);
        this.requestEditor.setRequest(shortenedRequest);
    }
    
    // Update original request from the custom tab content
    // An instance of HttpRequest derived from the content of the HTTP request editor.
    @Override
    public HttpRequest getRequest() {
        HttpRequest request;

        if (requestEditor.isModified()) {
            request = requestEditor.getRequest().withUpdatedParameters(parametersToSnortenBefore);
        }
        else {
            request = requestResponse.request();
        }

        return request;
    }

    @Override
    public String caption() {
        return "Shortened";
    }

    @Override
    public Component uiComponent() {
        return requestEditor.uiComponent();
    }

    @Override
    public Selection selectedData() {
        return requestEditor.selection().isPresent() ? requestEditor.selection().get() : null;
    }

    @Override
    public boolean isModified() {
        return requestEditor.isModified();
    }
}