package investigation;

import java.util.ArrayList; 

/*  
 * This class represents a cyber crime investigation.  It contains a directory of hackers, which is a resizing
 * hash table. The hash table is an array of HNode objects, which are linked lists of Hacker objects.  
 * 
 * The class contains methods to add a hacker to the directory, remove a hacker from the directory.
 * You will implement these methods, to create and use the HashTable, as well as analyze the data in the directory.
 * 
 * @author Colin Sullivan
 */
public class CyberCrimeInvestigation {
       
    private HNode[] hackerDirectory;
    private int numHackers = 0; 

    public CyberCrimeInvestigation() {
        hackerDirectory = new HNode[10];
    }

    /**
     * Initializes the hacker directory from a file input.
     * @param inputFile
     */
    public void initializeTable(String inputFile) { 
        // DO NOT EDIT
        StdIn.setFile(inputFile);  
        while(!StdIn.isEmpty()){
            addHacker(readSingleHacker());
        }
    }

    /**
     * Reads a single hackers data from the already set file,
     * Then returns a Hacker object with the data, including 
     * the incident data.
     * 
     * StdIn.setFile() has already been called for you.
     * 
     * @param inputFile The name of the file to read hacker data from.
     */
     public Hacker readSingleHacker(){ 
        // WRITE YOUR CODE HERE
        String name = StdIn.readLine();
        String ipHash = StdIn.readLine();
        String location = StdIn.readLine();
        String os = StdIn.readLine();
        String webServer = StdIn.readLine();
        String date = StdIn.readLine();
        String urlHash = StdIn.readLine();

        Incident incident = new Incident(os, webServer, date, location, ipHash, urlHash);

        Hacker hacker = new Hacker(name);
        hacker.getIncidents().add(incident);

        return hacker;
    }

    /**
     * Adds a hacker to the directory.  If the hacker already exists in the directory,
     * instead adds the given Hacker's incidents to the existing Hacker's incidents.
     * 
     * After a new insertion (NOT if a hacker already exists), checks if the number of 
     * hackers in the table is >= table length divided by 2. If so, calls resize()
     * 
     * @param toAdd
     */
    public void addHacker(Hacker toAdd) {
        // WRITE YOUR CODE HERE
        int tableLength = hackerDirectory.length;
        int index = Math.abs(toAdd.hashCode()) % tableLength;
        HNode current = hackerDirectory[index];
        HNode prev = null;

        while (current != null) {
            if (current.getHacker().getName().equals(toAdd.getName())) {
                current.getHacker().getIncidents().addAll(toAdd.getIncidents());
                return;
            }
            prev = current;
            current = current.getNext();
        }

        HNode newNode = new HNode(toAdd);
        if (prev == null) {
            hackerDirectory[index] = newNode;
        } else {
            prev.setNext(newNode);
        }

        numHackers++;
        if (numHackers >= tableLength / 2) {
            resize();
        }
    }

    /**
     * Resizes the hacker directory to double its current size.  Rehashes all hackers
     * into the new doubled directory.
     */
    private void resize() {
        // WRITE YOUR CODE HERE 
    HNode[] temp = hackerDirectory;
    hackerDirectory = new HNode[temp.length * 2];
    numHackers = 0;

    for (HNode node : temp) {
        while (node != null) {
            addHacker(node.getHacker());
            node = node.getNext();
        }
    }
        
    }

    /**
     * Searches the hacker directory for a hacker with the given name.
     * Returns null if the Hacker is not found
     * 
     * @param toSearch
     * @return The hacker object if found, null otherwise.
     */
    public Hacker search(String toSearch) {
        // WRITE YOUR CODE HERE 
        int index = Math.abs(toSearch.hashCode()) % hackerDirectory.length;
        HNode current = hackerDirectory[index];
    
        while (current != null) {
            if (current.getHacker().getName().equals(toSearch)) {
                return current.getHacker();
            }
            current = current.getNext();
        }
    
        return null;
    }

    /**
     * Removes a hacker from the directory.  Returns the removed hacker object.
     * If the hacker is not found, returns null.
     * 
     * @param toRemove
     * @return The removed hacker object, or null if not found.
     */
    public Hacker remove(String toRemove) {
        // WRITE YOUR CODE HERE 
        int index = Math.abs(toRemove.hashCode()) % hackerDirectory.length;
        HNode current = hackerDirectory[index];
        HNode prev = null;
    
        while (current != null) {
            if (current.getHacker().getName().equals(toRemove)) {
                if (prev == null) {
                    hackerDirectory[index] = current.getNext();
                } else {
                    HNode prevnext = prev.getNext();
                    prevnext = current.getNext();
                }
                numHackers--;
                return current.getHacker();
            }
            prev = current;
            current = current.getNext();
        }
    
        return null;
    } 

    /**
     * Merges two hackers into one based on number of incidents.
     * 
     * @param hacker1 One hacker
     * @param hacker2 Another hacker to attempt merging with
     * @return True if the merge was successful, false otherwise.
     */
    public boolean mergeHackers(String hacker1, String hacker2) {  
        // WRITE YOUR CODE HERE 
        Hacker h1 = search(hacker1);
        Hacker h2 = search(hacker2);

        if (hacker1 == null || hacker2 == null) {
            return false;
        }

        Hacker target;
        Hacker source;

        if (h1.getIncidents().size() >= h2.getIncidents().size()) {
            target = h1;
            source = h2;
        } else {
            target = h2;
            source = h1;
        }

        for (Incident incident : source.getIncidents()) {
            target.getIncidents().add(incident);
        }

        target.addAlias(source.getName());
        remove(source.getName());
        return true;
    }

    /**
     * Gets the top n most wanted Hackers from the directory, and
     * returns them in an arraylist. 
     * 
     * You should use the provided MaxPQ class to do this. You can
     * add all hackers, then delMax() n times, to get the top n hackers.
     * 
     * @param n
     * @return Arraylist containing top n hackers
     */
    public ArrayList<Hacker> getNMostWanted(int n) {
        // WRITE YOUR CODE HERE 
        MaxPQ<Hacker> pq = new MaxPQ<>();
        ArrayList<Hacker> result = new ArrayList<>();
    
        for (HNode node : hackerDirectory) {
            while (node != null) {
                pq.insert(node.getHacker());
                node = node.getNext();
            }
        }
    
        for (int i = 0; i < n && !pq.isEmpty(); i++) {
            result.add(pq.delMax());
        }
    
        return result;
    }

    /**
     * Gets all hackers that have been involved in incidents at the given location.
     * 
     * You should check all hackers, and ALL of each hackers incidents.
     * You should not add a single hacker more than once.
     * 
     * @param location
     * @return Arraylist containing all hackers who have been involved in incidents at the given location.
     */
    public ArrayList<Hacker> getHackersByLocation(String location) {
        // WRITE YOUR CODE HERE 
        ArrayList<Hacker> result = new ArrayList<>();
    
        for (HNode node : hackerDirectory) {
            while (node != null) {
                boolean found = false;
                for (Incident incident : node.getHacker().getIncidents()) {
                    if (incident.getLocation().equals(location)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    result.add(node.getHacker());
                }
                node = node.getNext();
            }
        }
        return result;
    }
  

    /**
     * PROVIDED--DO NOT MODIFY!
     * Outputs the entire hacker directory to the terminal. 
     */
     public void printHackerDirectory() { 
        System.out.println(toString());
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.hackerDirectory.length; i++) {
            HNode headHackerNode = hackerDirectory[i];
            while (headHackerNode != null) {
                if (headHackerNode.getHacker() != null) {
                    sb.append(headHackerNode.getHacker().toString()).append("\n");
                    ArrayList<Incident> incidents = headHackerNode.getHacker().getIncidents();
                    for (Incident incident : incidents) {
                        sb.append("\t" +incident.toString()).append("\n");
                    }
                }
                headHackerNode = headHackerNode.getNext();
            } 
        }
        return sb.toString();
    }

    public HNode[] getHackerDirectory() {
        return hackerDirectory;
    }
}
