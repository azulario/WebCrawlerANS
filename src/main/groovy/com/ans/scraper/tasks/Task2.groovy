package com.ans.scraper.tasks

import com.ans.scraper.ANSScraper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Task2 {

    private final ANSScraper scraper //instancia da classe principal

    Task2(ANSScraper scraper) {
        this.scraper = scraper
    }

    void execute() {
        println "Acessando página da ANS: ${ANSScraper.URL_ANS}"
        Document mainPage = scraper.fetchPage()

        println "Procurando link para tabela de histórico de versões..."
        String tableUrl = findTableLink(mainPage)

        if (tableUrl == null) {
            println "✗ Não foi possível encontrar o link da tabela de histórico de versões."
            return
        }

        println "Acessando página da tabela: ${tableUrl}"
        Document tablePage = scraper.fetchPage(tableUrl)

        println "Procurando tabela com as versões..."
        Element table = findHistoryTable(tablePage)

        if (table == null) {
            println "✗ Tabela não encontrada."
            return
        }

        List<Map<String, String>> data = extractTableData(table)
        saveHistoryCSV(data)

        println "✓ ${data.size()} registros coletados e salvos com sucesso."
    }

    private String findTableLink(Document document) {
        Elements links = document.select("a.internal-link")

        for (Element link : links) {
            String text = link.text().toLowerCase()
            if (text.contains("clique aqui") && text.contains("versões") && text.contains("componentes")) {
                return link.attr("abs:href")
            }
        }

        for (Element link : links) {
            String href = link.attr("abs:href").toLowerCase()
            if (href.contains("historico")) {
                return link.attr("abs:href")
            }
        }

        return null
    }

    private Element findHistoryTable(Document doc) {
        Elements tables = doc.select("table")

        for (Element table : tables) {
            String text = table.text().toLowerCase()
            if (text.contains("competência") &&
                    text.contains("publicação") &&
                    text.contains("vigência")) {
                return table
            }
        }
        return null
    }

    private List<Map<String, String>> extractTableData(Element table) {
        List<Map<String, String>> data = new ArrayList<>()
        Elements rows = table.select("tr")

        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i)
            Elements cols = row.select("td")

            if (cols.size() >= 3) {
                String competencia = cols.get(0).text().trim()
                String publicacao = cols.get(1).text().trim()
                String vigencia = cols.get(2).text().trim()

                if (isFromJan2016Onwards(competencia)) {
                    Map<String, String> record = new HashMap<>()
                    record.put("competencia", competencia)
                    record.put("publicacao", publicacao)
                    record.put("vigencia", vigencia)
                    data.add(record)
                }
            }
        }

        return data
    }

    private boolean isFromJan2016Onwards(String competencia) {
        try {
            String[] parts = competencia.split("/")
            if (parts.length >= 2) {
                int year = Integer.parseInt(parts[parts.length - 1])
                return year >= 2016
            }
        } catch (Exception e) {
            return true
        }
        return false
    }

    private void saveHistoryCSV(List<Map<String, String>> data) {
        File folder = new File("./Downloads/Historico_Versoes_TISS")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        File file = new File(folder, "historico_versoes_tiss.csv")

        try {
            PrintWriter writer = new PrintWriter(file, "UTF-8")
            writer.println("Competência;Publicação;Início de Vigência")

            for (Map<String, String> record : data) {
                writer.println("${record.get('competencia')};${record.get('publicacao')};${record.get('vigencia')}")
            }

            writer.close()
            println "✓ Histórico salvo em: ${file.absolutePath}"
        } catch (Exception e) {
            println "✗ Erro ao salvar CSV: ${e.message}"
        }
    }
}
