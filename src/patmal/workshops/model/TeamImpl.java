package patmal.workshops.model;
import java.util.ArrayList;
import java.util.List;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

public class TeamImpl implements Team{

    private Integer id;
    private String name;
    private List<Student> studentList;
    private Workshop workshop;

    
    public TeamImpl(Integer id, String name, Workshop workshop) {
        this.id = id;
        this.name = name;
        this.studentList = new ArrayList<>();
        this.workshop = workshop;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Student> getStudents() {
        return studentList;
    }

    @Override
    public Workshop getWorkshop() {
        return workshop;
    }
    
}
