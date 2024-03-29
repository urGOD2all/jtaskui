package jtaskui.Output;

import jtaskui.Records.AttributeGetter;
import jtaskui.TaskObj;
import jtaskui.Task.NoteObj;

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

    /**
     * Recursive method that will take as TaskObj and check it for children (subtasks). If there are children
     * then it will call itself with the first child.
     *  If there are no children (a leaf) then it will add all the attributes for this child to the element
     * and append the element to the root.
     *
     * @param TaskObj - task to add to the XML DOM objet. Should start with the root TaskObj
     * @param Element - branch is the XML Element object that tasks in this call will add objects to. Starts as DOM root and each call should use its own Element
     */
    private void buildDoc(TaskObj task, Element branch) {
        // Set this to be branch incase this is the first (root) call.
        Element e = branch;
        // Used to iterate children
        int i = 0;

        // If the task passed is the same as the rootTask then we skip it (because its not a Task so we don't save it, its just a holder for the top of the tree structure).
        if(!task.equals(rootTask)) {
            // Create an Element for this task
            e = doc.createElement("task");

            // Add all the attributes to this Element object
            for(Map.Entry<String, String> attribute : task.getAttributes().entrySet()) {
                e.setAttribute(attribute.getKey(), attribute.getValue());
            }

            // Add all the migrated attributes to this Element object
            for(Map.Entry<String, AttributeGetter> attribute : task.getAttributeGetters().entrySet()) {
                e.setAttribute(attribute.getKey(), attribute.getValue().execute());
            }

            // Add all the (currently) unsupported attributes to this Element object
            for(Map.Entry<String, String> attribute : task.getUnsupportedAttributes().entrySet()) {
                e.setAttribute(attribute.getKey(), attribute.getValue());
            }

            // Check to see if we need to create a nested Element for the description
            if(task.hasDescription()) {
                // Create an Element for the description
                Element desc = doc.createElement("description");
                // Add the Task Description content
                desc.setTextContent(task.getDescription());
                // Add this description Element to this Task Element
                e.appendChild(desc);
            }

            // Add all the notes to this element
            if(task.hasSubNote()) {
                while(i < task.getSubNoteCount()) {
                    // Create the parent (top-level) note
                    Element ne = doc.createElement("note");
                    // Recurse all notes and the children
                    buildNotes(task.getNoteRoot().getSubNoteAt(i), ne);
                    // Add the parent (top-level) note to the Task
                    e.appendChild(ne);
                    i = i + 1;
                }
            }
            // Reset i
            i = 0;
            // Add this Task Element to the XML Element branch
            branch.appendChild(e);
        }

        // If this has children then we will have to get them and recurse
        if(task.hasChildren()) {
            while(i < task.getChildCount()) {
                // Recurse with this child and use this Task XML element (e) as the branch
                buildDoc(task.getChildAt(i), e);
                i = i + 1;
            }
        }
    }

    /**
     * Recursive method to build the XML Element for Notes
     *
     * @param NoteObj - A Note, if the Note has children then this will recurse them all
     * @param Element - An XML Element that the Note will either construct or append a new XML "note" element to
     */
    private void buildNotes(NoteObj note, Element e) {
        Element noteElement;
        // If the passed in note already has a subject then this note must be its child.
        if(e.hasAttribute("subject")) {
            // Create a new noteElement
            noteElement = doc.createElement("note");
            // Add this new Element to the parent
            e.appendChild(noteElement);
        }
        // Else we are dealing with a top-level note
        else {
            noteElement = e;
        }

        // Set the standard note attributes
        for(Map.Entry<String, String> attribute : note.getAttributes().entrySet()) {
            noteElement.setAttribute(attribute.getKey(), attribute.getValue());
        }

        // Add all the migrated attributes to this Element object
        for(Map.Entry<String, AttributeGetter> attribute : note.getAttributeGetters().entrySet()) {
            noteElement.setAttribute(attribute.getKey(), attribute.getValue().execute());
        }

        // Set the description if it exists
        if(note.hasDescription()) {
            // Create an Element for the description
            Element desc = doc.createElement("description");
            // Add the Task Description content
            desc.setTextContent(note.getDescription());
            // Add this description Element to this Task Element
            noteElement.appendChild(desc);
        }

        int i = 0;
        // While there are child notes...
        while(i < note.getSubNoteCount()) {
            // Get the child Note at i and send the current noteElement as its parent
            buildNotes(note.getSubNoteAt(i), noteElement);
            i = i + 1;
        }
    }

    public void write() {
        try {
            buildDoc(rootTask, root);
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        }
        catch (Exception e) {
            System.out.println("There was an error while attempting to write to " + filename);
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
