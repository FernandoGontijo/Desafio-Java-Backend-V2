package com.axreng.backend;

import com.axreng.backend.controller.CrawlController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(Main.class);


        port(4567);

        logger.info("Starting application");


        post("/crawl", CrawlController.postCrawl);
        get("/crawl/:id", CrawlController.getCrawl);

    }
}
