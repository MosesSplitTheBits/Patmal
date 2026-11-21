package patmal.workshops.student.impl.logic;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.*;
import patmal.workshops.student.impl.model.InstructorImpl;
import patmal.workshops.student.impl.model.StudentImpl;
import patmal.workshops.student.impl.model.TeamImpl;
import patmal.workshops.student.impl.model.WorkshopImpl;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WorkshopSystemImpl implements WorkshopSystem {

    private int nextTeamId = 1;
    private int nextWorkshopId = 1;
    private Map<Integer, Workshop> workshops;
    private Map<String, Student> students;
    private Map<String, Instructor> instructors;
    private Map<Integer, Team> teams;
    private static final Logger log = LogManager.getLogger(WorkshopSystemImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public WorkshopSystemImpl() {
//        LinkedHash to list in order of creation for JSON.
        this.workshops = new LinkedHashMap<>();
        this.students = new LinkedHashMap<>();
        this.instructors = new LinkedHashMap<>();
        this.teams = new LinkedHashMap<>();
    }

    //    HERE START JACKSON NODES
    // {id, name, email}
    private static ObjectNode person(Person p) {
        ObjectNode n = mapper.createObjectNode();
        n.put("id", p.getId());
        n.put("name", p.getName());
        n.put("email", p.getEmail());
        return n;
    }

    // {id, name, description}
    private static ObjectNode workshopDetails(Workshop w) {
        ObjectNode n = mapper.createObjectNode();
        n.put("id", w.getId());
        n.put("name", w.getName());
        n.put("description", w.getDescription());
        return n;
    }

    // {id, name, description, instructor: {...} or null}
    private static ObjectNode workshopWithInstructor(Workshop w) {
        ObjectNode n = workshopDetails(w);
        if (w.getInstructor() == null) n.putNull("instructor");
        else n.set("instructor", person(w.getInstructor()));
        return n;
    }

    // writes the array to the file
    private static void write(File file, ArrayNode tree) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tree);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void exportInstructorsDataToJson(File targetFileName) {
        ArrayNode out = mapper.createArrayNode();
        for (Instructor i : instructors.values()) {
            ObjectNode in = person(i);
            ArrayNode ws = in.putArray("workshops");
            for (Workshop w : i.getWorkshops()) {
                ObjectNode wn = workshopDetails(w);
                ArrayNode ts = wn.putArray("teams");
                for (Team t : teams.values()) {
                    if (t.getWorkshop() == null || !t.getWorkshop().getId().equals(w.getId())) continue;
                    ObjectNode tn = ts.addObject();
                    tn.put("id", t.getId());
                    tn.put("name", t.getName());
                    ArrayNode ss = tn.putArray("students");
                    for (Student s : t.getStudents()) {
                        ss.add(person(s));
                    }
                }
                ws.add(wn);
            }
            out.add(in);
        }
        write(targetFileName, out);
    }

    @Override
    public void exportStudentsDataToJson(File targetFileName) {
        ArrayNode out = mapper.createArrayNode();
        for (Student s : students.values()) {
            ObjectNode sn = person(s);

            Team team = ((StudentImpl) s).getTeam();

            if (team == null) {
                sn.putNull("team");
            } else {
                ObjectNode tn = sn.putObject("team");
                tn.put("id", team.getId());
                tn.put("name", team.getName());
                ArrayNode ids = tn.putArray("studentIds");
                for (Student mate : team.getStudents()) {
                    ids.add(mate.getId());
                }
                tn.set("workshop", workshopWithInstructor(team.getWorkshop()));
            }
            out.add(sn);
        }
        write(targetFileName, out);
    }

    @Override
    public void exportTeamsDataToJson(File targetFileName) {
        ArrayNode out = mapper.createArrayNode();
        for (Team t : teams.values()) {
            ObjectNode tn = out.addObject();
            tn.put("id", t.getId());
            tn.put("name", t.getName());
            ArrayNode ss = tn.putArray("students");
            for (Student s : t.getStudents()) {
                ss.add(person(s));
            }
            tn.set("workshop", workshopWithInstructor(t.getWorkshop()));
        }
        write(targetFileName, out);
    }


    //EMAIL CHECK
    private static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }


    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        instructor.getWorkshops().add(workshop);
        ((WorkshopImpl) workshop).setInstructor(instructor);
        log.info("Assigned workshop {} to instructor {}", workshop.getId(), instructor.getId());

    }

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        //email check
        if (!isValidEmail(email)) {
            log.warn("Rejected instructor {}: invalid email '{}'", id, email);
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        if (instructors.containsKey(id)) {
            log.warn("Rejected instructor {}: id already exists", id);
            throw new IllegalArgumentException("Instructor ID already exists");
        }
        Instructor newInstructor = new InstructorImpl(id, name, email);
        instructors.put(id, newInstructor);
        log.info("Created instructor {} ({})", id, name);
        return newInstructor;
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        //email check
        if (!isValidEmail(email)) {
            log.warn("Rejected student {}: invalid email '{}'", id, email);
            throw new IllegalArgumentException("Invalid email: " + email);
        }

        if (students.containsKey(id)) {
            log.warn("Rejected student {}: id already exists", id);
            throw new IllegalArgumentException("Student ID already exists!");
        }
        Student newStudent = new StudentImpl(id, name, email, null);
        students.put(id, newStudent);
        log.info("Created student {} ({})", id, name);
        return newStudent;
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> studentIds) {
        //VALIDATION PHASE
        List<String> checkedIds = new ArrayList<>();
        for (String studentId : studentIds) {
            if (!students.containsKey(studentId)) {
                log.warn("Rejected team '{}': student {} does not exist", name, studentId);
                throw new IllegalArgumentException("Student does not exist!");
            }
            if (checkedIds.contains(studentId)) {
                log.warn("Rejected team '{}': student {} listed twice", name, studentId);
                throw new IllegalArgumentException("Student listed twice in team!");
            }
            if (students.get(studentId).getTeamName() != null) {
                log.warn("Rejected team '{}': student {} is already in a team", name, studentId);
                throw new IllegalArgumentException("Student is already in a team!");
            }
            checkedIds.add(studentId);
        }

        Team newTeam = new TeamImpl(nextTeamId++, name, workshop);
        for (String studentId : studentIds) {
            Student currStudent = students.get(studentId);
            newTeam.getStudents().add(currStudent);
            ((StudentImpl) currStudent).setTeam(newTeam);
        }
        teams.put(newTeam.getId(), newTeam);
        log.info("Created team {} '{}' with {} students", newTeam.getId(), name, studentIds.size());
        return newTeam;


    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        Workshop newWorkshop = new WorkshopImpl(nextWorkshopId++, name, description);
        workshops.put(newWorkshop.getId(), newWorkshop);
        log.info("Created workshop {} '{}'", newWorkshop.getId(), name);
        return newWorkshop;
    }

    @Override
    public List<Student> getFreeStudents() {
        List<Student> freeStudents = new ArrayList<>();

        for (Student student : students.values()) {
            if (student.getTeamName() == null) {
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
        for (Student student : students.values()) {
            Instructor currInstructor = student.getInstructor();
            if (currInstructor != null && currInstructor.getId().equals(instructorId)) {
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