package io.github.boogiemonster1o1.legacyfabricbot;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class URLTest {
    @Test
    public void checkUrl() throws IOException, DocumentException {
        URL url = new URL("http://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/yarn/maven-metadata.xml");
        Document doc = new SAXReader().read(url);
        Element root = doc.getRootElement();
        assertEquals("not 'metadata'", "metadata", root.getName());
        String data = "";
        Iterator<Element> iterator = root.element("versioning").element("versions").elementIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            assertSame(next.getData().getClass(), String.class);
            if (!next.getData().toString().startsWith("1.8.9")) {
                continue;
            }
            data = next.getData().toString();
        }
        System.out.println(data);
    }
}
