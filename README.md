# SmartLibrary2 - ORM Tabanlı Akıllı Kütüphane Sistemi

Bu proje, Nesne Dayalı Programlama-II dersi final projesidir. Kütüphane yönetim süreçlerini dijitalleştirmek amacıyla geliştirilmiş, Java tabanlı ve Hibernate ORM mimarisini kullanan kapsamlı bir otomasyon sistemidir. Proje, Nesneye Yönelik Programlama (OOP) prensipleri ve veri kalıcılığı (Persistence) standartlarına uygun olarak tasarlanmıştır.

## Proje Bilgileri

* **Ders Sorumlusu:** Emrah SARIÇİÇEK
* **Geliştiren:** Ömer Faruk SAĞLAM
* **Proje Adı:** SmartLibrary2 (SmartLibrary2)

---

## Projenin Amacı ve Kapsamı

SmartLibrary2, geleneksel JDBC kullanımının yerine modern ORM (Object Relational Mapping) tekniklerini kullanarak veritabanı işlemlerini nesne tabanlı bir yaklaşımla yönetmeyi hedefler. Proje, CRUD (Oluşturma, Okuma, Güncelleme, Silme) işlemlerini DAO (Data Access Object) tasarım deseni ile gerçekleştirir.

Sistem aşağıdaki temel yeteneklere sahiptir:
* Kitap ve Öğrenci kayıtlarının yönetimi.
* Ödünç verme ve iade alma süreçlerinin takibi.
* Otomatik veritabanı tablosu oluşturma (Hibernate hbm2ddl).
* Veri tutarlılığı ve ilişkisel bütünlük kontrolleri.
* Türkçe karakter desteği ve kullanıcı dostu konsol arayüzü.

---

## Kullanılan Teknolojiler ve Araçlar

Bu projenin geliştirilmesinde aşağıdaki teknolojiler kullanılmıştır:

* **Programlama Dili:** Java (JDK 21)
* **ORM Aracı:** Hibernate 6.4.0.Final
* **Veritabanı:** SQLite
* **Proje Yönetimi:** Maven
* **Log Yönetimi:** SLF4J (NOP - No Operation Logger)

---

## Veritabanı Tasarımı ve Varlık İlişkileri (Entity Relations)

Proje, ilişkisel veritabanı modeline uygun olarak 3 ana tablodan oluşmaktadır. Hibernate Entity anotasyonları ile şu ilişkiler kurulmuştur:

1.  **Books (Kitaplar):**
    * Özellikler: id, title, author, year, status (AVAILABLE/BORROWED).
    * İlişki: Loan tablosu ile ilişkilidir.

2.  **Students (Öğrenciler):**
    * Özellikler: id, name, department.
    * İlişki: Loan tablosu ile One-To-Many (Bir öğrencinin birden fazla ödünç kaydı olabilir) ilişkisine sahiptir.

3.  **Loans (Ödünç İşlemleri):**
    * Özellikler: id, borrowDate, returnDate.
    * İlişkiler:
        * Student ile Many-To-One ilişkisi.
        * Book ile One-To-One ilişkisi (Proje gereksinimleri doğrultusunda, aktif bir ödünç işleminde bir kitap yalnızca bir kayda bağlı olabilir).

---

## Proje Mimarisi

Proje, sürdürülebilirlik ve kod okunabilirliği açısından katmanlı mimari ile tasarlanmıştır:

* **src/main/java/entity:** Veritabanı tablolarını temsil eden POJO sınıfları (Book, Student, Loan).
* **src/main/java/dao:** Veritabanı ile iletişimi sağlayan Data Access Object sınıfları. Tüm CRUD işlemleri ve Transaction yönetimi burada yapılır.
* **src/main/java/util:** Veritabanı bağlantı ayarlarını ve SessionFactory yönetimini sağlayan yardımcı sınıf (HibernateUtil).
* **src/main/java/app:** Uygulamanın giriş noktası (Main). Menü yönetimi ve kullanıcı etkileşimi bu katmandadır.

---

## Kurulum ve Çalıştırma

Projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyebilirsiniz:

1.  **Gereksinimler:** Bilgisayarınızda Java JDK ve Maven yüklü olmalıdır.
2.  **Bağımlılıklar:** `pom.xml` dosyası üzerinden Maven bağımlılıklarını indiriniz (Reload Project).
3.  **Veritabanı:** Proje ilk çalıştırıldığında `library.db` dosyası otomatik olarak oluşturulacak ve tablolar Hibernate tarafından `hbm2ddl.auto=update` stratejisi ile kurulacaktır.
4.  **Başlatma:** `App.java` sınıfı içerisindeki `main` metodu çalıştırılarak uygulama başlatılır.

---

## Kullanım Kılavuzu

Uygulama başlatıldığında aşağıdaki menü seçenekleri sunulur:

* **[1] Kitap Ekle:** Kütüphaneye yeni bir kitap tanımlar. Varsayılan durumu 'MÜSAİT' olarak ayarlanır.
* **[2] Kitapları Listele:** Mevcut kitapları ve ödünç durumlarını listeler.
* **[3] Öğrenci Ekle:** Sisteme yeni öğrenci kaydeder.
* **[4] Öğrencileri Listele:** Kayıtlı öğrencileri gösterir.
* **[5] Kitap Ödünç Ver:** Seçilen öğrenciye, müsait durumdaki bir kitabı zimmetler. Kitap durumu 'ÖDÜNÇTE' olarak güncellenir.
* **[6] Ödünç Listesi:** Geçmiş ve şu anki tüm ödünç hareketlerini listeler.
* **[7] İade Al:** Ödünçteki bir kitabın iade işlemini gerçekleştirir ve kitabı tekrar 'MÜSAİT' durumuna getirir. Teslim edilmemiş kitap yoksa kullanıcı uyarılır.

---

**Not:** Bu proje, Emrah SARIÇİÇEK tarafından verilen final projesi kapsamında Ömer Faruk SAĞLAM tarafından hazırlanmıştır.