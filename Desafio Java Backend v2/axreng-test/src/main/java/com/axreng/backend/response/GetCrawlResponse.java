package com.axreng.backend.response;

import com.axreng.backend.enums.StatusEnum;
import com.google.gson.Gson;

import java.util.List;

public class GetCrawlResponse {

    private String id;
    private StatusEnum status;
    private List<String> urls;


    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

}
