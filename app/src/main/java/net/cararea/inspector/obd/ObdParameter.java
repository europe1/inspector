package net.cararea.inspector.obd;

public class ObdParameter {
    private int nameRes;
    private String value;
    private int unitsRes = 0;
    private String extraName;

    public ObdParameter(int name, String value, String extra) {
        nameRes = name;
        this.value = value;
        extraName = extra;
    }

    public ObdParameter(int name, String value, int units, String extra) {
        nameRes = name;
        this.value = value;
        unitsRes = units;
        extraName = extra;
    }

    public int getName() {
        return nameRes;
    }

    public String getValue() {
        return value;
    }

    public int getUnits() {
        return unitsRes;
    }

    public String getExtra() {
        return extraName;
    }
}
