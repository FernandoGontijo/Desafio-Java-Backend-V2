package com.axreng.backend.service;


import com.axreng.backend.enums.StatusEnum;
import com.axreng.backend.model.CrawlResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class CrawlService {

    static String baseUrl = System.getenv("BASE_URL");
    static  ConcurrentHashMap<String, CrawlResult> searchs = new ConcurrentHashMap<>();

    public static String startCrawl(String keyword) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        String id = GenerateId();

        searchs.put(id, new CrawlResult(StatusEnum.ACTIVE));

        CompletableFuture.supplyAsync(() -> findTermInPage(keyword, id))
                .thenAccept(urls -> {

                    CrawlResult crawlResult = searchs.get(id);
                    crawlResult.setStatus(StatusEnum.DONE);
                    crawlResult.setEndSearch(new Date());
                    searchs.put(id, crawlResult);

                    executorService.shutdown();
                });

        return id;
    }


    private static List<String>  findTermInPage(String keyword, String id) {

        List<String> foundUrls = new ArrayList<>();
        Set<String> visitedUrls = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(baseUrl);


        while (!queue.isEmpty()) {

            String url = queue.poll();
            visitedUrls.add(url);

            try {
                Document document = Jsoup.connect(url).get();
                String pageText = document.text().toLowerCase();

                if (pageText.contains(keyword.toLowerCase())) {

                    CrawlResult crawlResult = searchs.get(id);
                    List<String> listUrls = crawlResult.getUrls();
                    listUrls.add(url);
                    crawlResult.setUrls(listUrls);
                    searchs.put(id, crawlResult);

                    foundUrls.add(url);
                }

                List<String> linkedUrls = document.select("a[href]").eachAttr("abs:href");

                for (String linkedUrl : linkedUrls) {
                    if (!visitedUrls.contains(linkedUrl)
                            && !foundUrls.contains(linkedUrl)
                                && linkedUrl.contains(baseUrl)) {
                        queue.offer(linkedUrl);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return foundUrls;
    }

        public static CrawlResult getCrawlResult(String id) {

            CrawlResult crawlResult = searchs.get(id);

            return crawlResult;
        }


    public static String GenerateId() {

        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

            StringBuilder codeBuilder = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                codeBuilder.append(randomChar);
            }
            return codeBuilder.toString();

    }

}
