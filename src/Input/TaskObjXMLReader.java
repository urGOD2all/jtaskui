package jtaskui.Input;

import jtaskui.TaskObj;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;

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
        }
        catch(NullPointerException e) {
            System.out.println("ERROR: Failed to open file.");
            System.out.println(e.toString());
        }
        catch(ParserConfigurationException e) {
            System.out.println("ERROR: There was a parser setup error while attempting to read!");
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

        try {
            // Parse the XML into a Document object
            doc  = docBuilder.parse(taskFile);
        }
        catch(IOException e) {
            System.out.println("ERROR: An I/O error occured while reading the file!");
            System.out.println(e.toString());
        }
        catch(SAXException e) {
            System.out.println("ERROR: An error occured while parsing the XML file. This could indicate that the file is corrupt of not an XML file!");
            System.out.println(e.toString());
        }

        // Get the first child which is the first "Node" to appear in the file
        Node nodeIter = doc.getFirstChild();
        // This object is used to hold the children and we inspect this object below to see if it looks like a task
        Node taskNodes;

        /* TODO: At the moment this while loop creates a HashMap of the tasks for convenience but ideally this would not pass an XML node to TaskObj. 
                 Ideally it would create the TaskObj after an input processor (for XML atm) has read the content. These TaskObj objects can then just
                 be added to the ROOT TaskObj instead of a HashMap.
        */
        // Loop as long as nodeIter is not null. First time will contain the first child and this will get updated in the loop. 
        //  When there is no children it will get set to null so will naturally close the loop
        while (nodeIter != null) {
            // Check to see if this is the "node" we are interested in (all Tasks are contained with "tasks")
            if(nodeIter.getNodeName() == "tasks") {
                // Now we are interested in everything under <tasks> so we will get the first child of this "node" which will be the first Task
                taskNodes = nodeIter.getFirstChild();
                // Loop as long as taskNodes is not null. First time will contain the first child of "<tasks>" and this will get updated in the loop. 
                //   When there is no children it will get set to null so will naturally close the loop
                while (taskNodes != null) {
                    try {
                        // Tasks are ELEMENT_NODE so test for that and see if its a Task
                        if (taskNodes.getNodeType() == Node.ELEMENT_NODE) {
                            // Test to see if we found a Task
                            if(taskNodes.getNodeName() == "task") {
                                // TODO: We should not pass XML to the TaskObj for reading
                                // Initialise the task object with the Node object
                                TaskObj aTask = new TaskObj(taskNodes);
                            }
                        }
                    }
                    // TODO: This NPE catch should do something intelligent if something has gone this wrong
                    catch(java.lang.NullPointerException npe) {
                        System.out.println("Caught an NPE!");
                        System.out.println(npe.toString());
                    }
                    // Go to the next child "node" / Get the next task
                    taskNodes = taskNodes.getNextSibling();
                }
            }
            // Go to the next child "node" (these are objects outside of the "tasks" element such as syncmlconfig
            nodeIter = nodeIter.getNextSibling();
        }
        return rootTask;
    }
}
