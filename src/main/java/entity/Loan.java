package entity;

import jakarta.persistence.*;
import java.time.LocalDate; // Tarih işlemleri için yeni Java tarih kütüphanesi

/*
 * Sınıf: Loan
 * Amaç: Ödünç alma işlemlerini tutar. 'loans' tablosuna karşılık gelir.
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- İlişkiler ---

    /*
     * ManyToOne: Çoklu Ödünç -> Tek Öğrenci.
     * Bir ödünç işlemi sadece bir öğrenciye ait olabilir.
     * 
     * @JoinColumn: Veritabanında 'student_id' adında bir sütun oluşturur ve Student
     * tablosunun ID'sini buraya yazar.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /*
     * OneToOne: Bir Ödünç işlemi -> Bir Kitap.
     * Hocanın isteği üzerine Loan -> Book ilişkisi OneToOne.
     * Her ödünç satırı tek bir kitabı işaret eder.
     */
    @OneToOne
    @JoinColumn(name = "book_id", unique = true, nullable = false)
    private Book book;

    // --- Tarih Alanları ---

    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    @Column(name = "return_date")
    private LocalDate returnDate; // Henüz teslim edilmediyse null olabilir.

    // --- Constructorlar ---
    public Loan() {
    }

    public Loan(Student student, Book book, LocalDate borrowDate) {
        this.student = student;
        this.book = book;
        this.borrowDate = borrowDate;
    }

    // --- Getter ve Setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}