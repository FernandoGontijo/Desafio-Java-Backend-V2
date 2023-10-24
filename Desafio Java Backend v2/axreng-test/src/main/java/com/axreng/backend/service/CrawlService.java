package com.axreng.backend.service;

import com.axreng.backend.enums.StatusEnum;
import com.axreng.backend.model.CrawlResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlService {

    static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    public static String baseUrl = System.getenv("BASE_URL");
    public static ConcurrentHashMap<String, CrawlResult> searchs = new ConcurrentHashMap<>();

    public static String startCrawl(String keyword) {

        ExecutorService termSearchExecutor = Executors.newFixedThreadPool(100);

        String id = GenerateId();

        logger.info("Starting search - ID: {}", id);

        searchs.put(id, new CrawlResult(StatusEnum.ACTIVE));

        CompletableFuture.runAsync(() -> findTermInPage(keyword, id), termSearchExecutor).thenRun(() -> {

            CrawlResult crawlResult = searchs.get(id);
            crawlResult.setStatus(StatusEnum.DONE);
            searchs.put(id, crawlResult);

            logger.info("Finishing search - ID: {}", id);

        });

        return id;
    }


    private static void findTermInPage(String keyword, String id) {

        logger.info("Starting search for the keyword: {}", keyword);

        Set<String> foundUrls = new HashSet<>();
        Set<String> visitedUrls = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(baseUrl);

        while (!queue.isEmpty()) {

            String url = queue.poll();
            visitedUrls.add(url);

            try {

                String pageText = getHtmlContent(url);

                if (pageText.toLowerCase().contains(keyword.toLowerCase())) {

                    searchs.compute(id, (key, value) -> {
                        if (value != null) {
                            Set<String> listUrls = value.getUrls();
                            listUrls.add(url);
                            value.setUrls(listUrls);
                        }
                        return value;
                    });

                    foundUrls.add(url);
                }

                List<String> linkedUrls = extractLinks(pageText);

                for (String linkedUrl : linkedUrls) {
                    if (!visitedUrls.contains(linkedUrl)
                            && !foundUrls.contains(linkedUrl)
                            && !queue.contains(linkedUrl)) {
                        queue.offer(linkedUrl);
                    }
                }

                logger.info("Queue size: {} for ID: {}", queue.size(), id);

            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        logger.info("Finishing search for the keyword: {}", keyword);
    }

    public static String getHtmlContent(String url) {

        StringBuilder htmlContent = new StringBuilder();

        try {
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }

            reader.close();
        } catch (MalformedURLException e) {
            logger.error("Invalid URL: {}", url);
        } catch (FileNotFoundException e) {
            logger.error("URL not found: {}", url);
        } catch (Exception e) {
            logger.error("Failed to access URL: {}", url);
        }

        return htmlContent.toString();
    }

    public static List<String> extractLinks(String html) {
        List<String> links = new ArrayList<>();
        Pattern pattern = Pattern.compile("href=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String link = matcher.group(1);

            if (link.startsWith("http://") || link.startsWith("https://")) {

                if (link.startsWith(baseUrl) && isValidURL(link)) {
                    links.add(link);
                }
            } else {

                if (link.contains("../")) {
                    link = link.replace("../", "");
                }

                String absoluteLink = baseUrl + link;

                if (isValidURL(absoluteLink)) {
                    links.add(absoluteLink);
                }
            }
        }
        return links;
    }

    private static boolean isValidURL(String urlString) {
        try {
            new URL(urlString);

            if (urlString.contains("ftp:") || urlString.contains("mailto:")) {
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }


    public static CrawlResult getCrawlResult(String id) {
        logger.info("Retrieving search results for ID: {}", id);
        CrawlResult crawlResult = searchs.get(id);
        return crawlResult;
    }


    private static String GenerateId() {

        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        String generatedId;
        int maxAttempts = 100;
        int attempts = 0;

        do {
            StringBuilder codeBuilder = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                codeBuilder.append(randomChar);
            }
            generatedId = codeBuilder.toString();
            attempts++;
        } while (!VerifyId(generatedId) && attempts < maxAttempts);

        return generatedId;
    }

    private static boolean VerifyId(String id) {
        if (searchs.containsKey(id)) {
            return false;
        } else {
            return true;
        }
    }

}
