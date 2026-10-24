package patmal.workshops.model;
import patmal.workshops.api.model.Person;

public class PersonImpl implements Person{

    private String id;
    private String name;
    private String email;

    
    public PersonImpl(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
