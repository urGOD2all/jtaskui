package jtaskui.Output;

import jtaskui.TaskObj;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.util.Map;

public class TaskObjXMLWriter {
    private boolean connectState;
    // The filename of the output file
    private String filename;
    // The output stream to the specified file to save to
    private FileOutputStream file;
    private Document doc;
    private DocumentBuilderFactory docBuilderFactory;
    private DocumentBuilder builder;
    // The root of the XML document
    private Element root;
    private Transformer transformer;
    // TaskObj at the root of the TreeTable
    private TaskObj rootTask;

    private TaskObjXMLWriter() {
        connectState = false;
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        // Get the transformer object ready
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }
        catch(Exception e) {
            System.out.println("There was a configuration error while attempting to save!");
            System.out.println(e.toString());
        }
    }

    public TaskObjXMLWriter(String filename, TaskObj rootTask) {
        this();
        this.filename = filename;
        this.rootTask = rootTask;
    }

    public void connect() {
        try {
            // Get a new builder
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            // Get a new DOM
            doc = builder.newDocument();
            // Create the bare root node
            root = doc.createElement("tasks");
            // Add the root to the document
            doc.appendChild(root);
            file = new FileOutputStream(filename);
            connectState = true;
        }
        catch(FileNotFoundException e) {
            System.out.println("Error: Could not open specified file " + filename);
            System.out.println(e.toString());
        }
        catch(Exception e) {
            System.out.println("There was a setup error while attempting to save!");
            System.out.println(e.toString());
        }
    }

    public boolean isConnected() {
        return connectState;
    }

    public void disconnect() {
        connectState = false;
        builder = null;
        doc = null;
        root = null;
    }

    private void buildDoc() {
        Element e = null;

        // Create an Element for this task
        e = doc.createElement("task");

        // TODO: This needs fixing, it will only look at the first child.
        // Add all the attributes to the Task Element object
        for(Map.Entry<String, String> attribute : rootTask.getChildAt(1).getAttributes().entrySet()) {
            e.setAttribute(attribute.getKey(), attribute.getValue());
        }
        // TODO: Do some checking and make sure there is a desciption to add
        // Add the Task Description
        Element desc = doc.createElement("description");
        desc.setTextContent(rootTask.getChildAt(1).getDescription());
        // Add this description Element to the Task Element
        e.appendChild(desc);

        // Add this Task Element to the Document root
        root.appendChild(e);
    }

    public void write() {
        try {
            buildDoc();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        }
        catch (Exception e) {
            System.out.println("There was an error while attempting to write to " + filename);
            System.out.println(e.toString());
        }
    }
}
