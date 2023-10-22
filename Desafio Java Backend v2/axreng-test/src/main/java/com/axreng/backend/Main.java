package com.axreng.backend;

import com.axreng.backend.controller.CrawlController;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        port(4567);

        post("/crawl", CrawlController.postCrawl);
        get("/crawl/:id", CrawlController.getCrawl);

    }
}
