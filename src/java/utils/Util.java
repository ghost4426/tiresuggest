/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gian Tran
 */
public class Util {

    public static String capitalizeEachWordInString(String str) {
        String newStr = "";
        for (String s : str.toLowerCase().split("\\s")) {
            String tmp = s;
            if (tmp.length() > 1) {
                s = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
            } else {
                s = tmp.toUpperCase();
            }
            newStr += (s + " ");
        }
        return newStr.trim();
    }

    public static String getBrand(String str) {
        String brand = "";
        str = str.toLowerCase();
        if (str.contains("michelin")) {
            brand = "Michelin";
        } else if (str.contains("pirelli")) {
            brand = "Pirelli";
        } else if (str.contains("kumho")) {
            brand = "Kumho Tire";
        } else if (str.contains("continental")) {
            brand = "Continental";
        } else if (str.contains("goodyear")) {
            brand = "Goodyear";
        } else if (str.contains("bridgestone")) {
            brand = "Bridgestone";
        } else if (str.contains("dunlop")) {
            brand = "Dunlop";
        } else if (str.contains("bfgoodrich")) {
            brand = "BFgoodrich";
        } else if (str.contains("falken")) {
            brand = "Falken";
        } else if (str.contains("hankook")) {
            brand = "Hankook";
        } else if (str.contains("toyo")) {
            brand = "Toyo Tires";
        } else if (str.contains("yokohama")) {
            brand = "Yokohama";
        } else if (str.contains("chengsin")) {
            brand = "Chengsin-Maxxis";
        } else {
            brand = "Khác";
        }
        return brand;
    }

    public static String saveImage(String imageUrl, String filePath) {
        try {
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream("web/img/" + filePath.replaceAll("/", "-").replaceAll(" ", "_"));
            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }

            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            filePath = "notfound.png";
        } catch (MalformedURLException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filePath;
    }

}
