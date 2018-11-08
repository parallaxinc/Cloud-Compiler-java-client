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
import com.google.gson.JsonSyntaxException;
import com.parallax.client.cloudcompiler.exceptions.ServerException;
import com.parallax.client.cloudcompiler.objects.CompilationException;
import com.parallax.client.cloudcompiler.objects.CompilationResult;
import com.parallax.client.cloudcompiler.objects.CompileAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface between the BlocklyProp server and the Cloud Compiler service
 * 
 * @author Michel
 */
public class CCloudCompileService {

    /**
     * Application logging
     */
    private final Logger LOG = LoggerFactory.getLogger(CCloudCompileService.class);
    
    /**
     * Cloud Compiler service base URL
     */
    private final String BASE_URL;

    
    /**
     * Set the Cloud Compiler service base URL
     * 
     * @param baseUrl 
     */
    public CCloudCompileService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    /**
     * Helper function to manufacture a complete Cloud Compiler REST endpoint URL
     * 
     * @param actionUrl
     * @return String fully formed Cloud Compiler REST endpoint URL
     */
    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    
    /**
     * Submit a C source file for compilation
     * 
     * @param action  enumeration COMPILE, BIN, EEPROM
     * @param cCode   C source file text
     * 
     * @return
     * @throws CompilationException
     * @throws ServerException 
     */
    public CompilationResult compileSingleC(CompileAction action, String cCode)
            throws CompilationException, ServerException {
        
        try {
            HttpRequest request = HttpRequest.post(
                    getUrl("/single/prop-c/" + action.name()))
                    .contentType("text/plain")
                    .send(cCode);
            
            // Form a response object from the compilation
            return handleResponse(action, request);
            
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
    }

    
    /**
     * Form a response payload to return to the caller
     * 
     * @param action    enum compiler action: COMPILE, BIN, EEPROM
     * @param request   Payload that was returned from the Cloud Compiler service
     * 
     * @return  A populated CompilationResult object
     * 
     * @throws CompilationException
     * @throws ServerException 
     */
    protected CompilationResult handleResponse(CompileAction action, HttpRequest request)
            throws CompilationException, ServerException {
        
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
