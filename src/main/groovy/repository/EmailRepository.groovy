package repository

import groovy.sql.Sql
import model.Email

// CRUD emaiils
class EmailRepository {
    Sql sql

    EmailRepository(Sql sql) {
        this.sql = sql
        criarTabela()
    }


    void criarTabela() {
        sql.execute('''
            CREATE TABLE IF NOT EXISTS emails (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL
            )
        ''')
    }

    // CREATE - adiciona um novo email
    Email adicionar(String email) {
        def keys = sql.executeInsert("INSERT INTO emails (email) VALUES (?)", [email])
        return new Email(id: keys[0][0] as Long, email: email)
    }

    // READ - lista todos os emails
    List<Email> listarTodos() {
        def emails = []
        sql.eachRow("SELECT id, email FROM emails") { row ->
            emails << new Email(id: row.id, email: row.email)
        }
        return emails
    }

    // READ - busca por id
    Email buscarPorId(Long id) {
        def row = sql.firstRow("SELECT id, email FROM emails WHERE id = ?", [id])
        return row ? new Email(id: row.id, email: row.email) : null
    }


    boolean atualizar(Long id, String novoEmail) {
        def count = sql.executeUpdate("UPDATE emails SET email = ? WHERE id = ?", [novoEmail, id])
        return count > 0
    }


    boolean deletar(Long id) {
        def count = sql.executeUpdate("DELETE FROM emails WHERE id = ?", [id])
        return count > 0
    }
}

