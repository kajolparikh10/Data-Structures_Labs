package rumaps;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * You will complete methods to initialize blocks and intersections, calculate block lengths, find reachable intersections,
 * minimize intersections between two points, find the fastest path between two points, and calculate a path's information.
 * 
 * Provided is a Network object that contains all the streets and intersections in the map
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Constructor for the RUMaps class. Initializes the streets and intersections in the map.
     * For each block in every street, sets the block's length, traffic factor, and traffic value.
     * 
     * @param mapPanel The map panel to display the map
     * @param filename The name of the file containing the street information
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     * 
     * @param filename The name of the file containing the street information
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * the file was opened by the constructor - use StdIn to continue reading the file
     * @param numStreets the number of streets
     * @return an ArrayList of blocks
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        // WRITE YOUR CODE HERE
        ArrayList<Block> blocks = new ArrayList<>();
        
        for (int i = 0; i < numStreets; i++) {
            String streetName = StdIn.readLine();
            if (streetName.isEmpty()) {
                streetName = StdIn.readLine();
            }

            int numBlocks = StdIn.readInt(); //num blocks on this street

            for (int j = 0; j < numBlocks; j++) {
                int blockNumber = StdIn.readInt();
                int numPoints = StdIn.readInt();
                double roadSize = StdIn.readDouble();

                Block block = new Block();
                block.setStreetName(streetName);
                block.setBlockNumber(blockNumber);
                block.setRoadSize(roadSize);

                for (int k = 0; k < numPoints; k++) {
                    int x = StdIn.readInt();
                    int y = StdIn.readInt();
                    Coordinate coord = new Coordinate(x, y);
                    if (k == 0) {
                        block.startPoint(coord);
                    } else {
                        block.nextPoint(coord);
                    }
                }

                blocks.add(block);
            }
        }

        return blocks;   
    }

    /**
     * This method traverses through each block and finds
     * the block's start and end points to create intersections. 
     * 
     * It then adds intersections as vertices to the "rutgers" graph if
     * they are not already present, and adds UNDIRECTED edges to the adjacency
     * list.
     * 
     * Note that .addEdge(__) ONLY adds edges in one direction (a -> b). 
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        // WRITE YOUR CODE HERE
        for (Block block : blocks) {
            //first and last coordinate of block
            Coordinate start = block.getCoordinatePoints().get(0);
            Coordinate end = block.getCoordinatePoints().get(block.getCoordinatePoints().size() - 1);
    
            //handle start intersection
            int startIndex = rutgers.findIntersection(start.getX(), start.getY());
            Intersection startIntersection;
            if (startIndex == -1) {
                startIntersection = new Intersection(start);
                rutgers.addIntersection(startIntersection);
                startIndex = rutgers.findIntersection(start.getX(), start.getY());
            } else {
                startIntersection = rutgers.getIntersections()[startIndex];
            }
    
            //handle end intersection
            int endIndex = rutgers.findIntersection(end.getX(), end.getY());
            Intersection endIntersection;
            if (endIndex == -1) {
                endIntersection = new Intersection(end);
                rutgers.addIntersection(endIntersection);
                endIndex = rutgers.findIntersection(end.getX(), end.getY());
            } else {
                endIntersection = rutgers.getIntersections()[endIndex];
            }
    
            //set block endpoints
            block.setFirstEndpoint(startIntersection);
            block.setLastEndpoint(endIntersection);
    
            //add edges both ways
            Block forward = block.copy(); //start -> end
            Block reverse = block.copy(); //end -> start
            reverse.setFirstEndpoint(endIntersection);
            reverse.setLastEndpoint(startIntersection);
    
            rutgers.addEdge(startIndex, forward);
            rutgers.addEdge(endIndex, reverse);
        }
     }

    /**
     * Calculates the length of a block by summing the distances between consecutive points for all points in the block.
     * 
     * @param block The block whose length is being calculated
     * @return The total length of the block
     */
    public double blockLength(Block block) {
        // WRITE YOUR CODE HERE
        List<Coordinate> coords = block.getCoordinatePoints();
        double totalLength = 0.0;

        for (int i = 0; i < coords.size() - 1; i++) {
            Coordinate c1 = coords.get(i);
            Coordinate c2 = coords.get(i + 1);
            totalLength += coordinateDistance(c1, c2);
        }

        return totalLength;
    }

    /**
     * Use a DFS to traverse through blocks, and find the order of intersections
     * traversed starting from a given intersection (as source).
     * 
     * Implement this method recursively, using a helper method.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {
        // WRITE YOUR CODE HERE
        ArrayList<Intersection> reachable = new ArrayList<>();
        HashSet<Intersection> visited = new HashSet<>();
        dfs(source, visited, reachable);
        return reachable;
    }

    //recursive DFS helper method
    private void dfs(Intersection current, HashSet<Intersection> visited, ArrayList<Intersection> reachable) {
        visited.add(current);
        reachable.add(current); 
        
        Block ptr = rutgers.getAdjacencyList()[rutgers.findIntersection(current.getCoordinate().getX(), current.getCoordinate().getY())];
        
        while (ptr != null) {
            Intersection neighbor = null;
            
            if (ptr.getFirstEndpoint().equals(current)) {
                neighbor = ptr.getLastEndpoint();
            } else if (ptr.getLastEndpoint().equals(current)) {
                neighbor = ptr.getFirstEndpoint();
            }
            
            if (neighbor != null && !visited.contains(neighbor)) {
                dfs(neighbor, visited, reachable);
            }
            
            ptr = ptr.getNext();
        }
    }
     
    /**
     * Finds and returns the path with the least number of intersections (nodes) from the start to the end intersection.
     * 
     * - If no path exists, return an empty ArrayList.
     * - This graph is large. Find a way to eliminate searching through intersections that have already been visited.
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least number of turns, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        ArrayList<Intersection> path = new ArrayList<>();
        HashSet<Intersection> visited = new HashSet<>();
        Queue<Intersection> queue = new Queue<Intersection>();
        HashMap<Intersection, Intersection> edgeTo = new HashMap<>(); 
    
        //start BFS from source intersection
        visited.add(start);
        queue.enqueue(start);
    
        //BFS
        while (!queue.isEmpty()) {
            Intersection current = queue.dequeue();
    
            //check if we've reached target intersection
            if (current.equals(end)) {
                break; 
            }
    
            //explore neighbors w/ blocks
            Block ptr = rutgers.getAdjacencyList()[rutgers.findIntersection(current.getCoordinate().getX(), current.getCoordinate().getY())];
            while (ptr != null) {
                Intersection neighbor = null;
    
                //determine neighbor intersection
                if (ptr.getFirstEndpoint().equals(current)) {
                    neighbor = ptr.getLastEndpoint();
                } else if (ptr.getLastEndpoint().equals(current)) {
                    neighbor = ptr.getFirstEndpoint();
                }
    
                //if neighbor is not visited, mark it as visited and add to queue
                if (neighbor != null && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.enqueue(neighbor);
                    edgeTo.put(neighbor, current);
                }
    
                ptr = ptr.getNext();
            }
        }
    
        //reconstruct path from target to source
        if (!visited.contains(end)) {
            return path;
        }
        //trace back from target to source using the edgeTo array
        for (Intersection v = end; v != null; v = edgeTo.get(v)) {
            path.add(v);
        }
        //reverse path to start from source
        Collections.reverse(path);
    
        return path;
    }

    /**
     * Finds the path with the least traffic from the start to the end intersection using a variant of Dijkstra's algorithm.
     * The traffic is calculated as the sum of traffic of the blocks along the path.
     * 
     * What is this variant of Dijkstra?
     * - We are using traffic as a cost - we extract the lowest cost intersection from the fringe.
     * - Once we add the target to the done set, we're done. 
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least traffic, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        ArrayList<Intersection> path = new ArrayList<>();
        ArrayList<Intersection> fringe = new ArrayList<>();
        HashSet<Intersection> done = new HashSet<>();
        HashMap<Intersection, Double> d = new HashMap<>();
        HashMap<Intersection, Intersection> pred = new HashMap<>();

        //initialize distance and predecessor
        d.put(start, 0.0);
        pred.put(start, null);
        fringe.add(start);

        while (!fringe.isEmpty()) {
            Intersection current = getMinCostIntersection(fringe, d);
            fringe.remove(current);

            if (current.equals(end)) {
                break;
            }

            done.add(current);

            Block ptr = rutgers.getAdjacencyList()[rutgers.findIntersection(
                current.getCoordinate().getX(), current.getCoordinate().getY())];

            while (ptr != null) {
                Intersection neighbor = null;

                if (ptr.getFirstEndpoint().equals(current)) {
                    neighbor = ptr.getLastEndpoint();
                } else if (ptr.getLastEndpoint().equals(current)) {
                    neighbor = ptr.getFirstEndpoint();
                }

                if (neighbor != null && !done.contains(neighbor)) {
                    double trafficCost = ptr.getTraffic();
                    double newCost = d.get(current) + trafficCost;

                    if (newCost < d.getOrDefault(neighbor, Double.MAX_VALUE)) {
                        d.put(neighbor, newCost);
                        pred.put(neighbor, current);

                        if (!fringe.contains(neighbor)) {
                            fringe.add(neighbor);
                        }
                    }
                }

                ptr = ptr.getNext();
            }
        }

        for (Intersection v = end; v != null; v = pred.get(v)) {
            path.add(v);
        }

        Collections.reverse(path);
        return path;
    }
    
    private Intersection getMinCostIntersection(ArrayList<Intersection> fringe, HashMap<Intersection, Double> d) {
        Intersection minIntersection = fringe.get(0);
        double minCost = d.getOrDefault(minIntersection, Double.MAX_VALUE);
    
        for (Intersection inter : fringe) {
            double cost = d.getOrDefault(inter, Double.MAX_VALUE);
            if (cost < minCost) {
                minCost = cost;
                minIntersection = inter;
            }
        }
    
        return minIntersection;
    }

    /**
     * Calculates the total length, average experienced traffic factor, and total traffic for a given path of blocks.
     * 
     * You're given a list of intersections (vertices); you'll need to find the edge in between each pair.
     * 
     * Compute the average experienced traffic factor by dividing total traffic by total length.
     *  
     * @param path The list of intersections representing the path
     * @return A double array containing the total length, average experienced traffic factor, and total traffic of the path (in that order)
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        // WRITE YOUR CODE HERE
        double totalLength = 0.0;
        double totalTraffic = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            Intersection current = path.get(i);
            Intersection next = path.get(i + 1);

            Block ptr = rutgers.getAdjacencyList()[rutgers.findIntersection(
                current.getCoordinate().getX(), current.getCoordinate().getY())];

            while (ptr != null) {
                Intersection first = ptr.getFirstEndpoint();
                Intersection last = ptr.getLastEndpoint();

                if ((first.equals(current) && last.equals(next)) ||
                    (first.equals(next) && last.equals(current))) {

                    totalLength += ptr.getLength();
                    totalTraffic += ptr.getTraffic();
                    break;
                }

                ptr = ptr.getNext();
            }
        }

        double trafficFactor;
        if (totalLength == 0) {
            trafficFactor = 0.0;
        } else {
            trafficFactor = totalTraffic / totalLength;
        }

        return new double[] {totalLength, trafficFactor, totalTraffic};
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     * 
     * @param a The first coordinate
     * @param b The second coordinate
     * @return The Euclidean distance between the two coordinates
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        // PROVIDED METHOD

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Calculates and returns a randomized traffic factor for the block based on a Gaussian distribution.
     * 
     * This method generates a random traffic factor to simulate varying traffic conditions for each block:
     * - < 1 for good (faster) conditions
     * - = 1 for normal conditions
     * - > 1 for bad (slower) conditions
     * 
     * The traffic factor is generated with a Gaussian distribution centered at 1, with a standard deviation of 0.2.
     * 
     * Constraints:
     * - The traffic factor is capped between a minimum of 0.5 and a maximum of 1.5 to avoid extreme values.
     * 
     * @param block The block for which the traffic factor is calculated
     * @return A randomized traffic factor for the block
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * Calculates the traffic on a block by the product of its length and its traffic factor.
     * 
     * @param block The block for which traffic is being calculated
     * @return The calculated traffic value on the block
     */
    public double blockTraffic(Block block) {
        // PROVIDED METHOD
        
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }




    
    








}
