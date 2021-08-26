/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pucrio.inf.biobd.outertuning.bib.dispatcher.index;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Rafael
 */
public class HTML {

    public String getHtml(String url) {
        Document doc = this.getDocFromURL(url);
        if (doc != null) {
            return doc.html();
        } else {
            return "";
        }
    }

    public String getTag(String url, String tag) {
        Document doc = this.getDocFromURL(url);
        Element tagHtml = doc.select(tag).first();
        return tagHtml.html();
    }

    private Document getDocFromURL(String url) {
        try {
            Connection.Response res = Jsoup.connect(url).
                    timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute();
            if (res.statusCode() == 307) {
                String sNewUrl = res.header("Location");
                if (sNewUrl != null && sNewUrl.length() > 7) {
                    url = sNewUrl;
                }
                res = Jsoup.connect(url).
                        timeout(5000).execute();
            }
            Document doc = res.parse();
            return doc;
        } catch (IOException ex) {
            System.out.println("URL mal formada: " + url);
            return null;
        }
    }

}
