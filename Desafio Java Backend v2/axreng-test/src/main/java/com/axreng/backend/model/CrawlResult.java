package com.axreng.backend.model;

import com.axreng.backend.enums.StatusEnum;

import java.util.*;


public class CrawlResult {

    private StatusEnum status;
    private Set<String> urls;

    public CrawlResult(StatusEnum status) {
        this.status = status;
        this.urls = new HashSet<>();
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

}
