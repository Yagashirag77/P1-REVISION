package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client extends User {
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    public Client() { }
    public Client(String nom, LocalDate dateInscription) {
        super(nom);
        this.dateInscription = dateInscription;
    }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
}
