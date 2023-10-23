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
    public static  ConcurrentHashMap<String, CrawlResult> searchs = new ConcurrentHashMap<>();

    public static String startCrawl(String keyword) {

        logger.info("Iniciando método Post com a keyword {}", keyword);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        String id = GenerateId();

        logger.info("{} Id gerado para a keyword {}", id, keyword);

        searchs.put(id, new CrawlResult(StatusEnum.ACTIVE));

        logger.info("Search do id {} antes de iniciar a busca {}",id,  searchs.get(id));

        CompletableFuture.supplyAsync(() -> findTermInPage(keyword, id))
                .thenAccept(urls -> {

                    logger.info("URLS {}  do search do id {} no final da busca",searchs.get(id).getUrls(), id);

                    CrawlResult crawlResult = searchs.get(id);
                    crawlResult.setStatus(StatusEnum.DONE);
                    crawlResult.setEndSearch(new Date());
                    searchs.put(id, crawlResult);

                    executorService.shutdown();
                });

        return id;
    }


    private static Set<String>  findTermInPage(String keyword, String id) {

        logger.info("Iniciando método findTermInPage busca do Search do id {}",id);

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

                    CrawlResult crawlResult = searchs.get(id);
                    Set<String> listUrls = crawlResult.getUrls();
                    listUrls.add(url);
                    crawlResult.setUrls(listUrls);
                    searchs.put(id, crawlResult);

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

                logger.info("Tamanho da queue {} do id {}",queue.size(), id);

            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return foundUrls;


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
            logger.error("A URL: {} não é válida", url);
        } catch (FileNotFoundException e) {
            logger.error("A URL: {} não foi encontrada", url);
        } catch (Exception e) {
            logger.error("Não foi possível acessar a URL: {}", url);
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

    public static boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
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
