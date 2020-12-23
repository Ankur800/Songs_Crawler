package project.songsfetcher.utility;

import java.io.IOException;
import java.util.Properties;

// This is basically for avoiding us to directly pass the password and user name
// in DatabaseUtil class in database package.
// Now we need only to add user profiles in pom.xml and by using this class we can
// get that and without changing code in DatabaseUtil we can get our desired solution.
// Database will use the same code, we just need to add user-profiles in pom.xml
public class SysProperties {
    private static Properties prop = null;

    //making constructor private so that no access to other class
    private SysProperties(){

    }

    public static Properties getInstance(){
        if(prop == null){
            //Singleton
            ClassLoader loader = SysProperties.class.getClassLoader();
            if(loader == null){
                loader = ClassLoader.getSystemClassLoader();
            }

            String propFile = "application.properties";
            java.net.URL url = loader.getResource(propFile);
            prop = new Properties();

            try{
                prop.load(url.openStream());
            } catch (IOException ex){

            }
        }

        return prop;
    }

    public static String getPropertyValue(String key) {
        return  SysProperties.getInstance().getProperty(key);
    }

    public static void main(String[] args) {
        System.out.println(getPropertyValue("DB_PASSWORD"));
    }

}
