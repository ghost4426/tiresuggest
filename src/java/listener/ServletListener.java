/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import crawler.Crawler;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author Gian Tran
 */
public class ServletListener implements ServletContextListener {

    ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        System.out.println("Listener: App is started");
        scheduleCrawler(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void scheduleCrawler(ServletContext context) {
        Crawler crawler = new Crawler(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dateSchedule = calendar.getTime();
        long period = 7 * 24 * 60 * 60 * 1000;
        Timer timer = new Timer();
        timer.schedule(crawler, dateSchedule, period);
    }
}
