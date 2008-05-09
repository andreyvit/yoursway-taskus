package com.mkalugin.basecamp;

import static com.mkalugin.utils.UrlUtils.appendPath;
import static com.mkalugin.utils.XmlUtils.childElements;
import static com.mkalugin.utils.XmlUtils.parseXmlString;

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
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ToDoList;

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
    
    public Collection<Project> listProjects() throws IOException, ParserConfigurationException, SAXException {
        return createParser().parseProjects(childElements(query("/project/list")));
    }
    
    public Collection<ToDoList> listToDoLists(Project project) throws IOException, ParserConfigurationException, SAXException {
        return listToDoLists(project.getId());
    }
    
    public Collection<ToDoList> listToDoLists(int projectId) throws IOException, ParserConfigurationException, SAXException {
        return createParser().parseToDoLists(childElements(query("/projects/" + projectId + "/todos/lists")));
    }

    public ToDoList readToDoList(ToDoList list) throws IOException, ParserConfigurationException, SAXException {
        return readToDoList(list.getId());
    }

    public ToDoList readToDoList(int listId) throws IOException, ParserConfigurationException, SAXException {
        return createParser().parseToDoListWithItems(query("/todos/list/" + listId));
    }
    
    private Element query(String relativePath) throws HttpException, IOException,
            ParserConfigurationException, SAXException {
        PostMethod post = new PostMethod(appendPath(url, relativePath).toExternalForm());
        post.setRequestHeader("Accept", "application/xml");
        StringRequestEntity entity = new StringRequestEntity("<request></request>", "application/xml",
                "utf-8");
        post.setRequestEntity(entity);
        
        int result = client.executeMethod(post);
        String resp = post.getResponseBodyAsString();
        if (result != 200)
            throw new IOException("Failed to communicate with Basecamp: result is " + result + "(url was "
                    + relativePath + ")\n" + resp);
        System.out.println(resp);
        return parseXmlString(resp).getDocumentElement();
    }
    
    private BasecampResultsParser createParser() {
        return new BasecampResultsParser();
    }
    
}
