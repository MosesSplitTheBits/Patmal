package patmal.workshops.model;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.api.model.Instructor;


public class WorkshopImpl implements Workshop{

    private Integer id;
    private String name;
    private String description;
    private Instructor instructor;



    public WorkshopImpl(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setInstructor(Instructor instructor)
    {
        this.instructor = instructor;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Instructor getInstructor() {
        return instructor;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
