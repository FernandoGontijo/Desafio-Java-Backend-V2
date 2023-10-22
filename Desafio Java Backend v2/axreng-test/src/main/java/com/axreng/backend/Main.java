package com.axreng.backend;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        CrawlController crawlController = new CrawlController();

        get("/crawl/:id", crawlController::getCrawl);
        post("/crawl", crawlController::postCrawl);

//        get("/crawl/:id", (req, res) ->
//                "GET /crawl/" + req.params("id"));
//        post("/crawl", (req, res) ->
//                "POST /crawl" + System.lineSeparator() + req.body());
    }
}
