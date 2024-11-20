package org.example.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EndResolutionModel {
    private boolean isDone;
    private String output;

    public EndResolutionModel() {
        isDone = false;
        output = "";
    }

    public void setIsDone(boolean status) {
        isDone = status;
    }

    @JsonProperty("isDone")
    public boolean isQuestDone() {
        return isDone;
    }

    public void setOutput(String text) {
        output = text;
    }

    @JsonProperty("output")
    public String getOutput() {
        return output;
    }
}
