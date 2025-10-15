package com.ans.scraper.tasks

import com.ans.scraper.ANSScraper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Task1 {
    private final ANSScraper scraper //instância do scraper

    Task1(ANSScraper scraper) {
        this.scraper = scraper
    } //construtor que recebe o scraper como parâmetro

    void execute() {
        println "Acessando a página da ANS..."
        Document mainPage = scraper.fetchPage() //busca o documento HTML da página da ANS

        println "Procurando link da versão mais recente..."
        String versaoUrl = findLatestVersionLink(mainPage) //procura o link da versão mais recente

        if (versaoUrl == null) {
            println "✗ Não foi possível encontrar o link da versão mais recente."
            return
        }

        println "Acessando pagina da versão: ${versaoUrl}"
        Document versaoPage = scraper.fetchPage(versaoUrl)

        println "Procurando tabela com componentes..."
        Element table = versaoPage.selectFirst("table.table.table-bordered")

        if (table == null) {
            println "Tabela não encontrada."
            return
        }

        Elements buttons = table.select("a.btn.btn-primary.btn-sm.center-block.internal-link")

        if (buttons.isEmpty()) {
            println "Nenhum botão encontrado na tabela."
            return
        }

        println "Encontrado ${buttons.size()} botões."

        //primeiro botao precisa entrar ser visualizado
        if (buttons.size() > 0) {
            Element firstButton = buttons.get(0) //pega o primeiro botão
            String viewUrl = firstButton.attr("abs:href") //pega o link do botão

            println "\n→ [1/${buttons.size()}] Acessando visualização: ${viewUrl}"
            downloadFromVisualization(viewUrl, "./Downloads/Componentes_TISS")
        }

        // Demais botões: download direto
        for (int i = 1; i < buttons.size(); i++) {
            Element button = buttons.get(i)
            String fileUrl = button.attr("abs:href")
            String fileName = extractFileName(fileUrl)

            println "\n→ [${i + 1}/${buttons.size()}] Baixando: ${fileName}"
            downloadFile(fileUrl, "./Downloads/Componentes_TISS", fileName)
        }

        println "\n✓ Todos os arquivos baixados!"
    }

    private String findLatestVersionLink(Document doc) {
        Elements links = doc.select("a.internal-link")

        for (Element link : links) {
            String text = link.text().toLowerCase()
            if (text.contains("clique aqui") && text.contains("versão")) {
                return link.attr("abs:href")
            }
        }

        for (Element link : links) {
            String href = link.attr("abs:href").toLowerCase()
            if (href.matches(".*padrao-tiss-[a-z]+-\\d{4}.*")) {
                return link.attr("abs:href")
            }
        }

        return null
    }

    private void downloadFromVisualization(String viewUrl, String folder) {
        try {
            // Se a URL termina com .pdf, baixar diretamente
            if (viewUrl.toLowerCase().endsWith(".pdf")) {
                String fileName = extractFileName(viewUrl)
                println "   PDF detectado, baixando diretamente: ${fileName}"
                downloadFile(viewUrl, folder, fileName)
                return
            }

            println "   Carregando página de visualização..."
            Document viewPage = scraper.fetchPage(viewUrl)

            // Tentar encontrar link de download na página de visualização
            Element downloadLink = viewPage.selectFirst("a[download], a[href*='download']")

            if (downloadLink != null) {
                String fileUrl = downloadLink.attr("abs:href")
                String fileName = extractFileName(fileUrl)

                println "   Link de download encontrado: ${fileName}"
                downloadFile(fileUrl, folder, fileName)
            } else {
                // Fallback: tentar baixar a própria URL com content-type ignore
                String fileName = extractFileName(viewUrl)
                if (!fileName.isEmpty()) {
                    println "   Tentando download direto..."
                    downloadFile(viewUrl, folder, fileName)
                } else {
                    println "✗ Não foi possível encontrar o arquivo para download"
                }
            }
        } catch (Exception e) {
            println "✗ Erro ao acessar visualização: ${e.message}"
        }
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

        return lastPart.isEmpty() ? "arquivo_download" : lastPart
    }
}
