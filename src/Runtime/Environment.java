package Runtime;

import java.util.LinkedHashMap;

public class Environment {
    public static LinkedHashMap<String, String> Variables;

    public static void Init() {
        Variables = new LinkedHashMap<>();
        Variables.put("HOME", System.getProperty("user.home"));
        Variables.put("PWD", System.getProperty("user.home"));
    }
}
