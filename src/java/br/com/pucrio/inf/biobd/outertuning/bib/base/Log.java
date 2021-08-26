/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package br.com.pucrio.inf.biobd.outertuning.bib.base;

import br.com.pucrio.inf.biobd.outertuning.bib.configuration.Configuration;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log {

    protected static Configuration prop = null;
    protected static String debug;
    private static String lastDebug = "";
    private static int difTime = 0;
    protected static String nameFileLog;
    public static Gson gson;

    public static String getNameFileLog(String complement) {
        return complement + "_" + nameFileLog;

    }

    public static void setNameFileLog(String nameFileLog) {
        if (Log.nameFileLog == null) {
            Log.nameFileLog = nameFileLog;
        }
    }

    public Log(Configuration properties) {
        Log.prop = properties;
        readDebug();
        Log.setNameFileLog(getDateTime("dd-MM-yyyy-'at'-hh-mm-ss-a"));
        gson = new Gson();
    }

    protected final void readDebug() {
        if (Log.debug == null) {
            Log.debug = String.valueOf(Log.prop.getProperty("debug"));
        }
    }

    protected void print(Object msg) {
        String textToPrint = this.getDateTime("hh:mm:ss") + this.getDifTime(this.getDateTime("hh:mm:ss")) + " = " + msg;
        if (this.isPrint(0)) {
            System.out.println(textToPrint);
        }
        if (this.isPrint(1)) {
            this.writeFile("log", textToPrint);
        }
    }

    protected boolean isPrint(int pos) {
        return Log.debug.substring(pos, pos + 1).equals("1");
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

    public final String getDateTime(String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        Date today = new Date();
        return ft.format(today);
    }

    public void title(String msg) {
        if (this.isPrint(1)) {
            int size = 80 - msg.length();
            StringBuilder buf = new StringBuilder();
            buf.append("==");
            for (int i = 0; i < size / 2; ++i) {
                buf.append("=");
            }
            this.print(buf.toString() + " " + msg + " " + buf.toString());
        }
    }

    public void endTitle() {
        this.title("fim");
    }

    public void msg(Object msg) {
        this.print(msg);
    }

    public void error(Object error) {
        errorPrint(error);
    }

    private void errorPrint(Object e) {
        this.print(e);
        throw new UnsupportedOperationException(e.toString());
    }

    public String getDifTime(String now) {
        if (!now.isEmpty() && !lastDebug.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                Date nowDate = sdf.parse(now);
                Date lastDate = sdf.parse(lastDebug);
                long diff = nowDate.getTime() - lastDate.getTime();
                difTime = (int) (diff / 1000);
            } catch (ParseException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setLastDebug(now);
        String result = "= + " + difTime + "s";
        if (difTime < 9) {
            result += "  ";
        } else {
            result += " ";
        }
        return result;
    }

    public void setLastDebug(String last) {
        lastDebug = last;
    }

    public String getLastDebug() {
        return lastDebug;
    }

    public void setDifTime(int difTime) {
        Log.difTime = difTime;
    }

    public void writeFile(String nameFile, String content) {
        try {
            OutputStreamWriter writer;
            boolean append = false;
            switch (nameFile) {
                case "pid":
                    nameFile = nameFile + ".txt";
                    break;
                case "reportexcel":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".csv";
                    break;
                case "reportexcelappend":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".csv";
                    append = true;
                    break;
                case "log":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".txt";
                    append = true;
                    break;
                default:
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + nameFile + ".txt";
                    append = true;
            }
            nameFile = prop.getProperty("path") + nameFile;
            writer = new OutputStreamWriter(new FileOutputStream(nameFile, append), "UTF-8");
            BufferedWriter fbw = new BufferedWriter(writer);
            fbw.write(content);
            fbw.newLine();
            fbw.close();
        } catch (IOException ex) {
            this.errorPrint(ex);
        }
    }

    public void writePID() {
        msg("PID: " + getPID());
        writeFile("pid", String.valueOf(getPID()));
    }

    public void archiveLogFiles() {
        File folder = new File(prop.getProperty("path") + prop.getProperty("folderLog") + File.separatorChar);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File afile[] = folder.listFiles();
        int i = 0;
        for (int j = afile.length; i < j; i++) {
            File arquivos = afile[i];
            String nameFile = arquivos.getName();
            if (nameFile.contains(".txt") || nameFile.contains(".csv") || (nameFile.equals("blackboard.properties") && prop.containsKey("blackboard") && prop.getProperty("blackboard").equals("1"))) {
                String nameFolder = prop.getProperty("path") + "talk/";
                if (nameFile.contains("_")) {
                    nameFolder += nameFile.substring(nameFile.indexOf("_") + 1, nameFile.indexOf("."));
                } else {
                    nameFolder += nameFile.substring(0, nameFile.indexOf("."));
                }
                new File(nameFolder).mkdir();
                File diretorio = new File(nameFolder);
                File destiny = new File(diretorio, arquivos.getName());
                if (destiny.exists()) {
                    destiny.delete();
                }
                arquivos.renameTo(new File(diretorio, arquivos.getName()));
            }
        }
    }

    public long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
}
