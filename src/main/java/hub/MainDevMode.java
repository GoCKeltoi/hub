package hub;

import java.io.IOException;
import java.util.Properties;

import com.google.common.io.Resources;

public class MainDevMode {

    public static void main(String[] args) throws Exception {
        setEnvFromFile("dev.properties");
        Main.main(args);
    }

    private static void setEnvFromFile(String file) throws IOException {
        final Properties props = loadProperties(file);
        props.forEach((k, v) -> System.setProperty(k.toString(), v.toString()));
    }


    private static Properties loadProperties(String file) throws IOException {
        Properties p = new Properties();
        p.load(Resources.getResource(file).openStream());
        return p;
    }
}
