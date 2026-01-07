package entity;

import jakarta.persistence.*; // Hibernate 6 ve sonrası için jakarta kullanılır

/*
 * Sınıf: Book
 * Amaç: Veritabanındaki 'books' tablosunu temsil eder.
 */
@Entity // Bu sınıfın bir veritabanı tablosu olduğunu belirtir.
@Table(name = "books") // Veritabanında tablonun adı 'books' olacak.
public class Book {

    @Id // Tablonun Primary Key (Birincil Anahtar) alanı.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID'nin otomatik artmasını (Auto Increment) sağlar.
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false) // Boş geçilemez alan.
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "year")
    private int year;

    // Enum tipi veritabanında metin (STRING) olarak saklansın (Örn: "AVAILABLE")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status;

    // --- Parametresiz Constructor (Hibernate için zorunludur) ---
    public Book() {
    }

    // --- Veri girişi kolaylığı için Constructor ---
    public Book(String title, String author, int year, BookStatus status) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.status = status;
    }

    // --- Enum Tanımı ---
    public enum BookStatus {
        AVAILABLE, // Müsait
        BORROWED // Ödünç Verilmiş
    }

    // --- Getter ve Setter Metotları (Veriye erişim için) ---

    public Long getId() {
        return id;
    }

    // ID otomatik oluştuğu için genelde setId kullanılmaz ama Hibernate bazen
    // ihtiyaç duyar
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    // Konsola yazdırdığımızda anlamlı çıktı almak için toString
    @Override
    public String toString() {
        return "Kitap [ID=" + id + ", Başlık=" + title + ", Yazar=" + author + ", Durum=" + status + "]";
    }
}