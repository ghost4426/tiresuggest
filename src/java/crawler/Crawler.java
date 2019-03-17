/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import crawler.lopxehaitrieucrawler.LXHTCategoriesCrawler;
import crawler.lopxehaitrieucrawler.LXHTCrawler;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import utils.Util;

/**
 *
 * @author Gian Tran
 */
public class Crawler extends TimerTask {

    private ServletContext context;

    public Crawler(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        System.out.println("Crawler is started - " + new Date());
        try {
            Map<String, String> categories = new HashMap<>();
//            categories = new TTVXCategoriesCrawler().getCategories("http://www.trungtamvoxe.com/");
//            for (Map.Entry<String, String> entry : categories.entrySet()) {
//                new TTVXCrawler(entry.getValue(), entry.getKey(), context).startCrawl();
//            }
            categories = new LXHTCategoriesCrawler().getCategories("http://www.lopxehaitrieu.com/");
            for (Map.Entry<String, String> entry : categories.entrySet()) {
                new LXHTCrawler(entry.getValue(), entry.getKey(), context).startCrawl();
            }
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        Util.saveImage("http://www.trungtamvoxe.com/images/img_thum/bridgestone-alenza001(1).jpg", "img.jpg", context);

        System.out.println("Crawler is finish " + new Date());
    }

}
