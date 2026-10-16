package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javax.persistence.EntityManagerFactory;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() { }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            String jdbcUrl = configuration.getProperty("hibernate.connection.url");
            String mysqlPassword = System.getenv("MYSQL_PASSWORD");
            if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:mysql:")
                    && mysqlPassword != null && !mysqlPassword.isEmpty()) {
                configuration.setProperty("hibernate.connection.password", mysqlPassword);
            }
            return configuration.buildSessionFactory();
        } catch (Throwable exception) {
            System.err.println("Échec de création de la SessionFactory : " + exception.getMessage());
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }
    public static EntityManagerFactory getEntityManagerFactory() { return SESSION_FACTORY; }

    public static void shutdown() {
        if (!SESSION_FACTORY.isClosed()) SESSION_FACTORY.close();
    }
}
