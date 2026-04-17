# Hospital A&E Management System

A concurrent Java simulation of a hospital A&E system with continuous patient arrivals, automated shift rotation, and parallel consultant processing.

## Features

- **Concurrent Patient Processing**: Simulates multiple patients being handled at the same time
- **Multithreading**: Uses Java threads to represent real-world parallel hospital operations
- **Thread-Safe Queues**: Manages patient flow safely using blocking queues :contentReference[oaicite:0]{index=0}
- **Producer–Consumer Pattern**: Demonstrates controlled patient generation and consultant handling
- **Speciality-Based Queues**: Organizes patients by medical speciality for more realistic routing :contentReference[oaicite:1]{index=1}
- **Shift Rotation Simulation**: Models automated consultant shift changes
- **Shared Resource Synchronization**: Ensures safe access to common resources in a concurrent environment
- **Performance Tracking**: Records patient arrival, waiting, and treatment times :contentReference[oaicite:2]{index=2}.

## Project Overview

This project simulates the workflow of a hospital Accident & Emergency department. Patients arrive continuously, are assigned to speciality queues, and are processed by consultants working in parallel. The system is designed to demonstrate key operating system and concurrent programming concepts in Java, including synchronization, safe thread communication, and task coordination.

## Core Functionalities

### Patient Management
- Each patient has:
  - A unique patient ID
  - Name
  - Required speciality
  - Arrival time
  - Treatment start time
  - Treatment end time :contentReference[oaicite:3]{index=3}

### Queue Management
- Patients are placed into speciality-specific queues
- Queues are implemented using `LinkedBlockingQueue` for thread safety
- Queue operations safely support concurrent producers and consumers :contentReference[oaicite:4]{index=4}

### Treatment Timing
- The system tracks:
  - Waiting time
  - Treatment duration
  - Total patient journey time
- Includes logic to check whether a patient meets the NHS 4-hour target in the simulation :contentReference[oaicite:5]{index=5}

## File Structure

```text
HOSPITAL_PATIENT_MANAGEMENT_SYSTEM/
├── Patient.java               # Represents patient details and timing information
├── PatientQueue.java          # Thread-safe speciality queue for patients
├── Consultant.java            # Handles patient treatment (if included in your project)
├── PatientProducer.java       # Simulates continuous patient arrivals (if included)
├── ShiftManager.java          # Handles consultant shift rotation (if included)
├── Main.java                  # Runs the simulation


