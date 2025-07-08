package braille;

import java.util.ArrayList;

/**
 * Contains methods to translate Braille to English and English to Braille using
 * a BST.
 * Reads encodings, adds characters, and traverses tree to find encodings.
 * 
 * @author Seth Kelley
 * @author Kal Pandit
 */
public class BrailleTranslator {

    private TreeNode treeRoot;

    /**
     * Default constructor, sets symbols to an empty ArrayList
     */
    public BrailleTranslator() {
        treeRoot = null;
    }

    /**
     * Reads encodings from an input file as follows:
     * - One line has the number of characters
     * - n lines with character (as char) and encoding (as string) space-separated
     * USE StdIn.readChar() to read character and StdIn.readLine() after reading
     * encoding
     * 
     * @param inputFile the input file name
     */
    public void createSymbolTree(String inputFile) {

        /* PROVIDED, DO NOT EDIT */

        StdIn.setFile(inputFile);
        int numberOfChars = Integer.parseInt(StdIn.readLine());
        for (int i = 0; i < numberOfChars; i++) {
            Symbol s = readSingleEncoding();
            addCharacter(s);
        }
    }

    /**
     * Reads one line from an input file and returns its corresponding
     * Symbol object
     * 
     * ONE line has a character and its encoding (space separated)
     * 
     * @return the symbol object
     */
    public Symbol readSingleEncoding() {
        // WRITE YOUR CODE HERE
        char character = StdIn.readChar();
        String encoding = StdIn.readString(); 
        StdIn.readLine();
        return new Symbol(character, encoding);
    }

    /**
     * Adds a character into the BST rooted at treeRoot.
     * Traces encoding path (0 = left, 1 = right), starting with an empty root.
     * Last digit of encoding indicates position (left or right) of character within
     * parent.
     * 
     * @param newSymbol the new symbol object to add
     */
    public void addCharacter(Symbol newSymbol) {
        // WRITE YOUR CODE HERE
        if (treeRoot == null) {
            treeRoot = new TreeNode(new Symbol(""), null, null); // Root starts with empty encoding
        }
    
        TreeNode current = treeRoot;
        String encoding = newSymbol.getEncoding();
        String partialEncoding = "";
    
        for (int i = 0; i < encoding.length(); i++) {
            char direction = encoding.charAt(i);
            partialEncoding += direction;
    
            if (direction == 'L') {
                if (current.getLeft() == null) {
                    current.setLeft(new TreeNode(new Symbol(partialEncoding), null, null));
                }
                current = current.getLeft();
            } else { // direction == 'R'
                if (current.getRight() == null) {
                    current.setRight(new TreeNode(new Symbol(partialEncoding), null, null));
                }
                current = current.getRight();
            }
        }
        current.setSymbol(new Symbol(newSymbol.getCharacter(), encoding));
    }

    /**
     * Given a sequence of characters, traverse the tree based on the characters
     * to find the TreeNode it leads to
     * 
     * @param encoding Sequence of braille (Ls and Rs)
     * @return Returns the TreeNode of where the characters lead to, or null if there is no path
     */
    public TreeNode getSymbolNode(String encoding) {
        // WRITE YOUR CODE HERE
        TreeNode current = treeRoot;
        for (int i = 0; i < encoding.length(); i++) {
            char direction = encoding.charAt(i);
        
        if (direction == 'L') {
            if (current.getLeft() == null) {
                return null;
            }
            current = current.getLeft();
        } else if (direction == 'R') {
            if (current.getRight() == null) {
                return null;
            }
            current = current.getRight();
        }
        }
        return current;
    }

    /**
     * Given a character to look for in the tree will return the encoding of the
     * character
     * 
     * @param character The character that is to be looked for in the tree
     * @return Returns the String encoding of the character
     */
    public String findBrailleEncoding(char character) {
        // WRITE YOUR CODE HERE
        return findBrailleEncodingHelper(treeRoot, character);
    }
    private String findBrailleEncodingHelper(TreeNode node, char character) {
        if (node == null) return null;
        if (node.getSymbol().hasCharacter() && node.getSymbol().getCharacter() == character) {
            return node.getSymbol().getEncoding();
        }
        String leftSearch = findBrailleEncodingHelper(node.getLeft(), character);
        if (leftSearch != null) return leftSearch;
        return findBrailleEncodingHelper(node.getRight(), character);
    }

    /**
     * Given a prefix to a Braille encoding, return an ArrayList of all encodings that start with
     * that prefix
     * 
     * @param start the prefix to search for
     * @return all Symbol nodes which have encodings starting with the given prefix
     */
    public ArrayList<Symbol> encodingsStartWith(String start) {
        // WRITE YOUR CODE HERE
        ArrayList<Symbol> symbols = new ArrayList<>();
        TreeNode startNode = getSymbolNode(start);
        if (startNode != null) {
            preorder(startNode, symbols);
        }
        return symbols;
    }
    private void preorder(TreeNode node, ArrayList<Symbol> symbols) {
        if (node == null) return;
        if (node.getSymbol().hasCharacter()) {
            symbols.add(node.getSymbol());
        }
        preorder(node.getLeft(), symbols);
        preorder(node.getRight(), symbols);
    }

    /**
     * Reads an input file and processes encodings six chars at a time.
     * Then, calls getSymbolNode on each six char chunk to get the
     * character.
     * 
     * Return the result of all translations, as a String.
     * @param input the input file
     * @return the translated output of the Braille input
     */
    public String translateBraille(String input) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(input);
        String encoding = StdIn.readString();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encoding.length(); i += 6) {
            String chunk = encoding.substring(i, Math.min(i + 6, encoding.length()));
            TreeNode node = getSymbolNode(chunk);
            if (node != null) {
                result.append(node.getSymbol().getCharacter());
            }
        }
        return result.toString();
    }

    /**
     * Given a character, delete it from the tree and delete any encodings not
     * attached to a character (ie. no children).
     * 
     * @param symbol the symbol to delete
     */
    public void deleteSymbol(char symbol) {
        // WRITE YOUR CODE HERE
        String encoding = findBrailleEncoding(symbol);
        if (encoding == null) return;
        deleteSymbolHelper(treeRoot, encoding, 0);
    }
    private TreeNode deleteSymbolHelper(TreeNode node, String encoding, int depth) {
        if (node == null) {
            return null;
        }
        if (depth == encoding.length()) {
            if (node.getLeft() == null && node.getRight() == null) {
                return null; 
            } else {
                node.setSymbol(new Symbol(node.getSymbol().getEncoding())); // Convert to intermediate node
                return node;
            }
        }
        char direction = encoding.charAt(depth);
        if (direction == 'L') {
            node.setLeft(deleteSymbolHelper(node.getLeft(), encoding, depth + 1));
            if (node.getLeft() == null && node.getRight() == null && node.getSymbol().getCharacter() == Character.MIN_VALUE) {
                return null;
            }
        } else if (direction == 'R') {
            node.setRight(deleteSymbolHelper(node.getRight(), encoding, depth + 1));
            if (node.getLeft() == null && node.getRight() == null && node.getSymbol().getCharacter() == Character.MIN_VALUE) {
                return null;
            }
        }
        return node;
    }

    public TreeNode getTreeRoot() {
        return this.treeRoot;
    }

    public void setTreeRoot(TreeNode treeRoot) {
        this.treeRoot = treeRoot;
    }

    public void printTree() {
        printTree(treeRoot, "", false, true);
    }

    private void printTree(TreeNode n, String indent, boolean isRight, boolean isRoot) {
        StdOut.print(indent);

        // Print out either a right connection or a left connection
        if (!isRoot)
            StdOut.print(isRight ? "|+R- " : "--L- ");

        // If we're at the root, we don't want a 1 or 0
        else
            StdOut.print("+--- ");

        if (n == null) {
            StdOut.println("null");
            return;
        }
        // If we have an associated character print it too
        if (n.getSymbol() != null && n.getSymbol().hasCharacter()) {
            StdOut.print(n.getSymbol().getCharacter() + " -> ");
            StdOut.print(n.getSymbol().getEncoding());
        }
        else if (n.getSymbol() != null) {
            StdOut.print(n.getSymbol().getEncoding() + " ");
            if (n.getSymbol().getEncoding().equals("")) {
                StdOut.print("\"\" ");
            }
        }
        StdOut.println();

        // If no more children we're done
        if (n.getSymbol() != null && n.getLeft() == null && n.getRight() == null)
            return;

        // Add to the indent based on whether we're branching left or right
        indent += isRight ? "|    " : "     ";

        printTree(n.getRight(), indent, true, false);
        printTree(n.getLeft(), indent, false, false);
    }

}
