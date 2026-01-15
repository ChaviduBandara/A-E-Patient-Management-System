package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

public class Patient {
    private final int patientID;
    private final String name;
    private final String speciality;
    private final long arrivalTime;
    private long treatmentStartTime;
    private long treatmentEndTime;

    public Patient(int patientID, String name, String speciality){
        this.patientID = patientID;
        this.name = name;
        this.speciality = speciality;
        this.arrivalTime = System.currentTimeMillis();
        this.treatmentStartTime = 0;
        this.treatmentEndTime = 0;
    }

    public int getPatientID(){
        return patientID;
    }
    public String getName(){
        return name;
    }
    public String getSpeciality(){
        return speciality;
    }
    public long getArrivalTime(){
        return arrivalTime;
    }
    public long getWaitingTimeSeconds(){
        if (treatmentStartTime == 0){
            return (System.currentTimeMillis() - arrivalTime) / 1000;
        }else {
            return (treatmentStartTime - arrivalTime) / 1000;
        }
    }
    // wait time in minutes
    public long waitingTimeMinutes(){
        return getWaitingTimeSeconds() / 60;
    }

    // recording when treatment started
    public void setTreatmentStartTime(){
        this.treatmentStartTime = System.currentTimeMillis();
    }

    // recording when treatment ended
    public void setTreatmentEndTime(){
        this.treatmentEndTime = System.currentTimeMillis();
    }

    // checking if the patient met 4h NHS target
    public boolean metNHSTarget(){
        if (treatmentEndTime == 0){
            return false;
        }
        long totalJourneyMs = treatmentEndTime - arrivalTime;
        // 4000ms represents 4 simulated hours
        return totalJourneyMs <= 4000;
    }

    public long getTreatmentDurationSeconds(){
        if (treatmentStartTime == 0 || treatmentEndTime == 0){
            return 0;
        } else {
            return (treatmentEndTime - treatmentStartTime) / 1000;
        }
    }

    @Override
    public String toString(){
        return "Patient {" + "id=" + patientID + ", name='" + name + '\'' + ", speciality='" + speciality + '\'' + '}';
    }
}
