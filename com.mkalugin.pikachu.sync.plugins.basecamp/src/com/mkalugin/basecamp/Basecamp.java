package com.mkalugin.basecamp;

import static com.mkalugin.utils.UrlUtils.appendPath;
import static com.mkalugin.utils.XmlUtils.childElements;
import static com.mkalugin.utils.XmlUtils.parseXmlString;
import static org.apache.commons.lang.StringEscapeUtils.escapeXml;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ToDoItem;
import com.mkalugin.basecamp.model.ToDoList;
import com.mkalugin.utils.XmlUtils.XmlFormatException;

public class Basecamp {
    
    private final URL url;
    private HttpClient client;
    
    public Basecamp(URL url, String userName, String password) {
        if (url == null)
            throw new NullPointerException("url is null");
        if (userName == null)
            throw new NullPointerException("userName is null");
        if (password == null)
            throw new NullPointerException("password is null");
        this.url = url;
        this.client = createHttpClient(userName, password);
    }
    
    private HttpClient createHttpClient(String userName, String password) {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        UsernamePasswordCredentials cred = new UsernamePasswordCredentials(userName, password);
        client.getState().setCredentials(
                new AuthScope("yoursway.seework.com", AuthScope.ANY_PORT, AuthScope.ANY_REALM), cred);
        return client;
    }
    
    public Collection<Project> listProjects() throws BasecampException {
        try {
            return createParser().parseProjects(childElements(query("/project/list")));
        } catch (XmlFormatException e) {
            throw new BasecampException(e);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    public Collection<ToDoList> listToDoLists(Project project) throws BasecampException {
        return listToDoLists(project.getId());
    }
    
    public Collection<ToDoList> listToDoLists(int projectId) throws BasecampException {
        try {
            return createParser().parseToDoLists(childElements(query("/projects/" + projectId + "/todos/lists")));
        } catch (XmlFormatException e) {
            throw new BasecampException(e);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }

    public ToDoList readToDoList(ToDoList list) throws BasecampException {
        return readToDoList(list.getId());
    }

    public ToDoList readToDoList(int listId) throws BasecampException {
        try {
            return createParser().parseToDoListWithItems(query("/todos/list/" + listId));
        } catch (XmlFormatException e) {
            throw new BasecampException(e);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    public void complete(ToDoItem item) throws BasecampException {
        complete(item.getId());
    }
    
    public void complete(int itemId) throws BasecampException {
        try {
            query("/todos/complete_item/" + itemId);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    public void uncomplete(ToDoItem item) throws BasecampException  {
        uncomplete(item.getId());
    }
    
    public void uncomplete(int itemId) throws BasecampException  {
        try {
            query("/todos/uncomplete_item/" + itemId);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    public void deleteItem(ToDoItem item) throws BasecampException  {
        deleteItem(item.getId());
    }
    
    public void deleteItem(int itemId) throws BasecampException  {
        try {
            query("/todos/delete_item/" + itemId);
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    public ToDoItem rename(ToDoItem item, String newText) throws BasecampException  {
        int itemId = item.getId();
        try {
            return createParser().parseToDoItem(
                    query("/todos/update_item/" + itemId, buildUpdateRequest(item, newText)));
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }

    private String buildUpdateRequest(ToDoItem item, String newText) {
        StringBuilder builder = new StringBuilder();
        builder.append("<item><content>").append(escapeXml(newText)).append("</content></item>");
        // have to add a responsible party here, or else it will be removed
        if (item.getResponsiblePartyId() != null)
            builder.append("<responsible-party>").append(
                    item.getResponsiblePartyId().getResponsiblePartyForToDoItemUpdate()).append(
                    "</responsible-party>");
        return builder.toString();
    }
    
    public ToDoItem createItem(ToDoList item, String newText) throws BasecampException  {
        return createItem(item.getId(), newText);
    }
    
    public ToDoItem createItem(int listId, String newText) throws BasecampException  {
        try {
            return createParser().parseToDoItem(query("/todos/create_item/" + listId, "<content>" + escapeXml(newText)
                    + "</content>"));
        } catch (IOException e) {
            throw new BasecampException(e);
        } catch (ParserConfigurationException e) {
            throw new BasecampException(e);
        } catch (SAXException e) {
            throw new BasecampException(e);
        }
    }
    
    private Element query(String relativePath) throws HttpException, IOException,
    ParserConfigurationException, SAXException {
        return query(relativePath, "");
    }
    
    private Element query(String relativePath, String request) throws HttpException, IOException,
            ParserConfigurationException, SAXException {
        PostMethod post = new PostMethod(appendPath(url, relativePath).toExternalForm());
        post.setRequestHeader("Accept", "application/xml");
        StringRequestEntity entity = new StringRequestEntity("<request>" + request + "</request>",
                "application/xml", "utf-8");
        post.setRequestEntity(entity);
        
        int result = client.executeMethod(post);
        String resp = post.getResponseBodyAsString();
        if (result != 200 && result != 201)
            throw new IOException("Failed to communicate with Basecamp: result is " + result + "(url was "
                    + relativePath + ")\n" + resp);
        System.out.println(resp);
        return parseXmlString(resp).getDocumentElement();
    }
    
    private BasecampResultsParser createParser() {
        return new BasecampResultsParser();
    }
    
}
