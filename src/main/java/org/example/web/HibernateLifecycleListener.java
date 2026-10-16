package org.example.web;

import org.example.util.HibernateUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class HibernateLifecycleListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        HibernateUtil.getSessionFactory();
    }
    @Override public void contextDestroyed(ServletContextEvent event) {
        HibernateUtil.shutdown();
    }
}
