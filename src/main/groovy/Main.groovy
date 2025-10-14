import crawler.ANSCrawler
import groovy.sql.Sql
import model.Email
import repository.EmailRepository
import service.EmailService


class Main {
    static void main(String[] args) {
        println "==================================="
        println "Web Crawler ANS - Padrão TISS"
        println "Desenvolvido por: Azulario"
        println "==================================="

        // Carrega configurações
        Properties config = carregarConfiguracoes()

        // Conecta ao banco de dados
        Sql sql = conectarBanco(config)
        EmailRepository emailRepo = new EmailRepository(sql)

        // Menu principal
        boolean continuar = true
        Scanner scanner = new Scanner(System.in)

        while (continuar) {
            println "\n--- MENU PRINCIPAL ---"
            println "1. Executar Web Crawler"
            println "2. Gerenciar Emails (CRUD)"
            println "3. Sair"
            print "Escolha uma opção: "

            String opcao = scanner.nextLine()

            switch (opcao) {
                case "1":
                    executarCrawler(emailRepo, config)
                    break
                case "2":
                    menuCrudEmails(emailRepo, scanner)
                    break
                case "3":
                    continuar = false
                    println "Encerrando..."
                    break
                default:
                    println "Opção inválida!"
            }
        }

        sql.close()
    }

    // Carrega as configurações do arquivo
    static Properties carregarConfiguracoes() {
        Properties props = new Properties()
        def configFile = Main.class.getResourceAsStream('/database.properties')
        if (configFile) {
            props.load(configFile)
        } else {
            println "Arquivo de configuração não encontrado, usando valores padrão"
            props.setProperty('db.url', 'jdbc:postgresql://localhost:5432/webcrawler_ans')
            props.setProperty('db.user', 'postgres')
            props.setProperty('db.password', 'postgres')
        }
        return props
    }

    // Conecta ao banco PostgreSQL
    static Sql conectarBanco(Properties config) {
        try {
            String url = config.getProperty('db.url')
            String user = config.getProperty('db.user')
            String password = config.getProperty('db.password')

            Sql sql = Sql.newInstance(url, user, password, 'org.postgresql.Driver')
            println "Conectado ao banco de dados!"
            return sql
        } catch (Exception e) {
            println "Erro ao conectar ao banco: ${e.message}"
            println "Verifique se o PostgreSQL está rodando e as configurações estão corretas"
            System.exit(1)
        }
    }

    // Executa o crawler e envia emails
    static void executarCrawler(EmailRepository emailRepo, Properties config) {
        ANSCrawler crawler = new ANSCrawler()
        Map<String, File> resultados = crawler.executarTodasTasks()

        // Lista de emails cadastrados
        List<Email> emails = emailRepo.listarTodos()

        if (emails.isEmpty()) {
            println "\nNenhum email cadastrado. Cadastre emails no menu CRUD antes de enviar relatórios."
            return
        }

        println "\nDeseja enviar o relatório por email? (s/n)"
        Scanner scanner = new Scanner(System.in)
        String resposta = scanner.nextLine()

        if (resposta.toLowerCase() == 's') {
            enviarRelatorios(emails, resultados, config)
        }
    }

    // Envia relatórios por email
    static void enviarRelatorios(List<Email> emails, Map<String, File> resultados, Properties config) {
        EmailService emailService = new EmailService(config)

        // Monta a mensagem do relatório
        String mensagem = """
Olá!

Segue o relatório do Web Crawler ANS com os dados coletados do padrão TISS:

- Componente de Comunicação: ${resultados.componenteComunicacao ? 'Baixado' : 'Não disponível'}
- Histórico de Versões: ${resultados.historicoVersoes ? 'Coletado' : 'Não disponível'}
- Tabela de Erros: ${resultados.tabelaErros ? 'Baixada' : 'Não disponível'}

Arquivos em anexo.

Atenciosamente,
Sistema Web Crawler ANS
"""

        // Lista de anexos
        List<File> anexos = resultados.values().findAll { it != null }

        // Envia para cada email cadastrado
        emails.each { email ->
            try {
                emailService.enviarRelatorio(
                    email.email,
                    "Relatório Web Crawler ANS - Padrão TISS",
                    mensagem,
                    anexos
                )
            } catch (Exception e) {
                println "Erro ao enviar para ${email.email}: ${e.message}"
            }
        }
    }

    // Menu CRUD de emails
    static void menuCrudEmails(EmailRepository repo, Scanner scanner) {
        boolean voltar = false

        while (!voltar) {
            println "\n--- GERENCIAR EMAILS ---"
            println "1. Listar emails"
            println "2. Adicionar email"
            println "3. Atualizar email"
            println "4. Deletar email"
            println "5. Voltar"
            print "Escolha uma opção: "

            String opcao = scanner.nextLine()

            switch (opcao) {
                case "1":
                    listarEmails(repo)
                    break
                case "2":
                    adicionarEmail(repo, scanner)
                    break
                case "3":
                    atualizarEmail(repo, scanner)
                    break
                case "4":
                    deletarEmail(repo, scanner)
                    break
                case "5":
                    voltar = true
                    break
                default:
                    println "Opção inválida!"
            }
        }
    }

    // Lista todos os emails
    static void listarEmails(EmailRepository repo) {
        List<Email> emails = repo.listarTodos()
        if (emails.isEmpty()) {
            println "Nenhum email cadastrado."
        } else {
            println "\n--- Emails Cadastrados ---"
            emails.each { println "ID: ${it.id} - Email: ${it.email}" }
        }
    }

    // Adiciona novo email
    static void adicionarEmail(EmailRepository repo, Scanner scanner) {
        print "Digite o email: "
        String email = scanner.nextLine()

        try {
            Email novoEmail = repo.adicionar(email)
            println "Email adicionado: ${novoEmail}"
        } catch (Exception e) {
            println "Erro ao adicionar email: ${e.message}"
        }
    }

    // Atualiza email existente
    static void atualizarEmail(EmailRepository repo, Scanner scanner) {
        print "Digite o ID do email: "
        Long id = scanner.nextLine().toLong()

        print "Digite o novo email: "
        String novoEmail = scanner.nextLine()

        if (repo.atualizar(id, novoEmail)) {
            println "Email atualizado com sucesso!"
        } else {
            println "Email não encontrado."
        }
    }

    // Deleta email
    static void deletarEmail(EmailRepository repo, Scanner scanner) {
        print "Digite o ID do email: "
        Long id = scanner.nextLine().toLong()

        if (repo.deletar(id)) {
            println "Email deletado com sucesso!"
        } else {
            println "Email não encontrado."
        }
    }
}

