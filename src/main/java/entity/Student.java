package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Sınıf: Student
 * Amaç: Veritabanındaki 'students' tablosunu temsil eder.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "department")
    private String department;

    /*
     * İlişki: Bir öğrencinin birden fazla ödünç (Loan) kaydı olabilir.
     * mappedBy = "student": İlişkinin sahibi Loan sınıfındaki 'student' alanıdır diyoruz.
     * CascadeType.ALL: Öğrenci silinirse, ona ait ödünç kayıtları da silinsin (veya güncellensin).
     * FetchType.LAZY: Öğrenci çekildiğinde, ödünç listesini hemen çekme, ihtiyaç olunca çek (Performans için).
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();

    // --- Constructorlar ---
    public Student() {
    }

    public Student(String name, String department) {
        this.name = name;
        this.department = department;
    }

    // --- Getter ve Setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    @Override
    public String toString() {
        return "Öğrenci [ID=" + id + ", İsim=" + name + ", Bölüm=" + department + "]";
    }
}