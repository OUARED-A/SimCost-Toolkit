/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package br.com.pucrio.inf.biobd.outertuning.bib.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Rafael
 */
public final class Report {

    private File file;
    private FileOutputStream fos;

    public FileOutputStream getFos() {
        return fos;
    }

    public void setFos(FileOutputStream fos) {
        this.fos = fos;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Report(String nameFile) {
        try {
            this.setFile(new File(nameFile));
            this.setFos(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void closeReport() {
        try {
            this.fos.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void add(String text) {
        try {
            text += ";";
            this.fos.write(text.getBytes());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void ln() {
        try {
            String text = "\n";
            this.fos.write(text.getBytes());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
