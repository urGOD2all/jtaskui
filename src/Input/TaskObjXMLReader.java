package jtaskui.Input;

import jtaskui.TaskObj;
import jtaskui.Task.NoteObj;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;

import java.util.HashMap;

public class TaskObjXMLReader {
    private boolean connectState;
    private String filename;
    // This TaskObj will be loaded with all the file content and returned
    private TaskObj rootTask;
    private File taskFile;
    private DocumentBuilderFactory docBuilderFactory;
    private DocumentBuilder docBuilder;
    private Document doc;

    private TaskObjXMLReader() {
        // Connected is false by default
        connectState = false;
        // Create parser factory for Documents
        docBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    public TaskObjXMLReader(String filename, TaskObj rootTask) {
        this();
        this.filename = filename;

        if (rootTask == null) this.rootTask = new TaskObj("ROOT");
        else this.rootTask = rootTask;
    }

    // TODO: Would be good to test if this file conforms to an XML input for jtaskUI
    public void connect() {
        try {
            // Attempt to open the given file
            taskFile = new File(filename);
            if(taskFile.canRead()) {
                connectState = true;
            }

            // Get an DocumentBuilder so that we can get instances of Document
            docBuilder = docBuilderFactory.newDocumentBuilder();
            // Parse the XML into a Document object
            doc  = docBuilder.parse(taskFile);
        }
        catch(NullPointerException e) {
            System.out.println("ERROR: Failed to open file.");
            System.out.println(e.toString());
        }
        catch(ParserConfigurationException e) {
            System.out.println("ERROR: There was a parser setup error while attempting to read!");
            System.out.println(e.toString());
        }
        catch(IOException e) {
            System.out.println("ERROR: An I/O error occured while reading the file!");
            System.out.println(e.toString());
        }   
        catch(SAXException e) {
            System.out.println("ERROR: An error occured while parsing the XML file. This could indicate that the file is corrupt of not an XML file!");
            System.out.println(e.toString());
        }   
    }

    public boolean isConnected() {
        return connectState;
    }

    public void disconnect() {
    }

    public TaskObj read() {
        // Do not proceed if we are not connected to the datasource
        if(! isConnected()) return rootTask;

        // Get the first child which is the first "Node" to appear in the file
        Node nodeIter = doc.getFirstChild();
        // This object is used to hold the children and we inspect this object below to see if it looks like a task
        Node taskNodes;

        // Loop as long as nodeIter is not null. First time will contain the first child and this will get updated in the loop. 
        //  When there is no children it will get set to null so will naturally close the loop
        while (nodeIter != null) {
            // Check to see if this is the "node" we are interested in (all Tasks are contained with "tasks")
            if(nodeIter.getNodeName() == "tasks") {
                // Now we are interested in everything under <tasks> so we will get the first child of this "node" which will be the first Task
                // Pass the first Child and the rootTask to this method to read all the content of the XML file and add everything to rootTask
                readNodes(nodeIter.getFirstChild(), rootTask);
            }
            // Go to the next child "node" (these are objects outside of the "tasks" element such as syncmlconfig
            nodeIter = nodeIter.getNextSibling();
        }
        return rootTask;
    }

    /**
     * A parent can be a Task or a Note
     */
    private void readNodes(Node node, TaskObj parentTask) {
        // Read this node completely before moving on and add all the detail to the parentTask
        // If we find a task then its a subtask of this parentTask
        while (node != null) {
            // We are only interested in ELEMENTS
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Lets see what type of element this is
                switch(node.getNodeName()) {
                    // This is a subtask of the parent
                    case "task":
                        // TODO: Make a constuctor that will take a hash map of attributes ?
                        TaskObj childTask = new TaskObj("NEW CHILD");
                        // Pass the attributes to the child
                        childTask.populateAttributes(readNodeAttributes(node.getAttributes()));
                        // Add this task to its parent
                        parentTask.addChild(childTask);

                        // Recurse for sub tasks, call with the first child in the XML of the current node and the child TaskObj
                        if (node.hasChildNodes()) readNodes(node.getFirstChild(), childTask);
                        break;
                    // This is the parents description object
                    case "description":
                        if (node.getTextContent() != null) parentTask.setDescription(node.getTextContent().substring(0, node.getTextContent().length()-1));
                        break;
                    // This is a note (or subnote) for the parent
                    case "note":
                        // Create a new NoteObj
                        NoteObj note = new NoteObj("NOTE");
                        // Pass the attributes to the note
                        note.populateAttributes(readNodeAttributes(node.getAttributes()));
                        // Add this task to its parent
                        parentTask.addNote(note);

                        // Recurse for sub notes, call with the first child in the XML of the current node and the child TaskObj
                        if (node.hasChildNodes()) readNodes(node.getFirstChild(), note);
                        break;
                    default:
                        System.out.println("What is this? " + node.getNodeName());
                }
            }
            // Get the next sibblinb for the while loop to inspect, this could be, but not limited to, a note, description, a subtask or something else!
            node = node.getNextSibling();
        }
    }

    private HashMap<String, String> readNodeAttributes(NamedNodeMap attrs) {
        // Build a HashMap of all the attributes and return it
        HashMap<String, String> attributes = new HashMap<String, String>();
        for(int attrNo = 0; attrNo < attrs.getLength(); attrNo++) attributes.put(attrs.item(attrNo).getNodeName(), attrs.item(attrNo).getNodeValue());
        return attributes;
    }
}
