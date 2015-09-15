/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudcompiler;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parallax.client.cloudcompiler.objects.CompilationException;
import com.parallax.client.cloudcompiler.objects.CompilationResult;
import com.parallax.client.cloudcompiler.objects.CompileAction;

/**
 *
 * @author Michel
 */
public class CCloudCompileService {

    private final String BASE_URL;

    public CCloudCompileService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    public CompilationResult compileSingleC(CompileAction action, String cCode) throws CompilationException {
        HttpRequest request = HttpRequest.post(getUrl("/single/prop-c/" + action.name())).send(cCode);
        return handleResponse(action, request);
    }

    protected CompilationResult handleResponse(CompileAction action, HttpRequest request) throws CompilationException {
        String response = request.body();
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();

        int code = request.code();
        if (code == 200) {
            String compilerOut = responseObject.get("compiler-output").getAsString();
            String compilerErr = responseObject.get("compiler-error").getAsString();

            if (responseObject.get("success").getAsBoolean()) {

                CompilationResult result = new CompilationResult(true);
                result.setCompilerOutput(compilerOut);
                result.setCompilerError(compilerErr);

                if (action != CompileAction.COMPILE) {
                    result.setBinary(responseObject.get("binary").getAsString());
                    result.setExtension(responseObject.get("extension").getAsString());
                }

                return result;
            } else {
                CompilationResult result = new CompilationResult(false);
                result.setCompilerOutput(compilerOut);
                result.setCompilerError(compilerErr);

                return result;
            }
        } else if (code == 400) {
            String message = responseObject.get("message").getAsString();
            throw new CompilationException(message);

        }
        return null;
    }

}
