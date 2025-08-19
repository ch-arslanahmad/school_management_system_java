package classroom;

import java.util.ArrayList;
import java.util.List;

public class Grade {
    // total
    List<Subjects> subjects = new ArrayList<>();

    public void addSubject(Subjects g) {
        subjects.add(g);
    }

    public int getTotalMarks() {
        int total = 0;
        for (Subjects s : subjects) {
            total = +s.getMarks();
        }
        return total;
    }

    public int getTotalObtMarks() {
        int total = 0;
        for (Subjects s : subjects) {
            total = +s.getObtmarks();
        }
        return total;
    }

    public float getTotalPercentage() {
        return (getTotalObtMarks() * 100f) / getTotalMarks();
    }

    // in grade class
    public char getGrade(double percentage) {
        Subjects s = new Subjects();
        return s.getGrade(percentage);
    }

}
