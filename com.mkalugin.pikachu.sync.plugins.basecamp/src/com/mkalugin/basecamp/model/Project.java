package com.mkalugin.basecamp.model;

public class Project {
    
    private final int id;
    private final String name;
    private final Company company;

    public Project(int id, String name, Company company) {
        if (name == null)
            throw new NullPointerException("name is null");
        if (company == null)
            throw new NullPointerException("company is null");
        this.id = id;
        this.name = name;
        this.company = company;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Company getCompany() {
        return company;
    }
    
}
