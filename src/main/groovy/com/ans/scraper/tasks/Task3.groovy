package com.ans.scraper.tasks

import com.ans.scraper.ANSScraper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Task3 {
    private final ANSScraper scraper

    Task3(ANSScraper scraper) {
        this.scraper = scraper
    }

    void execute() {
        println "Acessando página da ANS: ${ANSScraper.URL_ANS}"
        Document mainPage = scraper.fetchPage()

        println "Procurando link para tabelas relacionadas..."
        String tableUrl = findTableLink(mainPage)

        if (tableUrl == null) {
            println "✗ Não foi possível encontrar o link das tabelas relacionadas."
            return
        }

        println "Acessando página de tabelas relacionadas: ${tableUrl}"
        Document tablePage = scraper.fetchPage(tableUrl)

        println "Procurando link da tabela de erros..."
        Element errorLink = findErrorTableLink(tablePage)

        if (errorLink == null) {
            println "✗ Link da tabela de erros não encontrado."
            return
        }

        String fileUrl = errorLink.attr("abs:href")
        String fileName = extractFileName(fileUrl)

        println "→ Baixando: ${fileName}"
        downloadFile(fileUrl, "./Downloads/Tabela_Erros", fileName)

        println "✓ Tabela de erros baixada com sucesso!"
    }

    private String findTableLink(Document document) {
        Elements links = document.select("a.internal-link")


        for (Element link : links) {
            String text = link.text().toLowerCase()
            if (text.contains("tabelas relacionadas") ||
                (text.contains("clique aqui") && text.contains("tabelas"))) {
                return link.attr("abs:href")
            }
        }

        for (Element link : links) {
            String href = link.attr("abs:href").toLowerCase()
            if (href.contains("tabelas-relacionadas")) {
                return link.attr("abs:href")
            }
        }

        return null
    }

    private Element findErrorTableLink(Document doc) {
        Elements links = doc.select("a.internal-link")

        for (Element link : links) {
            String text = link.text().toLowerCase()
            if (text.contains("tabela de erros") && text.contains("envio")) {
                return link
            }
        }

        for (Element link : links) {
            String href = link.attr("href").toLowerCase()
            if (href.contains("tabelaerrosenvioparaanspadraotiss") ||
                (href.endsWith(".xlsx") && href.contains("erro"))) {
                return link
            }
        }

        return null
    }

    private void downloadFile(String url, String folder, String fileName) {
        try {
            byte[] bytes = scraper.fetchFile(url)

            if (bytes.length > 0) {
                scraper.saveFile(bytes, folder, fileName)
            } else {
                println "✗ Arquivo vazio: ${fileName}"
            }
        } catch (Exception e) {
            println "✗ Erro ao baixar ${fileName}: ${e.message}"
        }
    }

    private String extractFileName(String url) {
        String[] parts = url.split("/")
        String lastPart = parts[parts.length - 1]

        if (lastPart.contains("?")) {
            lastPart = lastPart.split("\\?")[0]
        }

        return lastPart.isEmpty() ? "tabela_erros.xlsx" : lastPart
    }
}
