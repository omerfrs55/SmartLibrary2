# SmartLibraryPlus - ORM Tabanlı Kütüphane Sistemi

Bu proje, Nesneye Dayalı Programlama II dersi için hazırlanmış, Hibernate ORM ve SQLite kullanan bir konsol uygulamasıdır.

## 🚀 Kurulum ve Çalıştırma

1. Projeyi VS Code ile açın.
2. Maven bağımlılıklarının yüklenmesini bekleyin.
3. `src/main/java/app/App.java` dosyasını açın ve çalıştırın (Run).
4. Veritabanı (`library.db`) otomatik olarak oluşturulacaktır.

## 📂 Proje Yapısı

* **src/main/java/entity**: Veritabanı tablolarına karşılık gelen sınıflar (Book, Student, Loan).
* **src/main/java/dao**: Veritabanı erişim katmanı (CRUD işlemleri).
* **src/main/java/util**: Hibernate yapılandırma ve oturum yönetimi.
* **src/main/java/app**: Ana uygulama ve menü sistemi.

## 🛠 Kullanılan Teknolojiler

* Java 17+
* Hibernate ORM 6.4.0
* SQLite JDBC
* Maven

## 📋 Özellikler

* Kitap ve Öğrenci ekleme/listeleme.
* Kitap ödünç verme (Stok kontrolü yapılır).
* Kitap iade alma (Durum güncellemesi yapılır).
* İlişkisel veritabanı yapısı (OneToMany, OneToOne).