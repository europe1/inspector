package net.cararea.inspector.obd;

public class ObdError {
    private String code;
    private String text;
    private String manufacturer;

    ObdError(String code, String text, String manufacturer) {
        this.code = code;
        this.manufacturer = manufacturer;

        if (text == null)
            this.text = "";
        else
            this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getManufacturer() {
        return manufacturer;
    }
}
