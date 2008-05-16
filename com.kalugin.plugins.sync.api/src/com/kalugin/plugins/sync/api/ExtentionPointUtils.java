package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ExtentionPointUtils {
    
    private static final class ElementTagNamePredicate implements Predicate<IConfigurationElement> {
        
        private final String tagName;
        
        public ElementTagNamePredicate(String tagName) {
            this.tagName = tagName;
        }
        
        public boolean apply(IConfigurationElement element) {
            return tagName.equals(element.getName());
        }
    }
    
    public static IConfigurationElement childElement(IConfigurationElement node, String tagName) throws XmlFormatException {
        IConfigurationElement result = childElementOrNull(node, tagName);
        if (result == null)
            throw new XmlFormatException("Tag " + tagName + " not found under " + node.getName());
        return result;
    }
    
    public static IConfigurationElement childElementOrNull(IConfigurationElement node, String tagName) throws XmlFormatException {
        IConfigurationElement result = null;
        for (IConfigurationElement element : filter(childElements(node), new ElementTagNamePredicate(tagName)))
            if (result == null)
                result = element;
            else
                throw new XmlFormatException("Multiple " + tagName + " elements not expected.");
        return result;
    }
    
    public static Iterable<IConfigurationElement> childElements(IConfigurationElement node, String tagName) {
        return filterByTag(childElements(node), tagName);
    }

    public static boolean hasChildElements(IConfigurationElement node, String tagName) {
        return childElements(node, tagName).iterator().hasNext();
    }

    public static Iterable<IConfigurationElement> filterByTag(Iterable<IConfigurationElement> elements, String tagName) {
        return filter(elements, new ElementTagNamePredicate(tagName));
    }
    
    public static Iterable<IConfigurationElement> childElements(IConfigurationElement node) {
        return asList(node.getChildren());
    }
    
    public static String stringValue(IConfigurationElement element, String attrName) throws XmlFormatException {
        String result = stringValueOrNull(element, attrName);
        if (result == null)
            throw new XmlFormatException("Element " + element.getName() + " must have attribute " + attrName);
        return result;
    }
    
    public static String stringValueOrNull(IConfigurationElement element, String attrName) throws XmlFormatException {
        return element.getAttribute(attrName);
    }
    
    public static boolean booleanValue(IConfigurationElement element, String attrName) throws XmlFormatException {
        return Boolean.valueOf(stringValue(element, attrName));
    }
    
    public static Boolean booleanValueOrNull(IConfigurationElement element, String attrName) throws XmlFormatException {
        String s = stringValue(element, attrName);
        return s == null ? null : Boolean.valueOf(s);
    }
    
    public static int intValue(IConfigurationElement element, String attrName) throws XmlFormatException {
        return Integer.valueOf(stringValue(element, attrName));
    }
    
    public static Integer intValueOrNull(IConfigurationElement element, String attrName) throws XmlFormatException {
        String s = stringValueOrNull(element, attrName);
        return s == null ? null : Integer.valueOf(s);
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
    
}
