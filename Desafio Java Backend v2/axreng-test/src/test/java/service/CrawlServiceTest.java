package service;

import com.axreng.backend.enums.StatusEnum;
import com.axreng.backend.model.CrawlResult;
import com.axreng.backend.service.CrawlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class CrawlServiceTest {


    private CrawlService crawlService;

    @BeforeEach
    public void setup() {
        crawlService = new CrawlService();
        crawlService.baseUrl = "http://hiring.axreng.com/";
        crawlService.searchs = mock(ConcurrentHashMap.class);
        openMocks(this);
    }

//    @Test
//    public void testStartCrawl() {
//        String keyword = "testKeyword";
//        String id = "testId";
//        CrawlResult mockCrawlResult = new CrawlResult(StatusEnum.DONE);
//
//        when(crawlService.searchs.put(any(), any())).thenReturn(mockCrawlResult);
//
//        String result = crawlService.startCrawl(keyword);
//
//        assertThat(result, equalTo(id));
//    }

    @Test
    public void testExtractLinks() throws MalformedURLException {
        String html = "<a href=\"../htmlman2/execve.2.html\">Link 2</a>";
        String baseUrl = "http://hiring.axreng.com/";
        CrawlService crawlService = new CrawlService();
        crawlService.baseUrl = baseUrl;

        List<String> result = crawlService.extractLinks(html);

        assertThat(result, containsInAnyOrder( "http://hiring.axreng.com/htmlman2/execve.2.html"));
    }

}

