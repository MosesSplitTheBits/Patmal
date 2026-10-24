package patmal.workshops.model;

import java.util.ArrayList;
import java.util.List;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

public class InstructorImpl extends PersonImpl implements Instructor  {

    private List<Workshop> workshops;

    

    public InstructorImpl(String id, String name, String email) {
        super(id, name, email);
        this.workshops = new ArrayList<>();
    }


    @Override
    public List<Workshop> getWorkshops() {
        return workshops;
    
    }

}