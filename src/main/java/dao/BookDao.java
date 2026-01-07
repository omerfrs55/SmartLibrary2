package dao;

import entity.Book;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import java.util.List;

/*
 * Sınıf: BookDao
 * Amaç: Book entity'si ile veritabanı arasındaki CRUD (Create, Read, Update, Delete) işlemlerini yönetmek.
 */
public class BookDao {

    // 1. KAYDETME
    public void save(Book book) {
        Transaction transaction = null;
        // Session'ı açıyoruz (try-with-resources kullanarak otomatik kapanmasını
        // sağlayabiliriz ama transaction yönetimi için manuel açtım)
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // İşlemi başlat
            transaction = session.beginTransaction();

            // Nesneyi kaydet
            session.persist(book);

            // İşlemi onayla (Veritabanına işle)
            transaction.commit();
        } catch (Exception e) {
            // Hata olursa işlemi geri al (Rollback)
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // 2. GÜNCELLEME
    public void update(Book book) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // update yerine merge kullanımı daha güvenlidir (nesne session'dan kopmuşsa
            // tekrar bağlar)
            session.merge(book);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    // 3. SİLME
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Önce silinecek nesneyi bul
            Book book = session.get(Book.class, id);
            if (book != null) {
                session.remove(book);
                System.out.println("Kitap silindi ID: " + id);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    // 4. ID İLE GETİRME (GetById)
    public Book getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // get metodu ID ile arama yapar
            return session.get(Book.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 5. HEPSİNİ LİSTELEME (GetAll)
    public List<Book> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL (Hibernate Query Language) kullanıyoruz.
            // "FROM Book" diyerek veritabanındaki tablodan değil, Book sınıfından
            // çekiyoruz.
            return session.createQuery("from Book", Book.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}