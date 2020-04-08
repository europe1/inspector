package net.cararea.inspector;

import com.github.mikephil.charting.data.Entry;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.pressure.PressureCommand;
import com.github.pires.obd.commands.temperature.TemperatureCommand;
import com.github.pires.obd.enums.FuelType;

import net.cararea.inspector.db.DatabaseObject;
import net.cararea.inspector.db.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class TripRecord implements DatabaseObject<Trip> {
    private static final float GRAM_TO_LITRE_CNG = 128.2f;
    private static final float GRAM_TO_LITRE_ETHANOL = 789f;
    private static final float GRAM_TO_LITRE_PROPANE = 493f;
    private static final float GRAM_TO_LITRE_DIESEL = 850.8f;
    private static final float GRAM_TO_LITRE_GASOLINE = 748.9f;
    private static final float GRAM_TO_LITRE_METHANOL = 786.6f;
    private static final int MINUTE = 60000;

    private boolean isMafSupported = true;
    private boolean isTempPressureSupported = true;
    private Calendar tripStartCal;
    private Calendar tripEndCal;
    private float idleMaf;
    private float drivingMaf;
    private float intakeAirTemp;
    private float intakePressure;
    private float travelDistance;
    private float idlingDuration;
    private float drivingDuration;
    private float insFuelConsumption;
    private float idlingFuelConsumption;
    private float drivingFuelConsumption;
    private float fuelTypeValue = 14.7f;
    private float gramToLitre = GRAM_TO_LITRE_GASOLINE;
    private int speed;
    private int maxSpeed;
    private int engineRpm;
    private int maxEngineRpm;
    private int idleMafCount;
    private int drivingMafCount;
    private long addSpeed;
    private long tripStart;
    private long speedCount;
    private String oilTemp;
    private String absLoad;
    private String fuelLevel;
    private String dtcNumber;
    private String engineLoad;
//    private String equivRatio;
    private String throttlePos;
    private String coolantTemp;
    private String troubleCodes;
    private String vehicleIdNum;
    private String fuelPressure;
    private String airFuelRatio;
    private String distanceMilOn;
    private String engineRunningTime;
//    private String timingAdvance;
    private String relThrottlePos;
    private String engineFuelRate;
//    private String ambientAirTemp;
//    private String ignitionMonitor;
    private String fuelRailPressure;
//    private String timeSinceTcClear;
    private String describeProtocol;
//    private String barometricPressure;
    private String pendingTroubleCodes;
    private String fuelConsumptionRate;
    private String controlModuleVoltage;
    private String wideBandAirFuelRatio;
    private String permanentTroubleCodes;
//    private String describeProtocolNumber;
    private String distanceAfterCodesCleared;
    private UUID tripId;

    private static TripRecord instance;
    private ChartBuilder chart;

    private int speedChartX = 0;
    private int rpmChartX = 0;
    private List<Entry> rpmChart = new ArrayList<>();
    private List<Entry> speedChart = new ArrayList<>();

    private TripRecord() {
        tripStartCal = Calendar.getInstance();
        tripStart = System.currentTimeMillis();
        tripId = UUID.randomUUID();
    }

    public static TripRecord getInstance() {
        if (instance == null) instance = new TripRecord();
        return instance;
    }

    public void endTrip() {
        instance = null;
        tripEndCal = Calendar.getInstance();
    }

    public void setChart(ChartBuilder newChart) {
        chart = newChart;
    }

    List<Entry> getSpeedChart() { return speedChart; }
    List<Entry> getRpmChart() { return rpmChart; }

    public float getTravelDistance() { return travelDistance; }
    public String getAbsLoad() { return absLoad; }
    public String getOilTemp() { return oilTemp; }
    public String getDtcNumber() { return dtcNumber; }
    public String getFuelLevel() { return fuelLevel; }
    public String getEngineLoad() { return engineLoad; }
    public String getTroubleCodes() { return troubleCodes; }
    public String getCoolantTemp() { return coolantTemp; }
    public String getThrottlePos() { return throttlePos; }
    public String getAirFuelRatio() { return airFuelRatio; }
    public String getFuelPressure() { return fuelPressure; }
    public String getVehicleIdNum() { return vehicleIdNum; }
    public String getDistanceMilOn() { return distanceMilOn; }
    public String getEngineFuelRate() { return engineFuelRate; }
    public String getRelThrottlePos() { return relThrottlePos; }
    public String getDescribeProtocol() { return describeProtocol; }
    public String getFuelRailPressure() { return fuelRailPressure; }
    public String getEngineRunningTime() { return engineRunningTime; }
    public String getPendingTroubleCodes() { return pendingTroubleCodes; }
    public String getFuelConsumptionRate() { return fuelConsumptionRate; }
    public String getControlModuleVoltage() { return controlModuleVoltage; }
    public String getPermanentTroubleCodes() { return permanentTroubleCodes; }
    public String getWideBandAirFuelRatio() { return wideBandAirFuelRatio; }
    public String getDistanceAfterCodesCleared() { return distanceAfterCodesCleared; }
    public UUID getTripId() { return tripId; }

    public float getInsFuelConsumption() {
        return (isMafSupported || isTempPressureSupported) ? insFuelConsumption : 0;
    }

    public float getTotalFuelPrice() {
        if (isMafSupported || isTempPressureSupported) {
            return (idlingFuelConsumption + drivingFuelConsumption) * fuelTypeValue;
        } else {
            return 0;
        }
    }

    private void setSpeed(int currentSpeed) {
        setIdlingAndDrivingDuration(currentSpeed);
        speed = currentSpeed;
        if (maxSpeed < currentSpeed) maxSpeed = currentSpeed;

        if (speed != 0) {
            addSpeed += speed;
            speedCount++;
            travelDistance = (addSpeed / speedCount * (drivingDuration / (60 * MINUTE)));
        }
    }

    private void setIdlingAndDrivingDuration(int currentSpeed) {
        long currentTime = System.currentTimeMillis();
        if (speed == 0 && currentSpeed == 0) {
            idlingDuration = currentTime - tripStart - drivingDuration;
        }
        drivingDuration = currentTime - tripStart - idlingDuration;
    }

    private float calcMaf() {
        if (intakePressure > 0 && intakeAirTemp > 0) {
            float imap = ((engineRpm * intakePressure) / intakeAirTemp) / 2;
            return (imap / 60.0f) * (85.0f / 100.0f) * 2 * ((28.97f) / (8.314f));
        }
        return 0;
    }

    private void setInsFuelConsumption(float maf) {
        if (speed > 0)
            insFuelConsumption = 100 * (maf / (fuelTypeValue * gramToLitre) * 3600) / speed; // in  litre/100km
    }

    private void setIdleAndDrivingFuelConsumption(float currentMaf) {
        float literPerSecond;
        if (speed > 0) {
            drivingMaf += currentMaf;
            drivingMafCount++;
            literPerSecond = ((((drivingMaf / drivingMafCount) / fuelTypeValue) / gramToLitre));
            drivingFuelConsumption = (literPerSecond * (drivingDuration / 1000));
        } else {
            idleMaf += currentMaf;
            idleMafCount++;
            literPerSecond = ((((idleMaf / idleMafCount) / fuelTypeValue) / gramToLitre));
            idlingFuelConsumption = (literPerSecond * (idlingDuration / 1000));
        }
    }

    private void setEngineRpm(int rpm) {
        engineRpm = rpm;
        if (rpm > maxEngineRpm) {
            maxEngineRpm = rpm;
        }
    }

    private void setFuelTypeValue(String fuelType) {
        float fuelTypeValue = 0;
        if (FuelType.GASOLINE.getDescription().equals(fuelType)) {
            fuelTypeValue = 14.7f;
            gramToLitre = GRAM_TO_LITRE_GASOLINE;
        } else if (FuelType.PROPANE.getDescription().equals(fuelType)) {
            fuelTypeValue = 15.5f;
            gramToLitre = GRAM_TO_LITRE_PROPANE;
        } else if (FuelType.ETHANOL.getDescription().equals(fuelType)) {
            fuelTypeValue = 9f;
            gramToLitre = GRAM_TO_LITRE_ETHANOL;
        } else if (FuelType.METHANOL.getDescription().equals(fuelType)) {
            fuelTypeValue = 6.4f;
            gramToLitre = GRAM_TO_LITRE_METHANOL;
        } else if (FuelType.DIESEL.getDescription().equals(fuelType)) {
            fuelTypeValue = 14.6f;
            gramToLitre = GRAM_TO_LITRE_DIESEL;
        } else if (FuelType.CNG.getDescription().equals(fuelType)) {
            fuelTypeValue = 17.2f;
            gramToLitre = GRAM_TO_LITRE_CNG;
        }

        if (fuelTypeValue != 0) {
            this.fuelTypeValue = fuelTypeValue;
        }
    }

    public void updateTrip(String name, ObdCommand command) {
        Entry entry = null;
        switch (name) {
            // Basic
            case "Vehicle Speed":
                int vehicleSpeed = ((SpeedCommand) command).getMetricSpeed();
                setSpeed(vehicleSpeed);
                entry = new Entry(speedChartX++, vehicleSpeed);
                speedChart.add(entry);
                break;

            case "Engine RPM":
                int engineRpm = ((RPMCommand) command).getRPM();
                setEngineRpm(engineRpm);
                rpmChart.add(new Entry(rpmChartX++, engineRpm));
                break;

            case "Mass Air Flow":
                setInsFuelConsumption(Float.parseFloat(command.getFormattedResult()));
                isMafSupported = true;
                break;

            case "Fuel Level":
                fuelLevel = command.getFormattedResult();
                break;

            case "Fuel Type":
                setFuelTypeValue(command.getFormattedResult());
                break;

            case "Air Intake Temperature":
                intakeAirTemp = ((TemperatureCommand) command).getKelvin();
                float mafAit = calcMaf();
                setInsFuelConsumption(mafAit);
                setIdleAndDrivingFuelConsumption(mafAit);
                break;

            case "Trouble Codes":
                troubleCodes = command.getFormattedResult();
                break;

            case "Permanent Trouble Codes":
                permanentTroubleCodes = command.getFormattedResult();
                break;

            case "Pending Trouble Codes":
                pendingTroubleCodes = command.getFormattedResult();
                break;

            case "Diagnostic Trouble Codes":
                dtcNumber = command.getFormattedResult();
                break;

            case "Engine Load":
                engineLoad = command.getFormattedResult();
                break;

            case "Absolute load":
                absLoad = command.getFormattedResult();
                break;

            case "Vehicle Identification Number":
                vehicleIdNum = command.getFormattedResult();
                break;

            case "Air/Fuel Ratio":
                airFuelRatio = command.getFormattedResult();
                break;

            case "Wideband Air/Fuel Ratio":
                wideBandAirFuelRatio = command.getFormattedResult();
                break;

            case "Engine Fuel Rate":
                engineFuelRate = command.getFormattedResult();
                break;

            case "Fuel Pressure":
                fuelPressure = command.getFormattedResult();
                break;

            // Advanced
            case "Engine Runtime":
                engineRunningTime = command.getFormattedResult();
                break;

            case "Intake Manifold Pressure":
                intakePressure = ((PressureCommand) command).getMetricUnit();
                float mafImp = calcMaf();
                setInsFuelConsumption(mafImp);
                setIdleAndDrivingFuelConsumption(mafImp);
                break;

            case "Fuel Consumption Rate":
                fuelConsumptionRate = command.getFormattedResult();
                break;

            case "Throttle Position":
                throttlePos = command.getFormattedResult();
                break;

            case "Relative throttle position":
                relThrottlePos = command.getFormattedResult();
                break;

            case "Distance since codes cleared":
                distanceAfterCodesCleared = command.getFormattedResult();
                break;

            case "Distance Traveled With MIL On":
                distanceMilOn = command.getFormattedResult();
                break;

            case "Describe protocol":
                describeProtocol = command.getFormattedResult();
                break;

            case "Control Module Power Supply":
                controlModuleVoltage = command.getFormattedResult();
                break;

            case "Fuel Rail Pressure":
                fuelRailPressure = command.getFormattedResult();
                break;

            case "Engine Coolant Temperature":
                coolantTemp = command.getFormattedResult();
                break;

            case "Engine oil temperature":
                oilTemp = command.getFormattedResult();
                break;

            // Not used
//            case "Ambient Air Temperature":
//                ambientAirTemp = command.getFormattedResult();
//                break;
//
//            case "Barometric Pressure":
//                barometricPressure = command.getFormattedResult();
//                break;
//
//            case "Timing Advance":
//                timingAdvance = command.getFormattedResult();
//                break;
//
//            case "Command Equivalence Ratio":
//                equivRatio = command.getFormattedResult();
//                break;
//
//            case "Time since trouble codes cleared":
//                timeSinceTcClear = command.getFormattedResult();
//                break;
//
//            case "Describe protocol number":
//                describeProtocolNumber = command.getFormattedResult();
//                break;
//
//            case "Ignition monitor":
//                ignitionMonitor = command.getFormattedResult();
//                break;
        }

        if (chart != null && entry != null) {
            chart.updateChart(entry);
        }
    }

    @Override
    public Trip createDbObject() {
        Trip trip = new Trip();
        trip.maxRpm = maxEngineRpm;
        trip.maxSpeed = maxSpeed;
        trip.distance = travelDistance;
        trip.timeEnd = tripEndCal;
        trip.timeStart = tripStartCal;
        trip.troubleCodes = troubleCodes;
        trip.permanentTroubleCodes = permanentTroubleCodes;
        return trip;
    }
}
