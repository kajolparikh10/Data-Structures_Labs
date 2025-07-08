package dictionary;

import java.util.ArrayList;

public class BSTDictionary {

    // The root node of this BST
    private WordNode root;

    /**
     * This method is provided for you, do not edit it.
     * 
     * This will call your recursive postOrder() method, give it an ArrayList of
     * WordNodes, and then return that list. Your recursive method should fill
     * this list with WordNodes, in pre-order.
     * 
     * @return arraylist containing WordNodes of this tree, ordered via pre-order
     */
    public ArrayList<WordNode> preOrder() {
        // DO NOT EDIT
        ArrayList<WordNode> traversal = new ArrayList<>();
        preOrderHelper(root, traversal);
        return traversal;
    }

    /**
     * This is a recursive helper method for post-order traversal.
     * 
     * You should:
     * 1) return if the curr WordNode is null
     * 2) Add curr to the ArrayList
     * 3) Recursively call this method on curr's left child
     * 4) Recursively call this method on curr's right child
     */
    private void preOrderHelper(WordNode curr, ArrayList<WordNode> list) {
        // WRITE YOUR CODE HERE
        if(curr == null) return;
        list.add(curr);
        preOrderHelper(curr.getLeft(), list);
        preOrderHelper(curr.getRight(), list);

    }

    /**
     * This method is provided for you, do not edit it.
     * 
     * This will call your recursive postOrder() method, give it an ArrayList of
     * WordNodes, and then return that list. Your recursive method should fill this
     * list with WordNodes, in post-order.
     * 
     * @return An arraylist containing WordNodes of this tree, ordered via post
     *         order
     */
    public ArrayList<WordNode> postOrder() {
        // DO NOT EDIT
        ArrayList<WordNode> traversal = new ArrayList<>();
        postOrderHelper(root, traversal);
        return traversal;
    }

    /**
     * This is a recursive helper method for post-order traversal.
     * 
     * You should:
     * 1) return if the curr WordNode is null
     * 2) Recursively call this method on curr's left child
     * 3) Recursively call this method on curr's right child
     * 4) Add curr to the ArrayList
     */
    private void postOrderHelper(WordNode curr, ArrayList<WordNode> list) {
        // WRITE YOUR CODE HERE
        if(curr == null) return;
        postOrderHelper(curr.getLeft(), list);
        postOrderHelper(curr.getRight(), list);
        list.add(curr);

    }

    /**
     * This method is provided for you, do not edit it.
     * 
     * This will call your recursive postOrder() method, give it an ArrayList of
     * WordNodes, and then return that list. Your recursive method should fill
     * this list with WordNodes, ordered via in-order.
     * 
     * @return arraylist containing WordNodes of this tree, ordered via in-order
     */
    public ArrayList<WordNode> inOrder() {
        // DO NOT EDIT
        ArrayList<WordNode> traversal = new ArrayList<>();
        inOrderHelper(root, traversal);
        return traversal;
    }

    /**
     * This is a recursive helper method for post-order traversal.
     * 
     * You should:
     * 1) return if the curr WordNode is null
     * 2) Recursively call this method on curr's left child
     * 3) Add curr to the ArrayList
     * 4) Recursively call this method on curr's right child
     */
    private void inOrderHelper(WordNode curr, ArrayList<WordNode> list) {
        // WRITE YOUR CODE HERE
        if (curr == null) return; // Base case: if the node is null, return.
    
        inOrderHelper(curr.getLeft(), list);
        list.add(curr); 
        inOrderHelper(curr.getRight(), list);

    }

    /**
     * This method should iteratively traverse the tree, using a Queue.
     * It should fill an ArrayList with WordNodes, ordered via level-order.
     * Finally, return that array list.
     * 
     * To complete this method:
     * 1) Create an ArrayList of WordNodes and a Queue of WordNodes
     * 2) enqueue the root of the tree
     * 3) while the queue is NOT empty:
     * -dequeue a node, add it to the arraylist
     * -if the left child of that node is not null, enqueue it
     * -if the right child of that node is not null, enqueue it
     * 4) return your ArrayList
     * 
     * @return arraylist containing WordNodes of this tree, ordered via level-order
     */
    public ArrayList<WordNode> levelOrder() {
        // WRITE YOUR CODE HERE
        ArrayList<WordNode> result = new ArrayList<>();
        if (root == null) return result; //return empty list.
    
        Queue<WordNode> queue = new Queue<>();
        queue.enqueue(root); //root node.
    
        while (!queue.isEmpty()) {
        WordNode currentNode = queue.dequeue(); // Dequeue a node.
        result.add(currentNode); //add node to result list.
        
        if (currentNode.getLeft() != null) {
            queue.enqueue(currentNode.getLeft()); //enqueue left child
        }
        if (currentNode.getRight() != null) {
            queue.enqueue(currentNode.getRight()); // enqueue right child
        }
        }
    
        return result;
    }

    /**
     * This method is provided for you, do not edit it.
     * 
     * This inserts a new WordNode in this BST, containing the given
     * word and the given definition
     * 
     * @param word       The word to add
     * @param definition The definition of the word
     */
    public void addWord(String word, String definition) {
        // DO NOT EDIT
        if (root == null) {
            root = new WordNode(word, definition);
            return;
        }
        WordNode ptr = root;
        while (ptr != null) {
            if (word.compareTo(ptr.getWord()) < 0) {
                if (ptr.getLeft() == null) {
                    ptr.setLeft(new WordNode(word, definition));
                    return;
                }
                ptr = ptr.getLeft();
            } else if (word.compareTo(ptr.getWord()) > 0) {
                if (ptr.getRight() == null) {
                    ptr.setRight(new WordNode(word, definition));
                    return;
                }
                ptr = ptr.getRight();
            } else {
                return;
            }
        }
    }

    /**
     * This method is provided for you, do not edit it.
     * 
     * @return the root node of this BST
     */
    public WordNode getRoot() {
        // DO NOT EDIT
        return this.root;
    }
}
