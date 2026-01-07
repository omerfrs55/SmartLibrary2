package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/*
 * Sınıf: HibernateUtil
 * Amaç: Hibernate SessionFactory nesnesini tek bir noktadan (Singleton) yönetmek.
 * Neden Gerekli?: SessionFactory oluşturmak maliyetli bir işlemdir. Uygulama boyunca bir kez oluşturup tekrar kullanmak performansı artırır.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    // Static blok: Sınıf ilk yüklendiğinde çalışır.
    static {
        try {
            // hibernate.cfg.xml dosyasını okur ve yapılandırmayı yükler
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Bağlantı kurulamazsa hatayı konsola basar
            System.err.println("SessionFactory oluşturulurken hata oluştu: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Dışarıdan SessionFactory'ye erişmek için kullanılan metod
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Uygulama kapanırken Hibernate'i güvenli kapatmak için
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}