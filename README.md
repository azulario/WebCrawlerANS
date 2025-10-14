# Web Crawler ANS - Padrão TISS

**Desenvolvido por:** Azulario

## Descrição

Bot/Web Crawler para coletar dados do site da Agência Nacional de Saúde (ANS) relacionados ao padrão TISS (Troca de Informações na Saúde Suplementar).

## Funcionalidades

### Tasks Implementadas

1. **Task 1:** Baixar o Componente de Comunicação do padrão TISS
2. **Task 2:** Coletar histórico de versões do padrão TISS (a partir de jan/2016)
3. **Task 3:** Baixar a Tabela de Erros no envio para ANS

### Recursos Extras

- **CRUD de Emails:** Gerenciamento completo de emails interessados
- **Envio de Relatórios:** Envia automaticamente os arquivos coletados por email
- **Banco de Dados PostgreSQL:** Armazena emails de forma persistente

## Tecnologias Utilizadas

- **Groovy** - Linguagem principal
- **Jsoup** - Parser HTML para Web Scraping
- **PostgreSQL** - Banco de dados
- **JavaMail** - Envio de emails
- **Gradle** - Gerenciamento de dependências

## Pré-requisitos

- Java 11 ou superior
- PostgreSQL instalado e rodando
- Gradle (incluído via wrapper)

## Configuração

### 1. Criar o banco de dados

```sql
CREATE DATABASE webcrawler_ans;
```

### 2. Configurar credenciais

Edite o arquivo `src/main/resources/database.properties`:

```properties
# PostgreSQL
db.url=jdbc:postgresql://localhost:5432/webcrawler_ans
db.user=postgres
db.password=sua_senha

# Email (opcional - para envio de relatórios)
email.host=smtp.gmail.com
email.port=587
email.username=seu_email@gmail.com
email.password=sua_senha_app
email.from=seu_email@gmail.com
```

## Como Executar

### Via Gradle

```bash
./gradlew run
```

### Via IntelliJ IDEA

1. Abra o projeto
2. Execute a classe `Main.groovy`

## Estrutura do Projeto

```
src/main/groovy/
├── Main.groovy                    # Classe principal
├── crawler/
│   └── ANSCrawler.groovy         # Web Crawler
├── model/
│   └── Email.groovy              # Modelo de dados
├── repository/
│   └── EmailRepository.groovy    # CRUD no banco
└── service/
    └── EmailService.groovy       # Envio de emails

Downloads/                         # Arquivos baixados
├── Arquivos_padrao_TISS/
├── Tabelas_relacionadas/
└── historico_versoes_TISS.csv
```

## Menu do Sistema

1. **Executar Web Crawler** - Baixa todos os arquivos da ANS
2. **Gerenciar Emails (CRUD)** - Adicionar, listar, atualizar e deletar emails
3. **Sair** - Encerra o programa

## Observações

- Os arquivos baixados são salvos na pasta `Downloads/`
- O histórico de versões é salvo em formato CSV
- Para enviar emails, configure um App Password do Gmail
- A tabela de emails é criada automaticamente no primeiro uso

## Autor

**Azulario** - Projeto desenvolvido para aprendizado de Web Scraping

