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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import utils.Util;

/**
 *
 * @author Gian Tran
 */
public class TTVXCategoriesCrawler extends BaseCrawler {

    public Map<String, String> getCategories(String url) throws UnsupportedEncodingException, IOException {
        BufferedReader reader = null;
        String document = "";
        Map<String, String> categories = new HashMap<>();
        try {
            reader = getBufferReaderForURL(url);
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("ul class=\"sf-menu2\"")) {
                    document += line.trim();
                    break;
                }
            }
            categories = stAXParserForCategories(document);

        } catch (IOException | XMLStreamException e) {
            Logger.getLogger(TTVXCategoriesCrawler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Logger.getLogger(BaseCrawler.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return categories;
    }

    public Map<String, String> stAXParserForCategories(String document) throws UnsupportedEncodingException, XMLStreamException {
        document = document.trim().replaceAll("&", "&amp;").replaceAll("class=\"\"", "");
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        Map<String, String> categories = new HashMap<>();
        while (eventReader.hasNext()) {
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String tagName = startElement.getName().getLocalPart();
                if ("a".equals(tagName)) {
                    Attribute attrHref = startElement.getAttributeByName(new QName("href"));
                    String link = attrHref.getValue();
                    if (link.contains("vo-xe-du-lich") && !link.contains("vo-xe-du-lich/10.html")) {
                        event = (XMLEvent) eventReader.next();
                        StartElement se = event.asStartElement();
                        if (se.getName().getLocalPart().equals("span")) {
                            event = (XMLEvent) eventReader.next();
                            Characters character = event.asCharacters();
                            categories.put(Util.getBrand(character.getData()), link);
                        }
                    }
                }
            }
        }
        return categories;
    }

    @Override
    public void startCrawl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
