package com.ans.scraper

import com.ans.scraper.tasks.Task1
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ANSScraper {

    static final String URL_ANS = "https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss"

    static final int TIMEOUT = 40000

    void run() {
        println "═══════════════════════════════════════���═══"
        println "   ANS TISS Scraper - Iniciando execução"
        println "═══════════════════════════════════════════\n"

        try {
            executeTask1()
            // executeTask2()
            // executeTask3()

            println "\n═══════════════════════════════════════════"
            println "   Execução finalizada com sucesso!"
            println "═══════════════════════════════════════���═══"
        } catch (Exception e) {
            println "\n✗ Erro durante a execução:"
            println "  ${e.message}"
            e.printStackTrace()
        }
    }

    private void executeTask1() {
        println "→ Task 1: Download Componentes TISS"
        println "─────────────────────────────────────────\n"

        Task1 task1 = new Task1(this)
        task1.execute()

        println "\n✓ Task 1 concluída\n"
    }

//    private void executeTask2() {
//        println "→ Task 2: [Descrição]"
//        println "─────────────────────────────────────────\n"
//
//        Task2 task2 = new Task2(this)
//        task2.execute()
//
//        println "\n✓ Task 2 concluída\n"
//    }
//
//
//    private void executeTask3() {
//        println "→ Task 3: [Descrição]"
//        println "─────────────────────────────────────────\n"
//
//        Task3 task3 = new Task3(this)
//        task3.execute()
//
//        println "\n✓ Task 3 concluída\n"
//    }

    // conecta na página e retorna o documento HTML
    Document fetchPage(String url = URL_ANS) {
        return Jsoup.connect(url)
                .timeout(TIMEOUT)
                .get()
    }

    // baixa o arquivo e retorna os bytes
    byte[] fetchFile(String fileUrl) {
        return Jsoup.connect(fileUrl)
                .ignoreContentType(true)
                .timeout(TIMEOUT)
                .execute()
                .bodyAsBytes()
    }

    // salva o arquivo no diretório especificado
    void saveFile(byte[] bytes, String folder, String fileName) {
        File dir = new File(folder)
        if (!dir.exists()) {
            dir.mkdirs()
        } //dir é o diretório onde o arquivo será salvo

        File file = new File(dir, fileName)
        file.bytes = bytes

        println "✓ Arquivo salvo em: ${file.absolutePath}"
    }

    static void main(String[] args) {
        ANSScraper scraper = new ANSScraper()
        scraper.run()
    }

}