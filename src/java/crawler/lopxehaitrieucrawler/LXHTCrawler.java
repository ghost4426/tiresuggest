/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.lopxehaitrieucrawler;

import crawler.BaseCrawler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Gian Tran
 */
public class LXHTCrawler extends BaseCrawler {

    private final String url;
    private final String brandName;

    public LXHTCrawler(String url, String brandName) {
        this.url = url;
        this.brandName = brandName;
    }

    private int getLastPage(String document)
            throws UnsupportedEncodingException, XMLStreamException {
        int number = 1;
        int tmp = 0;
        String link = "";
        document = document.trim().replaceAll("&laquo;", "").replaceAll("&#8249; ", "").replace("&#8250;", "").replace("&raquo;", "");
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        String tagName = "";
        while (eventReader.hasNext()) {
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    Attribute seAttr = startElement.getAttributeByName(new QName("class"));
                    if (seAttr.getValue().equals("last paginate_button paginate_button_disabled")) {
                        Attribute href = startElement.getAttributeByName(new QName("href"));
                        link = href.getValue().trim();
                    }
                }
            }
        }

        String regex = "[0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            String result = matcher.group();
            try {
                number = Integer.parseInt(result);
            } catch (Exception e) {
                Logger.getLogger(LXHTCrawler.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return number;
    }

    @Override
    public void startCrawl() {
        BufferedReader reader;
        try {
            reader = getBufferReaderForURL(url + "&p=999");
            String line = "";
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("phantrang")) {
                    isStart = true;
                }
                if (isStart) {
                    document += line;
                    if (line.contains("</div>")) {
                        break;
                    }
                }
            }
            int lastPage = getLastPage(document);
            for (int i = 0; i < lastPage; i++) {
                String pageUrl = url + "&p=" + (i + 1);
                new LXHTEachPageCrawler(pageUrl, brandName).startCrawl();
                System.out.println("Parsed URL: " + pageUrl);
            }
        } catch (IOException | XMLStreamException e) {
            Logger.getLogger(LXHTCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
