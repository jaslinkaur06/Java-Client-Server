package config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class ConfigLoader {
    private Properties properties;
    
    public ConfigLoader(String filePath) {
    	properties = new Properties();
    	try {
    		properties.load(new FileReader(filePath));
    	}catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getProperty(String key) {
    	return properties.getProperty(key);
    }
}
