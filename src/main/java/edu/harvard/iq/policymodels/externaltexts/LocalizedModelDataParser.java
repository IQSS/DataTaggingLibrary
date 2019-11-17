package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.model.metadata.AuthorData;
import edu.harvard.iq.policymodels.model.metadata.GroupAuthorData;
import edu.harvard.iq.policymodels.model.metadata.ModelReference;
import edu.harvard.iq.policymodels.model.metadata.PersonAuthorData;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import static java.util.stream.Collectors.joining;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Parses the XML file for policy model data.
 *
 * @author michael
 */
public class LocalizedModelDataParser {

    String language;
    LocalizedModelData model;
    AuthorData currentAuthor;
    ModelReference currentReference;

    public LocalizedModelDataParser(String language) {
        this.language = language;
    }

    
    /**
     * Reads the XML in {@code xml} into a model.
     * @param xml The content of the policy-model.xml file.
     * @return The policy model, parsed.
     * @throws LocalizationException
     */   
    public LocalizedModelData read(String xml) throws LocalizationException {
        try {
            model = new LocalizedModelData();
            model.setLanguage(language);
            
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader reader = parser.getXMLReader();
            
            reader.setContentHandler(new LocalizedModelContentHandler());
            try (StringReader sr = new StringReader(xml) ) {
                reader.parse(new InputSource(sr));
            }
            
            return model;
            
        } catch (IOException ex) {
            throw new LocalizationException(language, "Cannot read localization: " + ex.getLocalizedMessage(), ex);
        } catch (ParserConfigurationException ex) {
            throw new LocalizationException(language, "Cannot load localization due to parse configuration error: " + ex.getLocalizedMessage(), ex);
        } catch (SAXException ex) {
            throw new LocalizationException(language, "Error parsing localization XML: " + ex.getLocalizedMessage(), ex);
        }
    }
    
    public LocalizedModelData read(Path pathToXml) throws LocalizationException {
        try {
            return read( Files.readAllLines(pathToXml, StandardCharsets.UTF_8).stream().collect(joining("\n")) );
        } catch (IOException ex) {
            throw new LocalizationException(language, "Cannot read localization: " + ex.getLocalizedMessage(), ex);
        }
    }
    
    
    class LocalizedModelContentHandler implements ContentHandler {

        StringBuilder sb = new StringBuilder();
        Locator docLoc;
        
        @Override
        public void setDocumentLocator(Locator locator) {
            docLoc = locator;
        }

        @Override
        public void startDocument() throws SAXException {}

        @Override
        public void endDocument() throws SAXException {}

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            switch (qName) {
                case "localized-model":
                    String direction = atts.getValue("direction");
                    if ( direction!=null ) {
                        model.setDirection(LocalizedModelData.Direction.valueOf(direction.toUpperCase()));
                    }
                    String uiLang = atts.getValue("ui-lang");
                    if ( uiLang!=null ) {
                        model.setUiLanguage(uiLang);
                    }
                    break;
                    
                case "person":
                    PersonAuthorData pad = new PersonAuthorData();
                    pad.setOrcid(atts.getValue("orcid"));
                    currentAuthor = pad;
                    model.add(currentAuthor);
                    break;

                case "group":
                    currentAuthor = new GroupAuthorData();
                    model.add(currentAuthor);
                    break;

                case "reference":
                    currentReference = new ModelReference();
                    currentReference.setDoi(atts.getValue("doi"));
                    currentReference.setUrl(atts.getValue("url"));
                    model.add(currentReference);
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch( qName ) {
                case "title":
                    model.setTitle(chars());
                    break;
                
                case "subtitle":
                    model.setSubTitle(chars());
                    break;
                    
                case "keywords":
                    Arrays.stream(sb.toString().split(","))
                            .map(w->w.trim().toLowerCase())
                            .forEach(model::addKeyword);
                    break;
                  
                case "name":
                    currentAuthor.setName(chars());
                    break;
                    
                case "affiliation":
                    ((PersonAuthorData)currentAuthor).setAffiliation(chars());
                    break;
                    
                case "email":
                    ((PersonAuthorData)currentAuthor).setEmail(chars());
                    break;
                    
                case "contact":
                    ((GroupAuthorData)currentAuthor).setContact(chars());
                    break;
                    
                case "reference":
                    currentReference.setText(chars());
                    break;
            }
            sb.setLength(0); 
        }

        private String chars() {
            return sb.toString().trim();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            sb.append(ch, start, length);
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
        }
        
        
    }

}
