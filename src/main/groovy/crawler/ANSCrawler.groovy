package crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

// Web Crawler para coletar dados da ANS
class ANSCrawler {
    static final String BASE_URL = "https://www.gov.br/ans/pt-br"
    static final String DOWNLOADS_PATH = "./Downloads"

    // Task 1: Baixa o componente de comunicação do padrão TISS
    File baixarComponenteComunicacao() {
        println "=== TASK 1: Baixando Componente de Comunicação ==="

        // Navega até a página do padrão TISS
        String url = "${BASE_URL}/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-tiss"
        Document doc = Jsoup.connect(url).timeout(10000).get()

        // Busca o link do componente de comunicação
        Element link = doc.select("a:contains(Componente de Comunicação)").first()

        if (!link) {
            println "Link não encontrado, tentando outra forma..."
            // Busca por links de download na tabela
            link = doc.select("table a[href*=.pdf], table a[href*=.zip]").find {
                it.text().toLowerCase().contains("comunicação")
            }
        }

        if (link) {
            String downloadUrl = link.attr("abs:href")
            println "Baixando de: $downloadUrl"

            // Cria pasta de downloads
            File pastaDownloads = new File("${DOWNLOADS_PATH}/Arquivos_padrao_TISS")
            pastaDownloads.mkdirs()

            // Baixa o arquivo
            String nomeArquivo = downloadUrl.split("/").last()
            File arquivo = new File(pastaDownloads, nomeArquivo)
            arquivo.bytes = new URL(downloadUrl).bytes

            println "Arquivo salvo: ${arquivo.absolutePath}"
            return arquivo
        } else {
            println "Link do componente não encontrado"
            return null
        }
    }

    // Task 2: Coleta dados históricos das versões (a partir de jan/2016)
    File coletarHistoricoVersoes() {
        println "\n=== TASK 2: Coletando Histórico de Versões ==="

        String url = "${BASE_URL}/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-tiss"
        Document doc = Jsoup.connect(url).timeout(10000).get()

        // Busca a tabela de histórico
        Element tabela = doc.select("table:has(th:contains(Competência))").first()

        if (!tabela) {
            println "Tabela de histórico não encontrada"
            return null
        }

        // Cria arquivo CSV
        File arquivo = new File("${DOWNLOADS_PATH}/historico_versoes_TISS.csv")
        arquivo.parentFile.mkdirs()

        arquivo.withWriter('UTF-8') { writer ->
            writer.println("Competência,Publicação,Início de Vigência")

            // Percorre as linhas da tabela
            tabela.select("tr").drop(1).each { linha ->
                def colunas = linha.select("td")
                if (colunas.size() >= 3) {
                    String competencia = colunas[0].text().trim()
                    String publicacao = colunas[1].text().trim()
                    String vigencia = colunas[2].text().trim()

                    // Filtra a partir de jan/2016
                    if (verificarDataAposJan2016(competencia)) {
                        writer.println("${competencia},${publicacao},${vigencia}")
                        println "Coletado: $competencia - $publicacao - $vigencia"
                    }
                }
            }
        }

        println "Histórico salvo: ${arquivo.absolutePath}"
        return arquivo
    }

    // Task 3: Baixa tabela de erros no envio para ANS
    File baixarTabelaErros() {
        println "\n=== TASK 3: Baixando Tabela de Erros ==="

        String url = "${BASE_URL}/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-tiss"
        Document doc = Jsoup.connect(url).timeout(10000).get()

        // Busca o link da tabela de erros
        Element link = doc.select("a:contains(Tabela de erros)").first()

        if (!link) {
            println "Link da tabela de erros não encontrado"
            return null
        }

        String downloadUrl = link.attr("abs:href")
        println "Baixando de: $downloadUrl"

        // Cria pasta de downloads
        File pastaDownloads = new File("${DOWNLOADS_PATH}/Tabelas_relacionadas")
        pastaDownloads.mkdirs()

        // Baixa o arquivo
        String nomeArquivo = downloadUrl.split("/").last()
        if (!nomeArquivo.contains(".")) {
            nomeArquivo = "tabela_erros.pdf"
        }

        File arquivo = new File(pastaDownloads, nomeArquivo)
        arquivo.bytes = new URL(downloadUrl).bytes

        println "Tabela de erros salva: ${arquivo.absolutePath}"
        return arquivo
    }

    // Verifica se a data é após jan/2016
    private boolean verificarDataAposJan2016(String competencia) {
        try {
            // Formato esperado: "mes/ano" (ex: "01/2016")
            def partes = competencia.split("/")
            if (partes.size() == 2) {
                int mes = partes[0].toInteger()
                int ano = partes[1].toInteger()
                return ano > 2016 || (ano == 2016 && mes >= 1)
            }
        } catch (Exception e) {
            // Se não conseguir parsear, inclui o dado
            return true
        }
        return true
    }

    // Executa todas as tasks
    Map<String, File> executarTodasTasks() {
        println "Iniciando Web Crawler ANS...\n"

        Map<String, File> resultados = [:]

        try {
            resultados.componenteComunicacao = baixarComponenteComunicacao()
        } catch (Exception e) {
            println "Erro na Task 1: ${e.message}"
        }

        try {
            resultados.historicoVersoes = coletarHistoricoVersoes()
        } catch (Exception e) {
            println "Erro na Task 2: ${e.message}"
        }

        try {
            resultados.tabelaErros = baixarTabelaErros()
        } catch (Exception e) {
            println "Erro na Task 3: ${e.message}"
        }

        println "\n=== Crawler finalizado! ==="
        return resultados
    }
}

