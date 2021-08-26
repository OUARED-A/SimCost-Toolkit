/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package br.com.pucrio.inf.biobd.outertuning.bib.base;

import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class BibFile {

    protected final Configuration config;
    protected final Log log;

    public BibFile() {
        this.config = new Configuration();
        this.log = new Log(this.config);
    }

    public Writer getNewWriter(String nameFile) {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output/" + nameFile), "8859_1"));
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            log.error(ex);
        }
        return null;
    }

    public File[] getAllFilesFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] afile = folder.listFiles();
        return afile;
    }

    public File[] getAllFilesFolderFiltered(String folderPath, String extensionFilter) {
        File[] afiles = this.getAllFilesFolder(folderPath);
        ArrayList<File> temp = new ArrayList<>();
        for (File afile : afiles) {
            if (afile.getName().contains(extensionFilter)) {
                temp.add(afile);
            }
        }
        return (File[]) temp.toArray();
    }

    public String readFileUTF8(String filename) {
        String result = "";
        try {
            try (BufferedReader myBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
                String linha = myBuffer.readLine();
                while (linha != null) {
                    result += linha;
                    linha = myBuffer.readLine();
                }
            }
        } catch (IOException e) {
            log.error(e);
        }
        return result;
    }

    public static void transform(File source, String srcEncoding, File target, String tgtEncoding) throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(source), srcEncoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), tgtEncoding));
            char[] buffer = new char[16384];
            int read;
            while ((read = br.read(buffer)) != -1) {
                bw.write(buffer, 0, read);
            }
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } finally {
                if (bw != null) {
                    bw.close();
                }
            }
        }
    }
}
