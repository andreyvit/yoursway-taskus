package com.mkalugin.utils;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class XmlUtils {
    
    private static final CastToElement AS_ELEMENT = new CastToElement();
    
    private static final NodeTypePredicate IS_NODE = new NodeTypePredicate(Node.ELEMENT_NODE);
    
    private static final class CastToElement implements Function<Node, Element> {
        public Element apply(Node node) {
            return (Element) node;
        }
    }
    
    private static final class NodeTypePredicate implements Predicate<Node> {
        
        private final short type;
        
        public NodeTypePredicate(short type) {
            this.type = type;
        }
        
        public boolean apply(Node node) {
            return node.getNodeType() == type;
        }
    }
    
    private static final class ElementTagNamePredicate implements Predicate<Element> {
        
        private final String tagName;
        
        public ElementTagNamePredicate(String tagName) {
            this.tagName = tagName;
        }
        
        public boolean apply(Element element) {
            return tagName.equals(element.getTagName());
        }
    }
    
    public static Element childElement(Node node, String tagName) throws XmlFormatException {
        Element result = childElementOrNull(node, tagName);
        if (result == null)
            throw new XmlFormatException("Tag " + tagName + " not found under " + node.getNodeName());
        return result;
    }
    
    public static Element childElementOrNull(Node node, String tagName) throws XmlFormatException {
        Element result = null;
        for (Element element : filter(childElements(node), new ElementTagNamePredicate(tagName)))
            if (result == null)
                result = element;
            else
                throw new XmlFormatException("Multiple " + tagName + " elements not expected.");
        return result;
    }
    
    public static Iterable<Element> childElements(Node node, String tagName) {
        return filterByTag(childElements(node), tagName);
    }

    public static boolean hasChildElements(Node node, String tagName) {
        return childElements(node, tagName).iterator().hasNext();
    }

    public static Iterable<Element> filterByTag(Iterable<Element> elements, String tagName) {
        return filter(elements, new ElementTagNamePredicate(tagName));
    }
    
    public static Iterable<Element> childElements(Node node) {
        return transform(filter(childrenAsList(node), IS_NODE), AS_ELEMENT);
    }
    
    public static String stringValue(Element element, String tagName) throws XmlFormatException {
        return childElement(element, tagName).getTextContent();
    }
    
    public static String stringValueOrNull(Element element, String tagName) throws XmlFormatException {
        Element el = childElementOrNull(element, tagName);
        return el == null ? null : el.getTextContent();
    }
    
    public static boolean booleanValue(Element element, String tagName) throws XmlFormatException {
        return Boolean.valueOf(stringValue(element, tagName));
    }
    
    public static Boolean booleanValueOrNull(Element element, String tagName) throws XmlFormatException {
        String s = stringValue(element, tagName);
        return s == null ? null : Boolean.valueOf(s);
    }
    
    public static int intValue(Element element, String tagName) throws XmlFormatException {
        return Integer.valueOf(stringValue(element, tagName));
    }
    
    public static Integer intValueOrNull(Element element, String tagName) throws XmlFormatException {
        String s = stringValueOrNull(element, tagName);
        return (s == null || s.trim().length() == 0) ? null : Integer.valueOf(s);
    }
    
    public static List<Node> childrenAsList(Node node) {
        final NodeList children = node.getChildNodes();
        return new AbstractList<Node>() {
            
            @Override
            public Node get(int index) {
                return children.item(index);
            }
            
            @Override
            public int size() {
                return children.getLength();
            }
            
        };
    }
    
    public static class XmlFormatException extends RuntimeException {
        
        private static final long serialVersionUID = 1L;
        
        public XmlFormatException() {
            super();
        }
        
        public XmlFormatException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public XmlFormatException(String message) {
            super(message);
        }
        
        public XmlFormatException(Throwable cause) {
            super(cause);
        }
        
    }
    
    public static Document parseXmlString(String resp) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(resp)));
    }
    
}
