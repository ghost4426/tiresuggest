/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.lopxehaitrieucrawler;

import crawler.BaseCrawler;
import dao.TireDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import dto.Tire;
import javax.servlet.ServletContext;
import utils.Util;
import utils.XMLUtil;

/**
 *
 * @author Gian Tran
 */
public class LXHTEachPageCrawler extends BaseCrawler {

    private String url;
    private String BrandName;
    private ServletContext context;

    public LXHTEachPageCrawler(String url, String BrandName, ServletContext context) {
        this.url = url;
        this.BrandName = BrandName;
        this.context = context;
    }

    public void startCrawl() {
        BufferedReader reader = null;
        try {
            reader = getBufferReaderForURL(url);
            String line = "";
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("col-right")) {
                    isStart = true;
                }
                if (line.contains("End col-right") && isStart) {
                    isStart = false;
                }
                if (isStart) {
                    document += line;
                }
            }
            document += "</div>";
            stAXParserForEachPage(document);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LXHTEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(LXHTEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stAXParserForEachPage(String document)
            throws UnsupportedEncodingException, XMLStreamException, IOException {
        document = document.trim().replaceAll("&laquo;", "").replaceAll("&#8249; ", "").replace("&#8250;", "").replace("&raquo;", "").replaceAll("&", "&amp;");
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        String imgLink = "";
        String price = "";
        String name = "";
        String size = "";
        boolean isStart = false;
        while (eventReader.hasNext()) {
            String tagName = "";
            Characters chars = null;
            StartElement startElement = null;
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("div".equals(tagName)) {
                    Attribute divClass = startElement.getAttributeByName(new QName("class"));
                    if (divClass != null) {
                        if ("item_product_tires".equals(divClass.getValue())) {
                            isStart = true;
                        }
                    }
                }

                if ("div".equals(tagName) && isStart) {
                    while (startElement.getAttributeByName(new QName("src")) == null) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                        }
                    }
                    imgLink = startElement.getAttributeByName(new QName("src")).getValue();

                    while (startElement.getAttributeByName(new QName("href")) == null) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                        }
                    }
                    event = (XMLEvent) eventReader.next();
                    chars = event.asCharacters();
                    name = chars.getData();

                    while (true) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                            Attribute att = startElement.getAttributeByName(new QName("class"));
                            if (att != null) {
                                if (att.getValue().equals("name")) {
                                    break;
                                }
                            }
                        }
                    }
                    event = (XMLEvent) eventReader.next();
                    if (event.isCharacters()) {
                        chars = event.asCharacters();
                        size = chars.getData();
                    }

                    while (true) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                            Attribute att = startElement.getAttributeByName(new QName("class"));
                            if (att != null) {
                                if (att.getValue().equals("price")) {
                                    break;
                                }
                            }
                        }
                    }
                    eventReader.next();
                    eventReader.next();
                    event = (XMLEvent) eventReader.next();
                    chars = event.asCharacters();
                    price = chars.getData();
                    size = wellFormSize(size);
                    String filePath = "";
                    if (size.endsWith("")) {
                        filePath = BrandName + "_" + size + ".jpg";
                    } else {
                        filePath = name + ".jpg";
                    }
                    filePath = Util.saveImage("http://www.lopxehaitrieu.com/" + imgLink.trim(), filePath, context);
                    Date date = new Date();
                    Tire tire = new Tire((int) date.getTime(), name.replaceAll("\\t", " ").trim(), BrandName, size, price, Boolean.FALSE, filePath);
                    tire.setId(date.getTime());
                    XMLUtil u = new XMLUtil();
                    if (u.validateWithSchema(tire, context)) {
                        tire.setIsValidate(Boolean.TRUE);
                    }
                    TireDAO dao = new TireDAO();
                    dao.addTire(tire);
                    isStart = false;
                }
            }
        }
    }

    private String wellFormSize(String size) {
        for (String s : size.split(":")) {
            if (!s.toLowerCase().trim().contains("kích thước")) {
                size = s;
            }
        }
        return size.trim().replaceAll("\\s", "");
    }
}
