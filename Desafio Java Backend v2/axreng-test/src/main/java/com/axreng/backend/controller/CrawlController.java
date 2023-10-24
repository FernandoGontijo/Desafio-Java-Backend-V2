package com.axreng.backend.controller;

import com.axreng.backend.model.CrawlResult;
import com.axreng.backend.response.ErrorResponse;
import com.axreng.backend.response.GetCrawlResponse;
import com.axreng.backend.response.PostCrawlResponse;
import com.axreng.backend.service.CrawlService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

public class CrawlController {
    private static final Logger logger = LoggerFactory.getLogger(CrawlController.class);
    private static final int MIN_KEYWORD_LENGTH = 4;
    private static final int MAX_KEYWORD_LENGTH = 32;

    public static Route postCrawl = (Request request, Response response) -> {
        try {
            String requestBody = request.body();
            JsonObject requestJson = JsonParser.parseString(requestBody).getAsJsonObject();
            String keyword = requestJson.get("keyword").getAsString();

            if (keyword == null || keyword.isEmpty()) {
                response.status(400);
                return new ErrorResponse("The keyword parameter is mandatory.", 400).toJSON();
            }

            if (keyword.length() < MIN_KEYWORD_LENGTH || keyword.length() > MAX_KEYWORD_LENGTH) {
                response.status(400);
                return new ErrorResponse("Invalid keyword.", 400).toJSON();
            }

            String crawlId = CrawlService.startCrawl(keyword);
            PostCrawlResponse postCrawlResponse = new PostCrawlResponse();
            postCrawlResponse.setId(crawlId);
            return postCrawlResponse.toJSON();
        } catch (Exception e) {
            logger.error("Error processing POST /crawl", e);
            response.status(500);
            return new ErrorResponse("Internal server error.", 500).toJSON();
        }
    };

    public static Route getCrawl = (Request request, Response response) -> {
        try {
            String crawlId = request.params(":id");
            CrawlResult result = CrawlService.getCrawlResult(crawlId);
            if (result == null) {
                response.status(404);
                return new ErrorResponse("Search not found.", 404).toJSON();
            }

            GetCrawlResponse getCrawlResponse = new GetCrawlResponse();
            getCrawlResponse.setId(crawlId);
            getCrawlResponse.setStatus(result.getStatus());
            getCrawlResponse.setUrls(result.getUrls());

            return getCrawlResponse.toJSON();
        } catch (Exception e) {
            logger.error("Error processing GET /crawl/:id", e);
            response.status(500);
            return new ErrorResponse("Internal server error.", 500).toJSON();
        }
    };
}