package patmal.workshops.model;
import java.util.ArrayList;
import java.util.List;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;


public class StudentImpl extends PersonImpl implements Student {

    private Team team;

    
    
    
    public StudentImpl(String id, String name, String email, Team team) {
        super(id, name, email);
        this.team = team;
    }

    public void setTeam(Team team) {
    this.team = team;
    }

    @Override
    public Instructor getInstructor() {
        if(team == null){
            return null;
        }
        return team.getWorkshop().getInstructor();
    }

    @Override
    public List<Student> getTeamMates() {
        if(team == null){
            return new ArrayList<>();
        }
        List<Student> mates = new ArrayList<>();
        for(Student s : team.getStudents()){
            if(!s.getId().equals(this.getId()))
            {
                mates.add(s);
            }
        }
        return mates;
    }

    @Override
    public String getTeamName() {
        if(team == null){
            return null;
        }
        return team.getName();
    }
    
}
