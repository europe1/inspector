package net.cararea.inspector.obd;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObdErrorReader {

    public static List<ObdError> getErrorsDefinitions(
            InputStreamReader in, List<String> errorCodes) throws IOException {
        try (JsonReader reader = new JsonReader(in)) {
            return readErrorsArray(reader, errorCodes);
        }
    }

    public static ObdError getErrorDefinition(InputStreamReader in, String errorCode)
        throws IOException {
        try (JsonReader reader = new JsonReader(in)) {
            return readError(reader, errorCode);
        }
    }

    private static ObdError readError(JsonReader reader, String errorCode)
            throws IOException {
        ObdError returnError = null;

        reader.beginArray();
        while (reader.hasNext()) {
            ObdError error = readError(reader);
            if (errorCode.equals(error.getCode())) {
                returnError = error;
                break;
            }
        }
        reader.endArray();

        return returnError;
    }

    private static List<ObdError> readErrorsArray(JsonReader reader, List<String> errorCodes)
            throws IOException {
        List<ObdError> errors = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            ObdError error = readError(reader);
            if (errorCodes.contains(error.getCode()))
                errors.add(error);
        }
        reader.endArray();

        return errors;
    }

    private static ObdError readError(JsonReader reader) throws IOException {
        String code = null;
        String text = null;
        String manufacturer = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("code")) {
                code = reader.nextString();
            } else if (name.equals("text") && reader.peek() != JsonToken.NULL) {
                text = reader.nextString();
            } else if (name.equals("manufacturer")) {
                manufacturer = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new ObdError(code, text, manufacturer);
    }
}
