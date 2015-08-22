package com.sandro.oneinvite.model;

public class Result {

    private final Data data;
    private final String errMsg;

    public Result(String errMsg, Data data) {
        this.data = data;
        this.errMsg = errMsg;
    }

    public Data getData() {
        return data;
    }

    public String getErrMsg() {
        return errMsg;
    }

}
