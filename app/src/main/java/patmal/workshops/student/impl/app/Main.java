package patmal.workshops.student.impl.app;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.logic.WorkshopSystemImpl;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("workshop-cli starting");
        WorkshopSystem system = new WorkshopSystemImpl();

        // demo data: 1 instructor, 2 workshops, 3 students, 1 team, 1 free student
        Instructor dana = system.createInstructor("i1", "Dana Cohen", "dana@uni.ac.il");
        Workshop java = system.createWorkshop("Java", "Intro to Java");
        Workshop maven = system.createWorkshop("Maven", "Build tools");
        system.associateWorkshopWithInstructor(dana, java);
        system.associateWorkshopWithInstructor(dana, maven);

        system.createStudent("s1", "Avi Levi", "avi@uni.ac.il");
        system.createStudent("s2", "Noa Bar", "noa@uni.ac.il");
        system.createStudent("s3", "Free Student", "free@uni.ac.il");
        system.createTeam("Team A", java, List.of("s1", "s2"));

        // export to the current folder
        File instructorsFile = new File("instructors.json");
        File studentsFile = new File("students.json");
        File teamsFile = new File("teams.json");

        system.exportInstructorsDataToJson(instructorsFile);
        log.info("Wrote {}", instructorsFile.getAbsolutePath());
        system.exportStudentsDataToJson(studentsFile);
        log.info("Wrote {}", studentsFile.getAbsolutePath());
        system.exportTeamsDataToJson(teamsFile);
        log.info("Wrote {}", teamsFile.getAbsolutePath());

        log.info("workshop-cli finished");
    }
}