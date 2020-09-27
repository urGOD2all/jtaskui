package jtaskui;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/* TODO:
   * Remove all code that handles XML from TaskObj and move to a XML processing class. This will hopefully allow for changing the output format easily
   * When will we save, auto like TaskCoach or manual or both
   * Make sure there are no thrown Exceptions and that its all handled properly/well
 */
public class jtaskui {

    public static void main(String[] args) {
        /* TODO:
         * Test the file exists and is readable
         * Any other error handling (try/catc)
         */
        // TODO: The UI should have support for opening a file of choice
        // Open the file given on the command line
        File jTaskUIFile = new File(args[0]);

        HashMap<String, TaskObj> tasksMap = new HashMap<String, TaskObj>();

        // TODO: Catch the exceptions properly
        try {
            // Make sure we can read the file
            if(jTaskUIFile.canRead()) {
                // TODO: Explain these two lines better
                DocumentBuilderFactory jTaskUIFileXMLDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder jTaskUIFileXMLDocumentBuilder = jTaskUIFileXMLDocumentBuilderFactory.newDocumentBuilder();

                // Parse the XML into a Document object
                Document jTaskUIFileXMLDocument  = jTaskUIFileXMLDocumentBuilder.parse(jTaskUIFile);

                // Get the first child which is the first "Node" to appear in the file
                Node nodeIter = jTaskUIFileXMLDocument.getFirstChild();
                // This object is used to hold the children and we inspect this object below to see if it looks like a task
                Node taskNodes;

                /* TODO: At the moment this while loop creates a HashMap of the tasks for convenience but ideally this would not pass an XML node to TaskObj. 
                         Ideally it would create the TaskObj after an input processor (for XML atm) has read the content. These TaskObj objects can then just
                         be added to the ROOT TaskObj instead of a HashMap.
                */
                // Loop as long as nodeIter is not null. First time will contain the first child and this will get updated in the loop. 
                //   When there is no children it will get set to null so will naturally close the loop
                while (nodeIter != null) {
                    // Check to see if this is the "node" we are interested in
                    if(nodeIter.getNodeName() == "tasks") {
//                        System.out.println("Tasks are here");
                        // Now we are interested in everything under <tasks> so we will get the first child of this "node"
                        taskNodes = nodeIter.getFirstChild();
                        // Loop as long as taskNodes is not null. First time will contain the first child of "<tasks>" and this will get updated in the loop. 
                        //   When there is no children it will get set to null so will naturally close the loop
                        while (taskNodes != null) {
                            try {
                                // Check that this is an ELEMENT_NODE and not something else
                                if (taskNodes.getNodeType() == Node.ELEMENT_NODE) {
//                                    System.out.println(taskNodes.getNodeName());
                                    if(taskNodes.getNodeName() == "task") {
//                                        NamedNodeMap taskItemAttributes = taskNodes.getAttributes();

                                        // Initialise the task object with the Node object
                                        TaskObj aTask = new TaskObj(taskNodes);

                                        // Add this new object to the map with the key being the id
                                        tasksMap.put(aTask.getID(), aTask);
                                    }
                                    // This is showing things like the guid and syncmlconfig "nodes"
                                    //else {
                                    //    System.out.println(taskNodes.getNodeName());
                                    //}
                                }
                                // TODO: Think I can ignore the TEXT_NODE here because all "real" text should be a child of the task (descriptions and notes)
                                // Check that this is an TEXT_NODE and not something else
    //                            else if (taskNodes.getNodeType() == Node.TEXT_NODE) {
  //                                  System.out.println(taskNodes.getNodeName() + " " + taskNodes.getNodeValue() + " " + taskNodes.getTextContent() + " " + taskNodes.getLocalName());
//                                }
                            // TODO: This NPE catch should do something intelligent if something has gone this wrong
                            } catch (java.lang.NullPointerException npe) {
                                System.out.println("Caught an NPE!");
                                System.out.println(npe.toString());
                            }
                            // Go to the next child "node"
                            taskNodes = taskNodes.getNextSibling();
                        }
                    }
                    // Go to the next child "node"
                    nodeIter = nodeIter.getNextSibling();
                }
                // System.out.println("No more nodes");

                // Make a TaskObj just to be the root and handle all the Tasks as its children. This should never be seen in the UI
                TaskObj root = new TaskObj("1", "ROOT");

                // Now lets see what tasks we have and add them all to the table
                for (Map.Entry<String, TaskObj> aTask : tasksMap.entrySet()) {
                    // Add the task to the root
                    root.addChild(aTask.getValue());

                    // TODO: This is diag and will need to be clean. Maybe I can add this to part of the UI later
                    for(Map.Entry<String, String> unsupItem : aTask.getValue().getUnsupportedAttributes().entrySet()) {
                        System.out.println("Unsupported items for ID: " + aTask.getValue().getID() + " - " + unsupItem.getKey() + " = " + unsupItem.getValue());
                    }
                }

                // TODO: This should be done as runable
                // Create the GUI
                jtaskView jtv = new jtaskView(root);
                
                // TODO: Show some info so I know I have read all the file content, this should be displayed in a status bar in the UI
                System.out.println("Total tasks = " + tasksMap.size());
                System.out.println("In root = " + root.getChildCount());
            }
        // TODO: Fix this catch
        } catch (Exception e) {
            System.out.println("Some error happened!");
            System.out.println(e.toString());
        }
    }
}

