/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.trungtamvoxecrawler;

import crawler.BaseCrawler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TTVXCrawler extends BaseCrawler {

    private final String url;
    private final String brandName;

    public TTVXCrawler(String url, String brandName) {
        this.url = url;
        this.brandName = brandName;
    }

   

    private int getLastPage(String document)
            throws UnsupportedEncodingException, XMLStreamException {
        int number = 1;
        int tmp = 0;
        document = document.trim().replaceAll(" << ", "").replaceAll(" < ", "");
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        String tagName = "";
        while (eventReader.hasNext()) {
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    Attribute attrHref = startElement.getAttributeByName(new QName("href"));
                    if (attrHref != null && !attrHref.getValue().equals("#")) {
                        event = (XMLEvent) eventReader.next();
                        Characters chars = event.asCharacters();
                        try {
                            tmp = number;
                            number = Integer.parseInt(chars.getData().trim());
                        } catch (NumberFormatException e) {
                            number = tmp;
                        }
                    }
                }
            }
        }
        return number;
    }

    @Override
    public void startCrawl() {
        BufferedReader reader;
        try {
            reader = getBufferReaderForURL(url);
            String line = "";
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<div class=\"paging\">")) {
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
                String pageUrl = url + "?page" + (i + 1);
                new TTVXEachPageCrawler(pageUrl, brandName).startCrawl();
                System.out.println("Parsed URL: " + pageUrl);
            }
        } catch (IOException | XMLStreamException e) {
            Logger.getLogger(TTVXCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
