package com.axreng.backend.response;

import com.google.gson.Gson;

public class PostCrawlResponse {

    private String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }


}
