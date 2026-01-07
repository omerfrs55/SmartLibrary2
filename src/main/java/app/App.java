package app;

import dao.BookDao;
import dao.LoanDao;
import dao.StudentDao;
import entity.Book;
import entity.Book.BookStatus;
import entity.Loan;
import entity.Student;
import util.HibernateUtil;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Sınıf: App
 * Amaç: Uygulamanın giriş noktası. Menü yönetimi ve kullanıcı etkileşimi burada yapılır.
 * Değişiklikler:
 * 1. Türkçe karakter sorunu için Scanner ve System.out UTF-8'e zorlandı.
 * 2. Log kirliliğini önlemek için Hibernate log seviyesi kapatıldı.
 */
public class App {

    // DAO nesnelerini static olarak oluşturuyoruz ki her metotta tekrar tekrar new
    // yapmayalım.
    private static final BookDao bookDao = new BookDao();
    private static final StudentDao studentDao = new StudentDao();
    private static final LoanDao loanDao = new LoanDao();

    // Scanner'ı henüz başlatmıyoruz, main içinde encoding ayarından sonra
    // başlatacağız.
    private static Scanner scanner;

    public static void main(String[] args) {
        // --- 1. TÜRKÇE KARAKTER AYARLARI ---

        // Windows CMD ekranında UTF-8 (Code Page 65001) aktif etme komutu.
        // Bu komut çalışmazsa Windows terminali Türkçe karakterleri düzgün göstermez.
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO().start().waitFor();
            } catch (Exception e) {
                // Hata oluşursa program patlamasın, sessizce devam etsin.
            }
        }

        try {
            // Çıktıların (System.out) UTF-8 olmasını zorluyoruz.
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));

            // Girdilerin (Scanner) UTF-8 olarak okunmasını zorluyoruz.
            // Kritik Nokta: Bunu yapmazsan senin yazdığın 'Kitap' kelimesini Java bozuk
            // okur.
            scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- 2. LOG TEMİZLİĞİ ---
        // Hibernate'in başlangıçtaki o kalabalık kırmızı yazılarını (INFO loglarını)
        // kapatıyoruz.
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        System.out.println("=========================================");
        System.out.println("  SmartLibrary2 - Akıllı Kütüphane Sistemi");
        System.out.println("=========================================");

        // --- 3. ANA DÖNGÜ ---
        while (true) {
            System.out.println("\n--- ANA MENÜ ---");
            System.out.println("1 - Kitap Ekle");
            System.out.println("2 - Kitapları Listele");
            System.out.println("3 - Öğrenci Ekle");
            System.out.println("4 - Öğrencileri Listele");
            System.out.println("5 - Kitap Ödünç Ver");
            System.out.println("6 - Ödünç Listesini Görüntüle");
            System.out.println("7 - Kitap Geri Teslim Al");
            System.out.println("0 - Çıkış");
            System.out.print("Seçiminiz: ");

            // nextLine() kullanıyoruz çünkü next() boşluktan sonrasını almaz.
            // trim() ile baştaki sondaki boşlukları siliyoruz.
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    listBooks();
                    waitEnter(); // Kullanıcı listeyi görsün diye bekletme
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
                    System.out.println("Sistemden çıkılıyor...");
                    HibernateUtil.shutdown(); // Veritabanı bağlantısını güvenli kapat
                    System.exit(0);
                default:
                    System.out.println("Hatalı seçim! Lütfen tekrar deneyin.");
            }
        }
    }
    // --- KİTAP İŞLEMLERİ ---

    private static void addBook() {
        System.out.println("\n--- Kitap Ekle --- (İptal için '0' yaz)");

        System.out.print("Kitap Başlığı: ");
        String title = scanner.nextLine().trim();
        if (title.equals("0"))
            return; // Kullanıcı vazgeçerse çık

        System.out.print("Yazar: ");
        String author = scanner.nextLine().trim();

        System.out.print("Yayın Yılı: ");
        int year = getIntInput(); // Güvenli sayı alma metodumuz

        // Yeni kitap varsayılan olarak AVAILABLE (Müsait) durumundadır
        Book book = new Book(title, author, year, BookStatus.AVAILABLE);

        bookDao.save(book);
        System.out.println(">> Başarılı: Kitap veritabanına eklendi.");
        waitEnter();
    }

    private static void listBooks() {
        System.out.println("\n--- Kitap Listesi ---");
        List<Book> books = bookDao.getAll();

        if (books == null || books.isEmpty()) {
            System.out.println(">> Listelenecek kitap bulunamadı.");
        } else {
            // Tablo başlıkları (Sola dayalı formatlama)
            System.out.printf("%-5s %-30s %-20s %-15s\n", "ID", "BAŞLIK", "YAZAR", "DURUM");
            System.out.println("--------------------------------------------------------------------------");

            for (Book b : books) {
                // Stringleri belli uzunlukta kesiyoruz ki tablo kaymasın
                System.out.printf("%-5d %-30s %-20s %-15s\n",
                        b.getId(),
                        limitString(b.getTitle(), 29),
                        limitString(b.getAuthor(), 19),
                        b.getStatus());
            }
        }
    }

    // --- ÖĞRENCİ İŞLEMLERİ ---

    private static void addStudent() {
        System.out.println("\n--- Öğrenci Ekle --- (İptal için '0' yaz)");

        System.out.print("Öğrenci Adı Soyadı: ");
        String name = scanner.nextLine().trim();
        if (name.equals("0"))
            return;

        System.out.print("Bölüm: ");
        String dept = scanner.nextLine().trim();

        Student s = new Student(name, dept);
        studentDao.save(s);
        System.out.println(">> Başarılı: Öğrenci kaydedildi.");
        waitEnter();
    }

    private static void listStudents() {
        System.out.println("\n--- Öğrenci Listesi ---");
        List<Student> students = studentDao.getAll();

        if (students == null || students.isEmpty()) {
            System.out.println(">> Kayıtlı öğrenci yok.");
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
    // --- ÖDÜNÇ VE İADE İŞLEMLERİ ---

    /*
     * Metot: borrowBook
     * Amaç: Bir öğrenciye bir kitap ödünç vermek.
     * Kontroller: Öğrenci var mı? Kitap var mı? Kitap zaten başkasında mı?
     */
    private static void borrowBook() {
        System.out.println("\n--- Kitap Ödünç Ver ---");

        // 1. Adım: Öğrenci Seçimi
        listStudents();
        System.out.print("Öğrenci ID seçiniz (İptal: 0): ");
        Long sId = getLongInput();
        if (sId == 0)
            return;

        Student student = studentDao.getById(sId);
        if (student == null) {
            System.out.println(">> Hata: Bu ID ile kayıtlı öğrenci bulunamadı.");
            return;
        }

        // 2. Adım: Kitap Seçimi
        listBooks();
        System.out.print("Kitap ID seçiniz (İptal: 0): ");
        Long bId = getLongInput();
        if (bId == 0)
            return;

        Book book = bookDao.getById(bId);
        if (book == null) {
            System.out.println(">> Hata: Bu ID ile kayıtlı kitap bulunamadı.");
            return;
        }

        // 3. Adım: Durum Kontrolü (Mantıksal Koruma)
        // Eğer kitap zaten verilmişse (BORROWED), tekrar verilemez.
        if (book.getStatus() == BookStatus.BORROWED) {
            System.out.println(">> UYARI: Bu kitap şu an başkasında! Ödünç verilemez.");
            return;
        }

        // 4. Adım: Tarih Belirleme ve Kayıt
        System.out.println("Ödünç alma tarihi girilecek...");
        LocalDate borrowDate = askForDate(); // Tarihi kullanıcıdan veya bugünden al

        Loan loan = new Loan(student, book, borrowDate);
        loanDao.save(loan); // Ödünç kaydını oluştur

        // 5. Adım: Kitabın Durumunu Güncelle
        // Kitap artık "MÜSAİT" değil, "ÖDÜNÇ VERİLDİ" olmalı.
        book.setStatus(BookStatus.BORROWED);
        bookDao.update(book);

        System.out.println(">> İşlem Başarılı: Kitap öğrenciye zimmetlendi.");
        waitEnter();
    }

    /*
     * Metot: listLoans
     * Amaç: Sistemdeki tüm ödünç kayıtlarını listelemek.
     */
    private static void listLoans() {
        System.out.println("\n--- Ödünç Listesi (Hareket Dökümü) ---");
        List<Loan> loans = loanDao.getAll();

        if (loans == null || loans.isEmpty()) {
            System.out.println(">> Kayıtlı işlem yok.");
        } else {
            System.out.printf("%-5s %-20s %-25s %-15s %-15s\n", "ID", "ÖĞRENCİ", "KİTAP", "ALIŞ", "İADE");
            System.out.println(
                    "----------------------------------------------------------------------------------------");

            for (Loan l : loans) {
                // Eğer iade tarihi null ise "---" yazdır, değilse tarihi yazdır.
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

    /*
     * Metot: returnBook
     * Amaç: Ödünç verilen kitabı geri almak (İade).
     */
    private static void returnBook() {
        System.out.println("\n--- Kitap İade Al ---");

        // Kullanıcıya kolaylık olsun diye önce listeyi gösterelim
        listLoans();

        System.out.print("İade edilecek İşlem ID'si (Loan ID) girin (İptal: 0): ");
        Long loanId = getLongInput();
        if (loanId == 0)
            return;

        Loan loan = loanDao.getById(loanId);

        // Hata Kontrolü: Kayıt yoksa veya zaten iade edilmişse uyar
        if (loan == null) {
            System.out.println(">> Hata: Böyle bir işlem kaydı yok.");
            return;
        }
        if (loan.getReturnDate() != null) {
            System.out.println(">> Uyarı: Bu kitap zaten iade edilmiş!");
            return;
        }

        // İade tarihi al
        System.out.println("İade tarihi girilecek...");
        LocalDate returnDate = askForDate();

        // Alış tarihinden önce iade edilemez mantığı (Opsiyonel ama iyi olur)
        if (returnDate.isBefore(loan.getBorrowDate())) {
            System.out.println(">> Hata: İade tarihi, alış tarihinden önce olamaz!");
            return;
        }

        // 1. Loan nesnesini güncelle
        loan.setReturnDate(returnDate);
        loanDao.update(loan);

        // 2. Kitabı boşa çıkar (Status -> AVAILABLE)
        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookDao.update(book);

        System.out.println(">> İşlem Başarılı: Kitap iade alındı, rafa kaldırıldı.");
        waitEnter();
    }
    // --- YARDIMCI (HELPER) METOTLAR ---

    /*
     * Metot: askForDate
     * Amaç: Kullanıcıya işlemi "Bugün" mü yoksa "Geçmiş/Gelecek" bir tarihte mi
     * yapacağını sormak.
     * Neden: Her işlem anlık olmayabilir, geçmişe dönük kayıt girmek gerekebilir.
     */
    private static LocalDate askForDate() {
        while (true) {
            System.out.print("İşlem tarihi BUGÜN mü? (E/H): ");
            String answer = scanner.nextLine().trim().toUpperCase();

            if (answer.startsWith("E")) {
                return LocalDate.now(); // Bugünün tarihini döndür
            }
            if (answer.startsWith("H")) {
                return getFlexibleDateInput(); // Kullanıcıdan tarih iste
            }
            // E veya H dışında bir şeye basarsa döngü başa döner.
            System.out.println(">> Lütfen sadece 'E' (Evet) veya 'H' (Hayır) giriniz.");
        }
    }

    /*
     * Metot: getFlexibleDateInput
     * Amaç: Kullanıcıdan Yıl-Ay-Gün formatında tarih almak ve doğrulamak.
     * Koruma: Kullanıcı "yarın", "bilmiyorum" gibi saçma şeyler yazarsa program
     * çökmez.
     */
    private static LocalDate getFlexibleDateInput() {
        while (true) {
            System.out.print("Tarih giriniz (Yıl-Ay-Gün, Örn: 2023-10-25): ");
            String input = scanner.nextLine().trim();

            // Kullanıcı nokta (.) kullanırsa tireye (-) çeviriyoruz ki format bozulmasın.
            input = input.replace(".", "-").replace("/", "-");

            try {
                // Java'nın yerleşik tarih çeviricisi. Format uymazsa hata fırlatır.
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println(">> Hatalı format! Lütfen YYYY-AA-GG formatında giriniz.");
            }
        }
    }

    /*
     * Metot: getIntInput
     * Amaç: Güvenli tamsayı (int) girişi almak.
     * Kritik: scanner.nextInt() kullanmak yerine string alıp parse ediyoruz.
     * Neden: nextInt() sonrası gelen enter karakteri buffer'da kalıp sonraki soruyu
     * atlatıyordu.
     * Ayrıca harf girilirse programın patlamasını try-catch ile engelliyoruz.
     */
    private static int getIntInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                // Boş enter'a basarsa 0 kabul etsin veya tekrar istesin (burada 0 dönüyoruz)
                if (line.isEmpty())
                    return 0;
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println(">> Hatalı giriş! Lütfen sadece SAYI giriniz.");
                System.out.print("Tekrar deneyin: ");
            }
        }
    }

    /*
     * Metot: getLongInput
     * Amaç: Güvenli uzun tamsayı (Long) girişi almak (ID'ler için).
     */
    private static Long getLongInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    return 0L;
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println(">> Hatalı giriş! ID sadece rakamlardan oluşmalıdır.");
                System.out.print("ID Tekrar girin: ");
            }
        }
    }

    /*
     * Metot: waitEnter
     * Amaç: Kullanıcı listeyi okuyabilsin diye "Devam etmek için Enter'a bas"
     * beklemesi.
     */
    private static void waitEnter() {
        System.out.println("\nAna menüye dönmek için [ENTER] tuşuna basınız...");
        scanner.nextLine();
    }

    /*
     * Metot: limitString
     * Amaç: Tablo görünümünde metinlerin sütunları kaydırmaması için kırpılması.
     * Örnek: "Harry Potter ve Felsefe Taşı" -> "Harry Potter ve..."
     */
    private static String limitString(String text, int maxLength) {
        if (text == null)
            return "";
        if (text.length() <= maxLength)
            return text;
        // Metin uzunsa, son 3 karakteri silip "..." ekle
        return text.substring(0, maxLength - 3) + "...";
    }
}