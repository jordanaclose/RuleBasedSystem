/*
References:
	https://www.java2blog.com/depth-first-search-in-java/
	https://stackoverflow.com/questions/19330731/tree-implementation-in-java-root-parents-and-children
*/


import java.lang.*;
import java.util.*;

public class RBS{

	static Node root = new Node("bg", false); //intialise root node
	static final String GOAL = "bg";
	static Node currentGoal = new Node("bg");
	static List<Node> subgoals = new ArrayList<Node>();
	static List<Node> nodes = new ArrayList<Node>();

	static boolean found;
	static boolean isInWorkingMemory;
	static boolean ruleFire;

	static ArrayList<String> workingMemory = new ArrayList<String>();

	static class Node{ //class for rule base as a tree structure - each string is a node in the tree with the goal as the root node
		List<Node> children = new ArrayList<Node>(); // the children of each node are its antecedents and the parent is the consequent
		Node parent = null;
		String data;
		int rule;
		boolean found;
		boolean visited;

		Node(String data){
			this.data = data;
		}

		Node(String data, boolean found){
			this.data = data;
			this.found = found;
		}

		Node(String data, int rule, boolean found){ //the node objects have the option to have their rule number and whether or not they've been found as their parameters
			this.data = data;
			this.rule = rule;
			this.found = found;
		}

		void addChild(Node child){
			child.setParent(this);
			this.children.add(child);
		}

		void addChild(String data, int rule){ //method to add children (antecedents) to each node in the tree
			Node newChild = new Node(data, rule, found);
			newChild.setParent(this);
			children.add(newChild);
		}

		void addChildren(List<Node> children){ //add all the children of a node to an arraylist of children
			for(Node n : children){
				n.setParent(this);
			}
			this.children.addAll(children);
		}

		List<Node> getChildren(){
			return children;
		}

		//setters and getters for data (the string), rule number, whether or not they've been found and the parent of the node

		String getData(){
			return data;
		}

		int getRule(){
			return rule;
		}

		void setRule(int rule){
			this.rule = rule;
		}

		void setData(String data){
			this.data = data;
		}

		boolean getFound(){
			return found;
		}

		void setFound(boolean found){
			this.found = found;
		}

		void setParent(Node parent){
			this.parent = parent;
		}

		Node getParent(){
			return parent;
		}
	}

	void matchSubGoal(Node node){ //this method uses depth first search through the tree starting with the subgoals to attempt to prove them
		for(int i = 0; i < subgoals.size(); i++){
			Node s = subgoals.get(i);
			for(int j = 0; j < workingMemory.size(); j++){
				if(s.getData().equals(workingMemory.get(j))){
					System.out.println("(" + s.getData() + ") found in working memory!"); // for each subgoal, if it can be found in the working memory
					for(int k = 0; k < nodes.size(); k++){									// then the console prints out a message saying so, and
						if(nodes.get(k).getData().equals(s.getData())){						// the found subgoal is removed from the list of subgoals.
							nodes.get(k).setFound(true);									// Its "found" parameter is also set to "true"
						}
					}
					if(subgoals.size() != 0){ //only try to remove subgoals if the arraylist isn't empty to avoid errors
						subgoals.remove(i); //if the subgoal is found in the working memory, remove it from the list of subgoals
						System.out.print("Subgoals: ");
						for(int m = 0; m < subgoals.size(); m++){
							System.out.print("(" + subgoals.get(m).getData() + ") "); //then print out the list of new subgoals
						}
						System.out.println();
					}
				}
			}
		}
		if(subgoals.size() != 0){ //set the current goal to the first element in the list of subgoals (i.e. the leftmost child in DFS)
			currentGoal.setData(subgoals.get(0).getData());
			match(subgoals.get(0));													// if not found in working memory, DFS is performed again on
		}																			// the first subgoal to get more subgoals and prove them
	}

	void match(Node node){ //this method uses depth first search to search through the tree until it finds an antecendent whose consequent matches the goal
		List<Node> children = node.getChildren();
		for(int i = 0; i < children.size(); i++){
			if(children.get(i).getFound() == false){ //search through the list of children for the given node - if child has not been found already, initialise that child as a new node
				Node n = children.get(i);
				if(n.getParent().getData().equals(currentGoal.getData())){ //if the parent (consequent) of the node matches the current goal, print:
					System.out.println("Found RULE" + n.getRule() + " with consequent that matches goal (" + currentGoal.getData() + ")");
					select(node); //then perform select, matchSubGoal and act on the node
					matchSubGoal(node);
					act(n);
					break;
				}
				if(children.size() != 0 && !n.visited){ //otherwise, keep searching through the non-visited nodes and perform match() again recursively
					match(n);
					if(n.getChildren().size() <= 2){
						n.visited = true;
					}
				}
			}
		}
	}

	void select(Node node){ //this method takes the children (antecedents) of the current goal and makes them subgoals
		subgoals.clear();
		System.out.print("Subgoals: ");
		for(int i = 0; i < node.children.size(); i++){
			if(node.children.get(i).getFound() == false){
				subgoals.add(node.children.get(i));
				if(subgoals.size() >= i){
					System.out.print("(" + subgoals.get(i).getData() + ") ");
				}
			}
		}
		System.out.println();
	}

	void act(Node node){ //this method checks if all the antecedents of a rule are satisfied - if so then the rule fires and the consequent is added to the working memory
		ruleFire = true;
		for(int i = 0; i < node.getParent().children.size(); i++){
			if(node.getParent().children.get(i).getFound() == false){
				ruleFire = false;
			}
		}
		if(ruleFire == true && node.getParent().children.size() != 0){
			node.getParent().setFound(true);
			System.out.println("All antecedents of (" + node.getParent().getData() + ") are true, so RULE" + node.getRule() + " fires!");
			workingMemory.add(node.getParent().getData());
			System.out.println("New Working Memory: " + workingMemory);
			currentGoal.setData(node.getParent().getParent().getData());
		}
	}

	void inferenceEngine(){
		for(int i = 0; i < workingMemory.size(); i++){ //check if the goal is in the working memory already
			if(GOAL.equals(workingMemory.get(i))){
				isInWorkingMemory = true;
				break;
			}
			else{
				isInWorkingMemory = false;
				break;
			}
		}
		if(isInWorkingMemory == true){
			System.out.println("Goal found in working memory!");
		}
		else{ //if not found in working memory, perform the match() function until all nodes are in the working memory
			while(workingMemory.size() != nodes.size()-1){
				match(root);
			}
		}
		if(workingMemory.size() == nodes.size()-1){ //when working memory contains all nodes up to the goal node, print out final lines saying the goal has been found
			System.out.println("All antecedents of (bg) are true, so RULE1 fires!");
			System.out.println("Goal " + GOAL + " has been found!");
		}
	}

	static void print(){ //prints out the goal and initial working memory before any algorithms happen
		System.out.println("Goal: " + GOAL);
		System.out.println("Working Memory: " + workingMemory);
		System.out.println("------------------------------------");
	}

	public static void main(String args[]){
		//add strings to working memory
		workingMemory.add("fh");
		workingMemory.add("djz");
		workingMemory.add("uv");
		workingMemory.add("rt");

		//initialise nodes with their data, rule number and boolean found
		Node fh = new Node("fh", 1, false);
		Node ac = new Node("ac", 1, false);
		Node djz = new Node("djz", 4, false);
		Node em = new Node("em", 4, false);
		Node ns = new Node("ns", 2, false);
		Node pq = new Node("pq", 5, false);
		Node rt = new Node("rt", 3, false);
		Node ki = new Node("ki", 4, false);
		Node uv = new Node("uv", 6, false);

		//add the nodes to a list of node objects
		nodes.add(root);
		nodes.add(fh);
		nodes.add(ac);
		nodes.add(djz);
		nodes.add(em);
		nodes.add(ns);
		nodes.add(pq);
		nodes.add(rt);
		nodes.add(ki);
		nodes.add(uv);

		//add the children to each node
		root.addChild(fh);
		root.addChild(ac);
		ac.addChild(djz);
		ac.addChild(em);
		ac.addChild(ki);
		em.addChild(ns);
		ns.addChild(pq);
		pq.addChild(rt);
		ki.addChild(uv);

		print();

		RBS rbs = new RBS();

		//perform inferenceEngine() method
		rbs.inferenceEngine();
	}
}