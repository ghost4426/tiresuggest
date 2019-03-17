
import dto.TireList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import crawler.lopxehaitrieucrawler.LXHTCategoriesCrawler;
import crawler.lopxehaitrieucrawler.LXHTCrawler;
import crawler.trungtamvoxecrawler.TTVXCategoriesCrawler;
import crawler.trungtamvoxecrawler.TTVXCrawler;
import utils.Util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Gian Tran
 */
public class test {

    public static void main(String[] args) throws IOException {
        System.out.println("App strated!!!!!!");
        Map<String, String> categories = new HashMap<>();
        categories = new TTVXCategoriesCrawler().getCategories("http://www.trungtamvoxe.com/");
//        categories.put("michellin", "http://www.trungtamvoxe.com/vo-xe-du-lich/michellin/341.html");
        for (Map.Entry<String, String> entry : categories.entrySet()) {
            new TTVXCrawler(entry.getValue(), entry.getKey(), null).startCrawl();
        }

//        categories = new LXHTCategoriesCrawler().getCategories("http://www.lopxehaitrieu.com/");
////        categories.put("Toyo", "http://www.lopxehaitrieu.com/lop-xe/toyo/");
//        for (Map.Entry<String, String> entry : categories.entrySet()) {
//            new LXHTCrawler(entry.getValue(), entry.getKey(), null).startCrawl();
//        }

//        TireDAO dao = new TireDAO();
//        List<Tire> t = new ArrayList<>();
//        t = dao.getListTireBySize("205/65R15");
//        for (int i = 0; i < t.size(); i++) {
//            System.out.println(t.get(i).getName());
//        }
//        Util.saveImage("http://www.trungtamvoxe.com/images/img_thum/bridgestone-alenza001(1).jpg", "img.jpg");
        System.exit(0);
    }
}
