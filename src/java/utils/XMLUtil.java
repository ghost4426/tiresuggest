/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import dto.Tire;
import java.io.Serializable;
import javax.servlet.ServletContext;

/**
 *
 * @author Gian Tran
 */
public class XMLUtil implements Serializable {

    public static boolean validateWithSchema(Tire tire, ServletContext context) {
        try {
            String xml = marshall(tire);
            String schemaPath = "schema/tire.xsd";
            Schema schema;
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            schema = sf.newSchema(new File(context.getRealPath("/" + schemaPath)));
            Validator validator = schema.newValidator();

            InputSource inputFile = new InputSource(new StringReader(xml));
            validator.validate(new SAXSource(inputFile));

//            System.out.println("Validated: " + tire.getName());
            return true;
        } catch (SAXException | IOException ex) {
            System.out.println("----------------------------");
            System.out.println("Validated fail: " + tire.getName());
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static String marshall(Tire tire) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(Tire.class);
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(tire, sw);
        } catch (JAXBException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sw.toString();
    }

}
