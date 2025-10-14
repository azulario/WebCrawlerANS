package service

import javax.mail.*
import javax.mail.internet.*

// Serviço para enviar emails
class EmailService {
    String host
    String port
    String username
    String password
    String from

    EmailService(Properties config) {
        this.host = config.getProperty('email.host')
        this.port = config.getProperty('email.port')
        this.username = config.getProperty('email.username')
        this.password = config.getProperty('email.password')
        this.from = config.getProperty('email.from')
    }

    // Envia email com anexos
    void enviarRelatorio(String destinatario, String assunto, String mensagem, List<File> anexos = []) {
        Properties props = new Properties()
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.host", host)
        props.put("mail.smtp.port", port)

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password)
            }
        })

        try {
            Message message = new MimeMessage(session)
            message.setFrom(new InternetAddress(from))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
            message.setSubject(assunto)

            // Corpo do email
            MimeBodyPart textoBody = new MimeBodyPart()
            textoBody.setText(mensagem)

            Multipart multipart = new MimeMultipart()
            multipart.addBodyPart(textoBody)

            // Adiciona anexos
            anexos.each { arquivo ->
                MimeBodyPart anexoBody = new MimeBodyPart()
                anexoBody.attachFile(arquivo)
                multipart.addBodyPart(anexoBody)
            }

            message.setContent(multipart)
            Transport.send(message)
            println "Email enviado para: $destinatario"
        } catch (Exception e) {
            println "Erro ao enviar email: ${e.message}"
        }
    }
}

