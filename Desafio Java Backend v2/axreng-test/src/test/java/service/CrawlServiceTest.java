package service;

import com.axreng.backend.service.CrawlService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
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

    @Test
    public void testStartCrawl_Success() {
        String keyword = "testKeyword";
        crawlService.startCrawl(keyword);
        assertThat(crawlService.searchs.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void testExtractLinks() throws MalformedURLException {
        String html = "<a href=\"../htmlman2/execve.2.html\">Link 2</a>";
        String baseUrl = "http://hiring.axreng.com/";
        CrawlService crawlService = new CrawlService();
        crawlService.baseUrl = baseUrl;

        List<String> result = crawlService.extractLinks(html);

        assertThat(result, containsInAnyOrder( "http://hiring.axreng.com/htmlman2/execve.2.html"));
    }


    @Test
    public void testGetHtmlContent() {
        String htmlContent = CrawlService.getHtmlContent(crawlService.baseUrl);
        assertThat(htmlContent.isEmpty(), CoreMatchers.is(false));
    }


}

