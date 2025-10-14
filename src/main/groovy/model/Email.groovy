package model

class Email {
    Long id
    String email

    @Override
    String toString() {
        return "Email{id=$id, email='$email'}"
    }
}

