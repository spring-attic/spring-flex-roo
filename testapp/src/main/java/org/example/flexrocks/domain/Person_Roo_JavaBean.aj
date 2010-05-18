package org.example.flexrocks.domain;

import java.lang.String;
import java.util.Set;
import org.example.flexrocks.domain.Person;

privileged aspect Person_Roo_JavaBean {
    
    public String Person.getName() {
        return this.name;
    }
    
    public void Person.setName(String name) {
        this.name = name;
    }
    
    public Set<Person> Person.getChildren() {
        return this.children;
    }
    
    public void Person.setChildren(Set<Person> children) {
        this.children = children;
    }
    
}
