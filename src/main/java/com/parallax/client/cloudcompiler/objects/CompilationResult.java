/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudcompiler.objects;

/**
 *
 * @author Michel
 */
public class CompilationResult {

    private boolean success;

    private String compilerOutput;
    private String compilerError;
    private String binary;
    private String extension;

    public CompilationResult() {
    }

    public CompilationResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCompilerOutput() {
        return compilerOutput;
    }

    public void setCompilerOutput(String compilerOutput) {
        this.compilerOutput = compilerOutput;
    }

    public String getCompilerError() {
        return compilerError;
    }

    public void setCompilerError(String compilerError) {
        this.compilerError = compilerError;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

}
