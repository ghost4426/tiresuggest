package crawler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public abstract class BaseCrawler {

    protected BufferedReader getBufferReaderForURL(String urlString) throws UnsupportedEncodingException, IOException {
        BufferedReader reader = null;
        try {
            URL urlLink = new URL(urlString);
            URLConnection connection = urlLink.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Window NT 6.1; Win64; x64)");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(BaseCrawler.class.getName()).log(Level.SEVERE, "Connection EROR");
        } catch (ConnectException c){
            if(c.getMessage().contains("resfused")){
                System.out.println("Connection Resfused");
            }
        }
        return reader;

    }

    protected XMLEventReader parseStringToXMLEventReader(String xmlSection)
            throws UnsupportedEncodingException, XMLStreamException {
        byte[] byteArray = xmlSection.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(inputStream);
        return reader;
    }

    abstract public void startCrawl();

}
