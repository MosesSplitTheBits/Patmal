package patmal.workshops.logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.model.InstructorImpl;
import patmal.workshops.model.StudentImpl;
import patmal.workshops.model.TeamImpl;
import patmal.workshops.model.WorkshopImpl;

 public class WorkshopSystemImpl implements WorkshopSystem{

    private int nextTeamId = 1;
    private int nextWorkshopId = 1;
    private Map<Integer, Workshop> workshops;
    private Map<String, Student> students;
    private Map<String, Instructor> instructors;
    private Map<Integer, Team> teams;

    
    
    public WorkshopSystemImpl() {
        this.workshops = new HashMap<>();
        this.students = new HashMap<>();
        this.instructors = new HashMap<>();
        this.teams = new HashMap<>();
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        instructor.getWorkshops().add(workshop);
        ((WorkshopImpl)workshop).setInstructor(instructor);

    }

    @Override
    public Instructor createInstructor(String id, String name, String email) {
    
    if (instructors.containsKey(id)) {
            throw new IllegalArgumentException("Instructor ID already exists");
        }
        Instructor newInstructor = new InstructorImpl(id, name, email);
        instructors.put(id, newInstructor);
        return newInstructor;
        }

    @Override
    public Student createStudent(String id, String name, String email) {
        if(students.containsKey(id)){
            throw new IllegalArgumentException("Student ID already exists!");
        }
        Student newStudent = new StudentImpl(id, name, email, null);
        students.put(id, newStudent);
        return newStudent;
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> studentIds) {
        //VALIDATION PHASE
        List<String> checkedIds = new ArrayList<>();
        for(String studentId : studentIds){
            if(!students.containsKey(studentId)){
                throw new IllegalArgumentException("Student does not exist!");
            }
            if(checkedIds.contains(studentId)){
                throw new IllegalArgumentException("Student listed twice in team!");
            }
            if(students.get(studentId).getTeamName() != null){
                throw new IllegalArgumentException("Student is already in a team!");
            }
            checkedIds.add(studentId);
        }
        
        Team newTeam = new TeamImpl(nextTeamId++,name, workshop);
        for(String studentId : studentIds){
            Student currStudent = students.get(studentId);
            newTeam.getStudents().add(currStudent);
            ((StudentImpl) currStudent).setTeam(newTeam);
        }
        teams.put(newTeam.getId(), newTeam);
        return newTeam;


    }

    @Override
    public Workshop createWorkshop(String name, String description) {
    Workshop newWorkshop = new WorkshopImpl(nextWorkshopId++, name, description);
    workshops.put(newWorkshop.getId(), newWorkshop);
    return newWorkshop;
    }

    @Override
    public List<Student> getFreeStudents() {
        List<Student> freeStudents = new ArrayList<>();

        for(Student student : students.values()){
            if(student.getTeamName() == null){
                freeStudents.add(student);
            }
        }
        return freeStudents;
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructors.get(id);
    }

    @Override
    public List<Instructor> getInstructors() {
        return new ArrayList<>(instructors.values());
    }

    @Override
    public Student getStudent(String id) {
        return students.get(id);
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        List<Student> studentList = new ArrayList<>();
        for(Student student : students.values()){
            Instructor currInstructor = student.getInstructor();
            if(currInstructor != null && currInstructor.getId().equals(instructorId)){
                studentList.add(student);
            }
        }
        return studentList;
        
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
    List<Student> workshopStudents = new ArrayList<>();
    
    for (Team team : teams.values()) {
        Workshop teamWorkshop = team.getWorkshop();
        
        if (teamWorkshop != null && teamWorkshop.getId().equals(workshopId)) {
            workshopStudents.addAll(team.getStudents());
        }
    }
    
    return workshopStudents;
    }

    @Override
    public Team getTeam(Integer id) {
        return teams.get(id);
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public Workshop getWorkshop(Integer workshopId) {
        return workshops.get(workshopId);
    }

    @Override
    public List<Workshop> getWorkshops() {
        return new ArrayList<>(workshops.values());
    }

    }