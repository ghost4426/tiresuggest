/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.trungtamvoxecrawler;

import crawler.BaseCrawler;
import dao.TireDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import dto.Tire;
import java.util.Locale;
import utils.Util;
import utils.XMLUtil;

/**
 *
 * @author Gian Tran
 */
public class TTVXEachPageCrawler extends BaseCrawler {

    private String url;
    private String BrandName;

    public TTVXEachPageCrawler(String url, String BrandName) {
        this.url = url;
        this.BrandName = BrandName;
    }

    public void startCrawl() {
        BufferedReader reader = null;
        try {
            reader = getBufferReaderForURL(url);
            String line = "";
            String document = "<td><div>";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<div class=\"div_productcontent\">")) {
                    isStart = true;
                }
                if (isStart) {
                    document += line;
                }
                if (line.contains("</td>")) {
                    isStart = false;
                }
            }
            stAXParserForEachPage(document);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TTVXEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(TTVXEachPageCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stAXParserForEachPage(String document)
            throws UnsupportedEncodingException, XMLStreamException, IOException {
        document = document.trim().replaceAll("&", "&amp;").replaceAll("class=\"\"", "")
                .replaceAll(">\">", ">").replaceAll("title=\"<", "><").replaceAll(" << ", "")
                .replaceAll(" < ", "").replaceAll("<tr>", "<tr></tr></table>");
        XMLEventReader eventReader = parseStringToXMLEventReader(document);
        String imgLink = "";
        String price = "";
        String productName = "";
        boolean isStart = false;
        while (eventReader.hasNext()) {
            String tagName = "";
            XMLEvent event = (XMLEvent) eventReader.next();
            if (event.isStartElement()) {
                Characters chars = null;
                StartElement startElement = event.asStartElement();
                tagName = startElement.getName().getLocalPart();
                if ("div".equals(tagName)) {
                    Attribute divClass = startElement.getAttributeByName(new QName("class"));
                    if (divClass != null) {
                        if ("product".equals(divClass.getValue())) {
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

                    while (startElement.getAttributeByName(new QName("title")) == null) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                        }
                    }
                    productName = startElement.getAttributeByName(new QName("title")).getValue();
                    boolean isFound = true;
                    while (isFound) {
                        event = (XMLEvent) eventReader.next();
                        if (event.isStartElement()) {
                            startElement = event.asStartElement();
                            Attribute att = startElement.getAttributeByName(new QName("class"));
                            if (att != null) {
                                if (att.getValue().equals("price")) {
                                    isFound = false;
                                }
                            }
                        }
                    }
                    event = (XMLEvent) eventReader.next();
                    if (event.isCharacters()) {
                        chars = event.asCharacters();
                        price = chars.getData();
                    } else if (event.isStartElement()) {
                        event = (XMLEvent) eventReader.next();
                        price = event.asCharacters().getData();
                    }
                    if (!price.contains("L")) {
                        for (String p : price.split("\\s")) {
                            if (p.contains(",")) {
                                price = p;
                                break;
                            }
                        }
                    }
                    Date date = new Date();
                    Tire tire = getTireSizeAndName(productName);
                    tire.setId((int) date.getTime());
                    tire.setBrand(BrandName);
                    String filePath = "";
                    if (!tire.getSize().endsWith("")) {
                        filePath = BrandName + "_" + tire.getSize() + ".jpg";
                    } else {
                        filePath = tire.getName();
                    }
                    filePath = Util.saveImage(imgLink.trim(), filePath);
                    tire.setImgUrl(filePath);
                    tire.setPrice(price.trim());
                    if (XMLUtil.validateWithSchema(tire)) {
                        tire.setIsValidate(Boolean.TRUE);
                    }
                    TireDAO dao = new TireDAO();
                    dao.addTire(tire);
                    isStart = false;
                }
            }
        }
    }

    private Tire getTireSizeAndName(String productname) {
        String size = "";
        String name = "";
        String[] productNames = productname.split("\\s");
        for (String p : productNames) {
            if (p.contains("/") && (p.toUpperCase().contains("R")
                    || p.toUpperCase().contains("B") || p.toUpperCase().contains("D")
                    || p.toUpperCase().contains("E") || p.toUpperCase().contains("Z"))) {
                size = p;
                continue;
            }
            if (!p.equals("-")) {
                name += p + " ";
            }
        }

        return new Tire(0, Util.capitalizeEachWordInString(name.replaceAll("-", "").trim()), "", size.trim(), "", Boolean.FALSE, "");
    }
}
