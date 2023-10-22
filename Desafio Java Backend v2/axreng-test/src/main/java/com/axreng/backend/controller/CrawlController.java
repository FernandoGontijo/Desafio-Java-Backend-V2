package com.axreng.backend.controller;

import com.axreng.backend.model.CrawlResult;
import com.axreng.backend.response.GetCrawlResponse;
import com.axreng.backend.response.PostCrawlResponse;
import com.axreng.backend.service.CrawlService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CrawlController {

    public static Route postCrawl = (Request request, Response response) -> {
        String keyword = request.queryParams("keyword");
        if (keyword == null || keyword.isEmpty()) {
            response.status(400);
            return "The 'keyword' parameter is mandatory.";
        }

        if (keyword.length() < 4 || keyword.length() > 32) {
            response.status(400);
            return "Invalid keyword.";
        }

        String id = CrawlService.startCrawl(keyword);
        PostCrawlResponse postCrawlResponse = new PostCrawlResponse();
        postCrawlResponse.setId(id);
        return postCrawlResponse.toJSON();
    };

    public static Route getCrawl = (Request request, Response response) -> {
        String id = request.params(":id");
        CrawlResult result = CrawlService.getCrawlResult(id);
        if (result == null) {
            response.status(404);
            return "Busca não encontrada.";
        }

        GetCrawlResponse getCrawlResponse = new GetCrawlResponse();
        getCrawlResponse.setId(id);
        getCrawlResponse.setStatus(result.getStatus());
        getCrawlResponse.setUrls(result.getUrls());
        
        return getCrawlResponse.toJSON();
    };
}
