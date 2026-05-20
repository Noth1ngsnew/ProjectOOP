package academic;
import java.io.Serializable;
import java.util.Objects;

public class Mark implements Serializable, Comparable<Mark> {
    private static final long serialVersionUID = 1L;

    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark() {
    }

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        if (firstAttestation < 0 || secondAttestation < 0 || finalExam < 0) {
            throw new IllegalArgumentException("Marks cannot be negative");
        }
        if (firstAttestation + secondAttestation > 60) {
            throw new IllegalArgumentException("The sum of first and second attestations cannot exceed 60");
        }
        if (finalExam > 40) {
            throw new IllegalArgumentException("Final exam mark cannot exceed 40");
        }
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
    }

    public double getTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }

    public String getLetterGrade() {
        double total = getTotal();
        if (total >= 95) return "A";
        if (total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";
        return "F";
    }

    public boolean isPassing() {
        return getTotal() >= 50;
    }

    public double getFirstAttestation() { return firstAttestation; }
    public double getSecondAttestation() { return secondAttestation; }
    public double getFinalExam() { return finalExam; }

    public void setFirstAttestation(double v) {
        if (v < 0) throw new IllegalArgumentException("Mark cannot be negative");
        if (v + this.secondAttestation > 60) {
            throw new IllegalArgumentException("Total attestation mark (1A + 2A) cannot exceed 60");
        }
        this.firstAttestation = v;
    }

    public void setSecondAttestation(double v) {
        if (v < 0) throw new IllegalArgumentException("Mark cannot be negative");
        if (this.firstAttestation + v > 60) {
            throw new IllegalArgumentException("Total attestation mark (1A + 2A) cannot exceed 60");
        }
        this.secondAttestation = v;
    }

    public void setFinalExam(double v) {
        if (v < 0 || v > 40) throw new IllegalArgumentException("Final exam mark must be between 0 and 40");
        this.finalExam = v;
    }

    @Override
    public int compareTo(Mark other) {
        return Double.compare(this.getTotal(), other.getTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;
        Mark m = (Mark) o;
        return Double.compare(firstAttestation, m.firstAttestation) == 0 &&
                Double.compare(secondAttestation, m.secondAttestation) == 0 &&
                Double.compare(finalExam, m.finalExam) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstAttestation, secondAttestation, finalExam);
    }

    @Override
    public String toString() {
        return String.format("%s (1A=%.1f, 2A=%.1f, F=%.1f, total=%.2f)",
                getLetterGrade(), firstAttestation, secondAttestation, finalExam, getTotal());
    }
}