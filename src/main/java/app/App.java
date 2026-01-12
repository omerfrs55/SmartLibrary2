package app;

import dao.BookDao;
import dao.LoanDao;
import dao.StudentDao;
import entity.Book;
import entity.Book.BookStatus;
import entity.Loan;
import entity.Student;
import util.HibernateUtil;

import java.time.format.DateTimeFormatter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    // Veritabanı erişim nesneleri (Data Access Objects)
    private static final BookDao bookDao = new BookDao();
    private static final StudentDao studentDao = new StudentDao();
    private static final LoanDao loanDao = new LoanDao();

    // Kullanıcıdan veri almak için Scanner
    private static Scanner scanner;

    public static void main(String[] args) {
        // UTF-8 KONSOL AYARLARIMIZ
        // Windows CMD'de Türkçe karakterlerin (ğ, ş, i, ı) bozulmaması için kod
        // sayfasını 65001 yapıyoruz.
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO().start().waitFor();
            } catch (Exception e) {
                // Hata olursa program akışı bozulmasın diye sessiz geçiyoruz.
            }
        }

        try {
            // Java'nın çıktı (System.out) sistemini UTF-8'e zorluyoruz.
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));

            // Java'nın girdi (Scanner) sistemini UTF-8'e zorluyoruz.
            scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // HIBERNATE LOGLARIMIZ
        // Konsolu kirleten kırmızı Hibernate bilgilendirme yazılarını kapatıyoruz.
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        // ANA MENÜ DÖNGÜMÜZ
        while (true) {
            System.out.println("\n\n");
            System.out.println("#################################################");
            System.out.println("#                Smart Library 2                #");
            System.out.println("#################################################");
            System.out.println("");
            System.out.println("   [1] Yeni Kitap Kaydı Oluştur");
            System.out.println("   [2] Kütüphane Arşivini Listele");
            System.out.println("   ----------------------------------");
            System.out.println("   [3] Yeni Öğrenci Kaydı Oluştur");
            System.out.println("   [4] Kayıtlı Öğrencileri Listele");
            System.out.println("   ----------------------------------");
            System.out.println("   [5] Kitap Ödünç Verme (Zimmet)");
            System.out.println("   [6] Ödünç Hareket Dökümü");
            System.out.println("   [7] Kitap İade Alma İşlemi");
            System.out.println("   ----------------------------------");
            System.out.println("   [0] Kapat ve Çık");
            System.out.println("");
            System.out.print(">> Lütfen yapmak istediğiniz işlemi seçiniz: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    listBooks();
                    waitEnter();
                    break;
                case "3":
                    addStudent();
                    break;
                case "4":
                    listStudents();
                    waitEnter();
                    break;
                case "5":
                    borrowBook();
                    break;
                case "6":
                    listLoans();
                    waitEnter();
                    break;
                case "7":
                    returnBook();
                    break;
                case "0":
                    System.out.println("\n>> Sistem güvenli bir şekilde kapatılıyor...");
                    HibernateUtil.shutdown();
                    System.exit(0);
                default:
                    System.out.println(">> [HATA] Geçersiz seçim! Lütfen menüdeki numaralardan birini giriniz.");
                    try {
                        Thread.sleep(800);
                    } catch (Exception e) {
                    } // Uyarıyı okuması için kısa bekleme işlemimiz
            }
        }
    }

    // ========================================================================
    // İŞLEM METOTLARIMIZ
    // ========================================================================

    private static void addBook() {
        System.out.println("\n>> YENİ KİTAP EKLEME EKRANI (İptal: '0')");

        System.out.print("Kitap Başlığı: ");
        String title = scanner.nextLine().trim();
        if (title.equals("0"))
            return;

        System.out.print("Yazar Adı: ");
        String author = scanner.nextLine().trim();

        int year = 0;
        // Geçerli bir yıl girene kadar çıkış yok
        while (true) {
            System.out.print("Basım Yılı: ");
            year = getIntInput();

            // Yıl 0 olamaz, 1000'den küçük olamaz (mantık kontrolü)
            if (year > 1000 && year <= LocalDate.now().getYear()) {
                break; // Doğru girdiyse döngüyü kırma işlemimiz
            }
            System.out.println(">> [HATA] Geçersiz yıl! Lütfen mantıklı bir sayı giriniz (Örn: 2020).");
        }

        Book book = new Book(title, author, year, BookStatus.AVAILABLE);
        bookDao.save(book);

        System.out.println(">> [BAŞARILI] Kitap kütüphane arşivine eklendi.");
        waitEnter();
    }

    private static void listBooks() {
        System.out.println("\n>> KÜTÜPHANE ARŞİVİ");
        List<Book> books = bookDao.getAll();

        if (books == null || books.isEmpty()) {
            System.out.println(">> Listelenecek kitap bulunamadı.");
        } else {
            // Tablo Başlığı
            System.out.printf("%-5s %-30s %-20s %-15s\n", "ID", "BAŞLIK", "YAZAR", "DURUM");
            System.out.println("--------------------------------------------------------------------------");

            for (Book b : books) {
                String statusTr = (b.getStatus() == BookStatus.AVAILABLE) ? "MÜSAİT" : "ÖDÜNÇTE";

                System.out.printf("%-5d %-30s %-20s %-15s\n",
                        b.getId(),
                        limitString(b.getTitle(), 29),
                        limitString(b.getAuthor(), 19),
                        statusTr);
            }
        }
    }

    private static void addStudent() {
        System.out.println("\n>> YENİ ÖĞRENCİ KAYDI (İptal: '0')");

        System.out.print("Öğrenci Adı Soyadı: ");
        String name = scanner.nextLine().trim();
        if (name.equals("0"))
            return;

        System.out.print("Bölümü: ");
        String dept = scanner.nextLine().trim();

        Student s = new Student(name, dept);
        studentDao.save(s);

        System.out.println(">> [BAŞARILI] Öğrenci sisteme kaydedildi.");
        waitEnter();
    }

    private static void listStudents() {
        System.out.println("\n>> ÖĞRENCİ LİSTESİ");
        List<Student> students = studentDao.getAll();

        if (students == null || students.isEmpty()) {
            System.out.println(">> Kayıtlı öğrenci bulunamadı.");
        } else {
            System.out.printf("%-5s %-25s %-20s\n", "ID", "İSİM", "BÖLÜM");
            System.out.println("--------------------------------------------------");
            for (Student s : students) {
                System.out.printf("%-5d %-25s %-20s\n",
                        s.getId(),
                        s.getName(),
                        s.getDepartment());
            }
        }
    }

    private static void borrowBook() {
        System.out.println("\n>> KİTAP ÖDÜNÇ VERME İŞLEMİ");

        // Önce öğrencileri gösterme
        listStudents();
        System.out.print("\nÖğrenci ID Seçiniz (İptal: 0): ");
        Long sId = getLongInput();
        if (sId == 0)
            return;

        Student student = studentDao.getById(sId);
        if (student == null) {
            System.out.println(">> [HATA] Belirtilen ID ile öğrenci bulunamadı.");
            waitEnter();
            return;
        }

        // Sonra kitapları gösterme
        listBooks();
        System.out.print("\nKitap ID Seçiniz (İptal: 0): ");
        Long bId = getLongInput();
        if (bId == 0)
            return;

        Book book = bookDao.getById(bId);
        if (book == null) {
            System.out.println(">> [HATA] Belirtilen ID ile kitap bulunamadı.");
            waitEnter();
            return;
        }

        // Kural Kontrolü: Kitap zaten başkasında mı?
        if (book.getStatus() == BookStatus.BORROWED) {
            System.out.println(">> [UYARI] Bu kitap zaten teslim alınmış! İşlem yapılamaz.");
            // Kullanıcı enter'a basana kadar bekle, sonra menüye dön
            waitEnter();
            return;
        }
        // ---------------------------------

        // Tarih seçimi
        System.out.println("\n[Tarih Girişi]");
        LocalDate borrowDate = askForDate();

        // Kaydetme işlemimiz
        // Hata kontrolü: OneToOne olduğu için try-catch ile sarmak mantıklı olabilir
        // ama kullanıcı arayüzünde "Zaten alınmış" kontrolü yaptığım için patlamaması
        // lazım.
        try {
            Loan loan = new Loan(student, book, borrowDate);
            loanDao.save(loan);

            // Kitap durumunu güncelleme işlemi
            book.setStatus(BookStatus.BORROWED);
            bookDao.update(book);

            System.out.println(">> [BAŞARILI] Kitap öğrenciye zimmetlendi.");
        } catch (Exception e) {
            System.out.println(">> [HATA] Veritabanı hatası: " + e.getMessage());
        }

        waitEnter();
    }

    private static void listLoans() {
        System.out.println("\n>> ÖDÜNÇ HAREKET DÖKÜMÜ");
        List<Loan> loans = loanDao.getAll();

        if (loans == null || loans.isEmpty()) {
            System.out.println(">> Kayıtlı işlem bulunamadı.");
        } else {
            System.out.printf("%-5s %-20s %-25s %-15s %-15s\n", "ID", "ÖĞRENCİ", "KİTAP", "ALIŞ TARİHİ", "İADE TARİHİ");
            System.out.println(
                    "-----------------------------------------------------------------------------------------");

            for (Loan l : loans) {
                String returnStr = (l.getReturnDate() == null) ? "---" : l.getReturnDate().toString();

                System.out.printf("%-5d %-20s %-25s %-15s %-15s\n",
                        l.getId(),
                        limitString(l.getStudent().getName(), 19),
                        limitString(l.getBook().getTitle(), 24),
                        l.getBorrowDate(),
                        returnStr);
            }
        }
    }

    private static void returnBook() {
        System.out.println("\n>> KİTAP İADE ALMA İŞLEMİ");

        // 1. Önce tüm kayıtları çekelim
        List<Loan> loans = loanDao.getAll();

        // 2. Dışarıda olan (henüz iade edilmemiş) kitap var mı kontrol edelim
        boolean activeLoanExists = false;
        if (loans != null) {
            for (Loan l : loans) {
                if (l.getReturnDate() == null) { // İade tarihi boşsa kitap hala dışarıdadır
                    activeLoanExists = true;
                    break;
                }
            }
        }

        // 3. Eğer iade edilecek aktif bir kitap yoksa uyarı ver ve çık
        if (!activeLoanExists) {
            System.out.println(
                    ">> [UYARI] Teslim edilmiş kitap bulunmamakta bu nedenle iade işlemi gerçekleştirilemiyor.");
            System.out.println(">> Lütfen önce kitap teslim alın (ödünç verin).");
            waitEnter(); // Kullanıcı okusun diye beklet
            return; // Ana menüye at
        }

        // Ödünçleri listeliyoruz ki ID bilsin
        listLoans();

        System.out.print("\nİade edilecek İşlem ID (Loan ID) giriniz (İptal: 0): ");
        Long id = getLongInput();
        if (id == 0)
            return;

        Loan loan = loanDao.getById(id);
        if (loan == null) {
            System.out.println(">> [HATA] Geçersiz işlem ID.");
            waitEnter();
            return;
        }

        // İkinci kontrol: Belki eski bir ID girdi, zaten iade edilmiş mi?
        if (loan.getReturnDate() != null) {
            System.out.println(">> [UYARI] Bu kitap zaten iade alınmış.");
            waitEnter();
            return;
        }

        System.out.println("\n[İade Tarihi]");
        LocalDate returnDate = askForDate();

        // Mantık kontrolü: İade, alıstan önce olamaz
        if (returnDate.isBefore(loan.getBorrowDate())) {
            System.out.println(">> [HATA] İade tarihi, ödünç alma tarihinden önce olamaz!");
            waitEnter();
            return;
        }

        // İade işlemini tamamla
        loan.setReturnDate(returnDate);
        loanDao.update(loan);

        // Kitabı boşa çıkar
        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookDao.update(book);

        System.out.println(">> [BAŞARILI] Kitap iade alındı ve rafa kaldırıldı.");
        waitEnter();
    }

    // ========================================================================
    // YARDIMCI METOTLARIMIZ
    // ========================================================================

    /*
     * Tarih sorma: "Bugün" seçeneği veya manuel giriş.
     */
    private static LocalDate askForDate() {
        while (true) {
            System.out.print("İşlem tarihi BUGÜN mü? (E/H): ");
            String a = scanner.nextLine().trim().toUpperCase();

            if (a.startsWith("E"))
                return LocalDate.now();
            if (a.startsWith("H"))
                return getFlexibleDateInput();

            System.out.println(">> Lütfen sadece 'E' (Evet) veya 'H' (Hayır) giriniz.");
        }
    }

    /*
     * Tarih formatı doğrulama (YYYY-AA-GG)
     */
    private static LocalDate getFlexibleDateInput() {
        // Tek haneli girişleri kabul eden format (y-M-d)
        // Bu format hem 2023-01-01 hem de 2023-1-1 okuyabilir.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-M-d");

        while (true) {
            System.out.print("Tarih giriniz (Yıl-Ay-Gün): ");
            // Nokta ve Slash işaretlerini Tireye çeviriyoruz
            String dateStr = scanner.nextLine().trim()
                    .replace(".", "-")
                    .replace("/", "-");

            try {
                // Özel formatımızla parse ediyoruz
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                System.out.println(">> [HATA] Tarih formatı geçersiz!");
                System.out.println(">> Lütfen 'Yıl-Ay-Gün' sırasıyla girin (Örn: 2023-5-1 veya 1990-12-30).");
            }
        }
    }

    /*
     * Güvenli Tam Sayı Girişi (Hata durumunda programı kırmaz)
     */
    private static int getIntInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                // Eğer boş enter'a basarsa 0 dönsün ama yukarıdaki addBook bunu kabul etmeyecek
                // zaten.
                if (line.isEmpty())
                    return 0;
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                // Hata verince satır atlamadan kullanıcıyı uyarıyoruz
                System.out.print(">> [HATA] Harf değil SAYI girmelisiniz! Tekrar deneyin: ");
                // Döngü başa dönecek ve scanner.nextLine() tekrar çalışacak
            }
        }
    }

    /*
     * Güvenli ID Girişi (Long tipinde)
     */
    private static Long getLongInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    return 0L;
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println(">> Hatalı giriş! ID sadece rakam olmalıdır: ");
            }
        }
    }

    /*
     * Kullanıcıyı bekletme metodu
     */
    private static void waitEnter() {
        System.out.println("\nAna menüye dönmek için [ENTER] tuşuna basınız...");
        scanner.nextLine();
    }

    /*
     * Tablo taşmalarını önlemek için metin kısaltma metodu
     */
    private static String limitString(String t, int m) {
        if (t == null)
            return "";
        return (t.length() <= m) ? t : t.substring(0, m - 3) + "...";
    }
}