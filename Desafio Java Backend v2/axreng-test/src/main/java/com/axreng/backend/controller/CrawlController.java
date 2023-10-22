
public class CrawlController {
    private final CrawlService crawlService;

    public CrawlController() {
        this.crawlService = new CrawlService();
    }

    public String getCrawl(Request req, Response res) {
        String id = req.params("id");
        // Realize a lógica de busca usando o serviço
        List<String> results = crawlService.searchUrlsByTerm(id);
        // Retorne os resultados
        return "GET /crawl/" + id + System.lineSeparator() + results;
    }

    public String postCrawl(Request req, Response res) {
        String requestBody = req.body();
        // Realize a lógica de busca usando o serviço com base no corpo da solicitação
        List<String> results = crawlService.searchUrlsByTerm(requestBody);
        // Retorne os resultados
        return "POST /crawl" + System.lineSeparator() + requestBody + System.lineSeparator() + results;
    }
}
