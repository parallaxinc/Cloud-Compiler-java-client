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
public class SpinCloudCompileService {

    private final String BASE_URL;

    public SpinCloudCompileService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    public CompilationResult compileSingleSpin(CompileAction action, String spinCode) throws CompilationException {
        HttpRequest request = HttpRequest.post(getUrl("/single/spin/" + action.name())).send(spinCode);
        return handleResponse(action, request);
    }

    protected CompilationResult handleResponse(CompileAction action, HttpRequest request) throws CompilationException {
        String response = request.body();
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();

        int code = request.code();
        if (code == 200) {

            if (responseObject.get("success").getAsBoolean()) {
                String compilerOut = responseObject.get("compiler-output").getAsString();
                String compilerErr = responseObject.get("compiler-error").getAsString();

                CompilationResult result = new CompilationResult();
                result.setCompilerOutput(compilerOut);
                result.setCompilerError(compilerErr);

                if (action != CompileAction.COMPILE) {

                }
                return result;
            } else {
                String message = responseObject.get("message").getAsString();
                throw new CompilationException(message);
            }
        } else if (code == 400) {
            String message = responseObject.get("message").getAsString();
            throw new CompilationException(message);

        }
        return null;
    }

}
