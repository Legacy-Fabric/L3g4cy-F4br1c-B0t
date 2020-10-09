package io.github.boogiemonster1o1.legacyfabricbot;

import java.io.IOException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class URLTest {
    @Test
    public void checkUrl() throws IOException, DocumentException {
        URL url = new URL("http://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/yarn/maven-metadata.xml");
        Document doc = new SAXReader().read(url);
        Element root = doc.getRootElement();
        assertEquals(root.getName() + " was not 'metadata'", "metadata", root.getName());
    }
}
