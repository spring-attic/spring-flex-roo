package org.example.flexrocks.service;

import java.lang.Long;
import java.util.List;
import org.example.flexrocks.domain.Person;

privileged aspect PersonService_Roo_Service {
    
    public Person PersonService.create(Person person) {
        person.persist();
        return person;
    }
    
    public Person PersonService.show(Long id) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        return Person.findPerson(id);
    }
    
    /*public List<Person> PersonService.list() {
        return list(null, null);
    }*/
    
    public List<Person> PersonService.list(Integer page, Integer size) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            return Person.findPersonEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo);
        } else {
            return Person.findAllPeople();
        }
    }
    
    public Person PersonService.update(Person person) {
        if (person == null) throw new IllegalArgumentException("A person is required");
        person.merge();
        return person;
    }
    
    public void PersonService.delete(Long id) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        Person.findPerson(id).remove();
    }
    
}
