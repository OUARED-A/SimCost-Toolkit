/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package br.com.pucrio.inf.biobd.outertuning.bib.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {

    private static Properties prop;

    public Configuration() {
        this.getPropertiesFromFile();
    }

    private void getPropertiesFromFile() {
        if (Configuration.prop == null) {
            try {
                Configuration.prop = new Properties();
                Properties propTemp = new Properties();
                String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                prop.put("path", path.substring(1, path.indexOf("build")));
                path = path.substring(1, path.indexOf("build")) + "parameters";
                File folder = new File(path);
                if (folder.exists()) {
                    System.out.println("Reading parameters.");
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        File file = listOfFiles[i];
                        if (file.isFile() && file.getName().endsWith(".properties")) {
                            InputStream targetStream = new FileInputStream(file);
                            propTemp.load(targetStream);
                            prop.putAll(propTemp);
                            System.out.println("File: " + file.getName());
                        }
                    }
                    File fileDatabase = new File(path + prop.getProperty("databasefile"));
                    if (fileDatabase.isFile() && fileDatabase.getName().endsWith(".properties")) {
                        InputStream targetStream = new FileInputStream(fileDatabase);
                        propTemp.load(targetStream);
                        prop.putAll(propTemp);
                        System.out.println("File: " + fileDatabase.getName());
                    } else {
                        System.out.println(path + prop.getProperty("databasefile"));
                        System.err.print("FILE DATABASE READ FAIL");
                    }
                }
            } catch (IOException e) {
                System.err.print(e);
            }
        }
    }

    public String clearNameFile(String nameFile) {
        String name = nameFile.toLowerCase().replace("http://", "");
        name = name.toLowerCase().replace("/", ".");
        name = name.toLowerCase().replace("?", "_");
        name = name.toLowerCase().replace(" ", "_");
        name = name.replaceAll("[^\\p{ASCII}]", "");
        return name;
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    public boolean containsKey(String key) {
        return prop.containsKey(key);
    }

}
