package org.example.flexrocks.domain;

import javax.persistence.Entity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@Entity
@RooJavaBean
@RooToString
@RooEntity
public class Person {

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<org.example.flexrocks.domain.Person> children = new java.util.HashSet<org.example.flexrocks.domain.Person>();
    
    private void doStuff() {
    	
    }
}
