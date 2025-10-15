# Web Crawler ANS - Padrão TISS

**Desenvolvido por:** Nathalia Veiga

## Descrição

Web Scraper modular para coletar dados e arquivos do site da Agência Nacional de Saúde (ANS) relacionados ao padrão TISS (Troca de Informações na Saúde Suplementar).

## Funcionalidades

### Tasks Implementadas

1. **Task 1:** Baixar todos os Componentes TISS da versão mais recente
   - Componente Organizacional (PDF)
   - Componente de Conteúdo e Estrutura (ZIP)
   - Componente de Segurança (ZIP)
   - Componente de Comunicação (ZIP)
2. **Task 2:** Coletar histórico de versões do padrão TISS (a partir de jan/2016)
3. **Task 3:** Baixar a Tabela de Erros no envio para ANS

## Tecnologias Utilizadas

- **Groovy** - Linguagem principal
- **Jsoup** - Parser HTML para Web Scraping
- **Gradle** - Gerenciamento de dependências e build

## Pré-requisitos

- Java 11 ou superior
- Gradle (incluído via wrapper)

## Como Executar

### Via Gradle

```bash
./gradlew run
```

### Via IntelliJ IDEA

1. Abra o projeto
2. Execute a classe `ANSScraper.groovy`

## Estrutura do Projeto

```
src/main/groovy/com/ans/scraper/
├── ANSScraper.groovy              # Orquestradora + métodos utilitários
└── tasks/
    ├── Task1.groovy               # Download de componentes TISS
    ├── Task2.groovy               # Histórico de versões
    └── Task3.groovy               # Tabela de erros

Downloads/                          # Arquivos baixados
└── Componentes_TISS/
    ├── PadroTISS_ComponenteOrganizacional_202509.pdf
    ├── PadroTISS_ComponentedeContedoeEstrutura_202505.zip
    ├── PadroTISS_segurana_202305.zip
    └── PadroTISSComunicao202505.zip
```

## Arquitetura

### ANSScraper (Classe Principal)
- **Orquestradora:** Executa todas as tasks em sequência
- **Métodos utilitários:**
  - `fetchPage(url)` - Busca e retorna documento HTML
  - `fetchFile(url)` - Baixa arquivo binário (PDF, ZIP, etc)
  - `saveFile(bytes, folder, fileName)` - Salva arquivo em disco

### Tasks (Módulos Independentes)
Cada task é uma classe separada que recebe uma instância do `ANSScraper` e implementa sua própria lógica de scraping.

**Vantagens:**
- Modularidade: fácil adicionar novas tasks
- Reutilização: ANSScraper fornece métodos comuns
- Manutenção: cada task tem responsabilidade única

## Observações Técnicas

- Os arquivos baixados são salvos na pasta `Downloads/`
- PDFs são detectados e baixados diretamente (ver `VISUALIZACAO_PDF_PROBLEMA.md`)
- Timeout de 40 segundos para requisições
- Logs formatados para facilitar acompanhamento da execução

## Autor

**Nathalia Veiga** - Projeto desenvolvido para aprendizado de Web Crawler/Scraping pro Acelera ZG.
