package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.PolicyModelData;
import edu.harvard.iq.datatags.model.metadata.AuthorData;
import edu.harvard.iq.datatags.model.metadata.GroupAuthorData;
import edu.harvard.iq.datatags.model.metadata.ModelReference;
import edu.harvard.iq.datatags.model.metadata.PersonAuthorData;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
public class PolicyModelDataParser {

    private static final Map<String, PolicyModelData.AnswerTransformationMode> ANSWERS_ORDER_MAP = new HashMap<>();

    static {
        ANSWERS_ORDER_MAP.put("yes-first", PolicyModelData.AnswerTransformationMode.YesFirst);
        ANSWERS_ORDER_MAP.put("yes-last", PolicyModelData.AnswerTransformationMode.YesLast);
        ANSWERS_ORDER_MAP.put("verbatim", PolicyModelData.AnswerTransformationMode.Verbatim);
    }

    PolicyModelData model;
    AuthorData currentAuthor;
    ModelReference currentReference;

    /**
     * Reads the XML in {@code xml} into a model.
     * @param xml The content of the policy-model.xml file.
     * @param pathToXml the path to policy-model.xml
     * @return The policy model, parsed.
     * @throws PolicyModelLoadingException
     */   
    public PolicyModelData read(String xml, Path pathToXml) throws PolicyModelLoadingException {
        try {
            model = new PolicyModelData();
            model.setMetadataFile(pathToXml);
            
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader reader = parser.getXMLReader();
            
            reader.setContentHandler(new PolicyModelContentHandler(pathToXml));
            try (StringReader sr = new StringReader(xml) ) {
                reader.parse(new InputSource(sr));
            }
            return model;
            
        } catch (IOException ex) {
            throw new PolicyModelLoadingException(pathToXml, "Cannot read model: " + ex.getLocalizedMessage(), ex);
        } catch (ParserConfigurationException ex) {
            throw new PolicyModelLoadingException(pathToXml, "Cannot load model due to parse configuration error: " + ex.getLocalizedMessage(), ex);
        } catch (SAXException ex) {
            throw new PolicyModelLoadingException(pathToXml, "Error parsing model XML: " + ex.getLocalizedMessage(), ex);
        }
    }
    
    public PolicyModelData read(Path pathToXml) throws PolicyModelLoadingException {
        try {
            if ( Files.isDirectory(pathToXml)) {
                pathToXml = pathToXml.resolve("policy-model.xml");
            }
            return read( Files.readAllLines(pathToXml, StandardCharsets.UTF_8).stream().collect(joining("\n")),
                    pathToXml );
        } catch (IOException ex) {
            throw new PolicyModelLoadingException(pathToXml, "Cannot read model: " + ex.getLocalizedMessage(), ex);
        }
    }
    
    
    class PolicyModelContentHandler implements ContentHandler {

        Path xmlFilePath;
        StringBuilder sb = new StringBuilder();
        Locator docLoc;

        public PolicyModelContentHandler(Path xmlFilePath) {
            this.xmlFilePath = xmlFilePath;
        }
        
        @Override
        public void setDocumentLocator(Locator locator) {
            docLoc = locator;
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            switch (qName) {
                case "version":
                    model.setDoi(atts.getValue("doi"));
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

                case "model":
                    String answerOrderValue = atts.getValue("answers-order");
                    if (answerOrderValue == null || answerOrderValue.trim().isEmpty()) {
                        // default value
                        model.setAnswerTransformationMode(PolicyModelData.AnswerTransformationMode.YesFirst);
                    } else {
                        PolicyModelData.AnswerTransformationMode mode = ANSWERS_ORDER_MAP.get(answerOrderValue.trim().toLowerCase());
                        if (mode == null) {
                            throw new SAXException("Illegal value for attribute 'answers-order' of node 'model': " + answerOrderValue);
                        }
                        model.setAnswerTransformationMode(mode);

                    }
                    break;

                case "space":
                    String rootSlotName = atts.getValue("root");
                    if (rootSlotName == null || rootSlotName.trim().isEmpty()) {
                        throw new SAXException("Node 'space' must specify a valid root for the policy space.");
                    }
                    model.setRootTypeName(rootSlotName);
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
            String tmpStr;
            switch( qName ) {
                case "title":
                    model.setTitle(chars());
                    break;
                
                case "subtitle":
                    model.setSubTitle(chars());
                    break;
                    
                case "version":
                    model.setVersion(chars());
                    break;
                    
                case "keywords":
                    Arrays.stream(sb.toString().split(","))
                            .map(w->w.trim().toLowerCase())
                            .forEach(model::addKeyword);
                    break;
                  
                case "date":
                    tmpStr = chars();
                    String[] comps = tmpStr.split("-");
                    model.setReleaseDate(LocalDate.of(
                                 Integer.parseInt(comps[0]),
                                 Integer.parseInt(comps[1]),
                                 Integer.parseInt(comps[2])));
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
                    
                case "space":
                    tmpStr = chars();
                    model.setPolicySpacePath(xmlFilePath.resolveSibling(tmpStr));
                    break;
                
                case "graph":
                    tmpStr = chars();
                    model.setDecisionGraphPath(xmlFilePath.resolveSibling(tmpStr));
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
