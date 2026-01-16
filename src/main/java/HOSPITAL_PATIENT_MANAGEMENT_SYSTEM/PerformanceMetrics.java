package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMetrics {
    private final AtomicInteger totalPatientsArrived = new AtomicInteger(0);
    private final AtomicInteger totalPatientsTreated = new AtomicInteger(0);
    private final AtomicInteger patientsMetTarget = new AtomicInteger(0);
    private final AtomicInteger patientsBreachedTarget = new AtomicInteger(0);

    // Tracking wait time
    private final AtomicLong totalWaitTimeSeconds = new AtomicLong(0);
    private final AtomicLong totalTreatmentTimeSeconds = new AtomicLong(0);

    // Per-speciality statistics
    private final AtomicInteger paediatricTreated  = new AtomicInteger(0);
    private final AtomicInteger surgeryTreated = new AtomicInteger(0);
    private final AtomicInteger cardiologyTreated = new AtomicInteger(0);

    private final AtomicInteger paediatricWaiting = new AtomicInteger(0);
    private final AtomicInteger surgeryWaiting = new AtomicInteger(0);
    private final AtomicInteger cardiologyWaiting = new AtomicInteger(0);

    // Timing
    private long simulationStartTime;
    private long simulationEndTime;

    // Storing the treated patients for data analysis
    private final List<Patient> treatedPatients = new ArrayList<>();

    // Getting the simulation start time
    public void startSimulation(){
        this.simulationStartTime = System.currentTimeMillis();
    }
    // Getting the simulation end time
    public void endSimulation(){
        this.simulationEndTime = System.currentTimeMillis();
    }
    // Recording the patient arrival
    public void recordArrival(String speciality){
        totalPatientsArrived.incrementAndGet();

        // Updating the waiting counter for specific speciality
        if (speciality.equals("Paediatrician")){
            paediatricWaiting.incrementAndGet();
        } else if (speciality.equals("Surgeon")) {
            surgeryWaiting.incrementAndGet();
        } else if (speciality.equals("Cardiologist")) {
            cardiologyWaiting.incrementAndGet();
        }
    }
    // Recording patient treatment completion
    public synchronized void recordTreatment(Patient patient){
        totalPatientsTreated.incrementAndGet();

        // updating the speciality count
        String speciality = patient.getSpeciality();
        if (speciality.equals("Paediatrician")){
            paediatricTreated.incrementAndGet();
            paediatricWaiting.decrementAndGet();
        } else if (speciality.equals("Surgeon")) {
            surgeryTreated.incrementAndGet();
            surgeryWaiting.decrementAndGet();
        } else if (speciality.equals("Cardiologist")) {
            cardiologyTreated.incrementAndGet();
            cardiologyWaiting.decrementAndGet();
        }

        // tracking the waiting time
        totalWaitTimeSeconds.addAndGet(patient.getWaitingTimeSeconds());
        totalTreatmentTimeSeconds.addAndGet(patient.getTreatmentDurationSeconds());

        // Checking the NHS 4h target
        if (patient.metNHSTarget()){
            patientsMetTarget.incrementAndGet();
        } else {
            patientsBreachedTarget.incrementAndGet();
        }

        // storing the patient for data analysis
        treatedPatients.add(patient);
    }

    // calculating NHS target compliance as a percentage
    public double getNHSTragetCompliancePercentage(){
        int total = totalPatientsTreated.get();
        if (total == 0){
            return 0.0;
        } else {
            return (patientsMetTarget.get() * 100.0) / total;
        }
    }

    // calculating the average wait time
    public double getAvergaeWaitTimeMinutes(){
        int total = totalPatientsTreated.get();
        if (total == 0){
            return 0.0;
        } else {
            return (totalWaitTimeSeconds.get() / 60.0) / total;
        }
    }

    // calculating the average treatment time
    public double getAverageTreatmentTimeMinutes(){
        int total = totalPatientsTreated.get();
        if (total == 0){
            return 0.0;
        } else {
            return (totalTreatmentTimeSeconds.get() / 60.0) / total;
        }
    }

    // calculating throughput (patients per hour)
    public double getThroughputPerHour() {
        long durationSeconds = (simulationEndTime - simulationStartTime) / 1000;
        if (durationSeconds == 0) return 0.0;
        double hours = durationSeconds / 3600.0;
        return totalPatientsTreated.get() / hours;
    }

    // getting the total simulation in seconds
    public long getSimulationDurationSeconds(){
        return (simulationEndTime - simulationStartTime) / 1000;
    }

    public void displayPerformanceReport() {
        System.out.println("         PERFORMANCE METRICS & NHS TARGET COMPLIANCE");
        System.out.println("--------------------------------------------------------");
        System.out.println("   Total Patients Arrived: " + totalPatientsArrived.get());
        System.out.println("   Total Patients Treated: " + totalPatientsTreated.get());
        System.out.println("   Patients Still Waiting: " + (totalPatientsArrived.get() - totalPatientsTreated.get()));
        System.out.println("   Total Simulation Duration: " + getSimulationDurationSeconds() + " seconds");
        System.out.println("   Throughput: " + String.format("%.2f", getThroughputPerHour()) + " patients/hour");

        // NHS 4-Hour Target Compliance
        double compliancePercentage = getNHSTragetCompliancePercentage();
        System.out.println("\n NHS 4-HOUR TARGET COMPLIANCE:");
        System.out.println("   Target: 95% of patients seen within 4 hours");
        System.out.println("   -------------------------------------------");
        System.out.println("   Patients Met Target: " + patientsMetTarget.get() + " (" + String.format("%.2f%%", compliancePercentage) + ")");
        System.out.println("   Patients Breached Target: " + patientsBreachedTarget.get() + " (" + String.format("%.2f%%", 100 - compliancePercentage) + ")");

        // Compliance status
        if (compliancePercentage >= 95.0) {
            System.out.println("   Status: MEETING NHS TARGET (≥95%)");
        } else {
            System.out.println("   Status: BELOW NHS TARGET (<95%)");
            System.out.println("   Action Required: Increase staffing or improve efficiency");
        }

        // Wait Time Statistics
        System.out.println("\n WAIT TIME STATISTICS:");
        System.out.println("   Average Wait Time: " + String.format("%.2f", getAvergaeWaitTimeMinutes()) + " minutes");
        System.out.println("   Average Treatment Time: " + String.format("%.2f", getAverageTreatmentTimeMinutes()) + " minutes");
        System.out.println("   Total Patient Time: " + String.format("%.2f", (getAvergaeWaitTimeMinutes() + getAverageTreatmentTimeMinutes())) + " minutes");
        System.out.println();

        // Per-Speciality Breakdown
        System.out.println(String.format("   %-25s %3d  |  Still Waiting: %3d", "Paediatrician Treated:", paediatricTreated.get(), paediatricWaiting.get()));
        System.out.println(String.format("   %-25s %3d  |  Still Waiting: %3d", "Surgeon Treated:", surgeryTreated.get(), surgeryWaiting.get()));
        System.out.println(String.format("   %-25s %3d  |  Still Waiting: %3d", "Cardiologist Treated:", cardiologyTreated.get(), cardiologyWaiting.get()));

        System.out.println("\n========================================================");
    }

    public int getTotalPatientsArrived() {
        return totalPatientsArrived.get();
    }

    public int getTotalPatientsTreated() {
        return totalPatientsTreated.get();
    }

    public int getPatientsMetTarget() {
        return patientsMetTarget.get();
    }

    public int getPatientsBreachedTarget() {
        return patientsBreachedTarget.get();
    }
}
