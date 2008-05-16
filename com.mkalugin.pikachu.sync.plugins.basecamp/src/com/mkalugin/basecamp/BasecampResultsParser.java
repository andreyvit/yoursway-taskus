package com.mkalugin.basecamp;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mkalugin.utils.XmlUtils.booleanValue;
import static com.mkalugin.utils.XmlUtils.childElement;
import static com.mkalugin.utils.XmlUtils.childElementOrNull;
import static com.mkalugin.utils.XmlUtils.childElements;
import static com.mkalugin.utils.XmlUtils.filterByTag;
import static com.mkalugin.utils.XmlUtils.intValue;
import static com.mkalugin.utils.XmlUtils.intValueOrNull;
import static com.mkalugin.utils.XmlUtils.stringValue;
import static com.mkalugin.utils.XmlUtils.stringValueOrNull;

import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.mkalugin.basecamp.model.Company;
import com.mkalugin.basecamp.model.CompanyId;
import com.mkalugin.basecamp.model.PersonId;
import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ReponsiblePartyId;
import com.mkalugin.basecamp.model.ToDoItem;
import com.mkalugin.basecamp.model.ToDoList;
import com.mkalugin.utils.XmlUtils;
import com.mkalugin.utils.XmlUtils.XmlFormatException;

public class BasecampResultsParser {
    
    private Map<Integer, Project> idsToProjects = newHashMap();
    
    private Map<Integer, Company> idsToCompanies = newHashMap();
    
    public Collection<Project> parseProjects(Iterable<Element> elements) throws XmlFormatException {
        return newArrayList(transform(filterByTag(elements, "project"), PARSE_PROJECT));
    }
    
    public Project parseProject(Element project) {
        int id = intValue(project, "id");
        Project result = idsToProjects.get(id);
        if (result == null) {
            result = parseProjectWithoutCache(project, id);
            idsToProjects.put(id, result);
        }
        return result;
    }
    
    private Project parseProjectWithoutCache(Element project, int id) {
        Company company = parseCompany(childElement(project, "company"));
        String name = stringValue(project, "name");
        return new Project(id, name, company);
    }
    
    public Company parseCompany(Element company) {
        int id = intValue(company, "id");
        Company result = idsToCompanies.get(id);
        if (result == null) {
            result = parseCompanyWithoutCache(company, id);
            idsToCompanies.put(id, result);
        }
        return result;
    }
    
    private Company parseCompanyWithoutCache(Element company, int id) {
        String name = stringValue(company, "name");
        return new Company(id, name);
    }
    
    public Collection<ToDoList> parseToDoLists(Iterable<Element> elements) throws XmlFormatException {
        return newArrayList(transform(filterByTag(elements, "todo-list"), PARSE_TODO_LIST));
    }
    
    public ToDoList parseToDoListWithoutItems(Element list) {
        return parseToDoList(list);
    }
    
    public ToDoList parseToDoListWithItems(Element list) {
        return parseToDoList(list);
    }
    
    private ToDoList parseToDoList(Element list) {
        int id = intValue(list, "id");
        String name = stringValue(list, "name");
        String description = stringValue(list, "description");
        Element itemsEl = childElementOrNull(list, "todo-items");
        Collection<ToDoItem> items = (itemsEl == null ? null : parseToDoItems(childElements(itemsEl)));
        return new ToDoList(id, name, description, items);
    }
    
    public Collection<ToDoItem> parseToDoItems(Iterable<Element> elements) throws XmlFormatException {
        return newArrayList(transform(filterByTag(elements, "todo-item"), PARSE_TODO_ITEM));
    }
    
    public ToDoItem parseToDoItem(Element item) {
        int id = intValue(item, "id");
        String content = stringValue(item, "content");
        int position = intValue(item, "position");
        boolean completed = booleanValue(item, "completed");
        Integer responsiblePartyId = intValueOrNull(item, "responsible-party-id");
        String responsiblePartyType = stringValueOrNull(item, "responsible-party-type");
        return new ToDoItem(id, position, completed, content, parseResponsibleParty(responsiblePartyType,
                responsiblePartyId));
    }
    
    private ReponsiblePartyId parseResponsibleParty(String responsiblePartyType,
            Integer responsiblePartyId) {
        if (responsiblePartyId == null)
            return null;
        else if (responsiblePartyType.equals("Person"))
            return new PersonId(responsiblePartyId);
        else if (responsiblePartyType.equals("Company"))
            return new CompanyId(responsiblePartyId);
        else
            throw new IllegalArgumentException("Unknown responsible-party-type: " + responsiblePartyType);
    }
    
    private final Function<Element, Project> PARSE_PROJECT = new Function<Element, Project>() {
        
        public Project apply(Element element) {
            return parseProject(element);
        }
        
    };
    
    private final Function<Element, ToDoList> PARSE_TODO_LIST = new Function<Element, ToDoList>() {
        
        public ToDoList apply(Element element) {
            return parseToDoListWithoutItems(element);
        }
        
    };
    
    private final Function<Element, ToDoItem> PARSE_TODO_ITEM = new Function<Element, ToDoItem>() {
        
        public ToDoItem apply(Element element) {
            return parseToDoItem(element);
        }
        
    };
    
}
