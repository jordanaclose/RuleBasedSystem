/*
References:
	https://www.java2blog.com/depth-first-search-in-java/
	https://stackoverflow.com/questions/19330731/tree-implementation-in-java-root-parents-and-children

*/


import java.lang.*;
import java.util.*;

public class RuleBasedSystem{

	static Node goal = new Node("bg", false);
	static final String GOAL = "bg";
	static Node currentGoal = new Node("bg");
	static List<Node> subgoals = new ArrayList<Node>();

	static boolean found;
	static boolean isInWorkingMemory;

	static ArrayList<String> workingMemory = new ArrayList<String>();

	static class Node{
		List<Node> children = new ArrayList<Node>();
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

		Node(String data, int rule, boolean found){
			this.data = data;
			this.rule = rule;
			this.found = found;
		}

		void addChild(Node child){
			child.setParent(this);
			this.children.add(child);
		}

		void addChild(String data, int rule){
			Node newChild = new Node(data, rule, found);
			newChild.setParent(this);
			children.add(newChild);
		}

		void addChildren(List<Node> children){
			for(Node n : children){
				n.setParent(this);
			}
			this.children.addAll(children);
		}

		List<Node> getChildren(){
			return children;
		}

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

	void matchSubGoal(){
		System.out.print("New subgoals: ");
		for(int i = 0; i < subgoals.size(); i++){
			System.out.print("(" + subgoals.get(i).getData() + ") ");
		}
		for(int i = 0; i < workingMemory.size(); i++){ //check if the subgoal is in the working memory
			for(int j = 0; j < subgoals.size(); j++){
				if(workingMemory.get(i).equals(subgoals.get(j).getData())){
					subgoals.get(j).visited = true;
					subgoals.get(j).setFound(true);
					break;
				}
			}
		}
		System.out.println();
		for(int j = 0; j < subgoals.size(); j++){
			if(subgoals.get(j).getFound() == true){
				System.out.println("(" + subgoals.get(j).getData() + ") already in working memory");
				subgoals.remove(j);
			}
		}
		//List<Node> children = currentGoal.getChildren();
		//if(children.size() == 0 && currentGoal.getFound() == true){
		//	currentGoal.setData(currentGoal.getParent().getData());
		//}
		//else{
			System.out.print("New subgoals: ");
			for(int k = 0; k < subgoals.size(); k++){
				System.out.print("(" + subgoals.get(k).getData() + ") ");
			}
		//}
		System.out.println();
		if(subgoals.size() == 0){
			currentGoal.setFound(true);
			for(int i = 0; i < currentGoal.getParent().getChildren().size(); i++){
				if(currentGoal.getParent().getChildren().get(i).getFound() == true){
					System.out.println("All antecendents proven so RULE" + currentGoal.getRule() + " fires!");
					currentGoal.setData(currentGoal.getParent().getData());
				}
			}
		}
		else{
			currentGoal.setData(subgoals.get(0).getData());
			subgoals.clear();
			matchGoal(currentGoal);
		}
		//System.out.println(workingMemory);
		matchGoal(goal);
	}

	void matchGoal(Node node){
		List<Node> children = node.getChildren();
		for(int i = 0; i < children.size(); i++){
			Node n = children.get(i);
			for(int j = 0; j < workingMemory.size(); j++){ //check if the subgoal is in the working memory
				//if(n.getData().equals(workingMemory.get(j))){
				//	n.visited = true;
				//	n.setFound(true);
				//	break;
				//}
				if(n.getParent().getData().equals(currentGoal.getData())){
					System.out.println("Found RULE" + n.getRule() + " with consequent that matches goal (" + currentGoal.getData() + ")");
					for(int k = 0; k < children.size(); k++){
						subgoals.add(children.get(k));
					}
					matchSubGoal();
					break;
				}
			}
			if(children.size() != 0 && !n.visited){
				matchGoal(n);
				n.visited = true;
			}
		}
	}

	void select(){

	}

	void act(){

	}

	void inferenceEngine(){
		for(int i = 0; i < workingMemory.size(); i++){
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
		else{
			matchGoal(goal);
			//matchSubGoal();
			//select();
			//act();
		}
	}

	static void print(){
		System.out.println("Goal: " + GOAL);
		System.out.println("Working Memory: " + workingMemory);
		System.out.println("------------------------------------");
	}

	public static void main(String args[]){
		workingMemory.add("fh");
		workingMemory.add("dj");
		workingMemory.add("uv");
		workingMemory.add("rt");

		Node fh = new Node("fh", 1, false);
		Node ac = new Node("ac", 1, false);
		Node dj = new Node("dj", 4, false);
		Node em = new Node("em", 4, false);
		Node ns = new Node("ns", 2, false);
		Node pq = new Node("pq", 5, false);
		Node rt = new Node("rt", 3, false);
		Node ki = new Node("ki", 4, false);
		Node uv = new Node("uv", 6, false);

		goal.addChild(fh);
		goal.addChild(ac);
		ac.addChild(dj);
		ac.addChild(em);
		ac.addChild(ki);
		em.addChild(ns);
		ns.addChild(pq);
		pq.addChild(rt);
		ki.addChild(uv);

		print();

		RuleBasedSystem rbs = new RuleBasedSystem();

		rbs.inferenceEngine();
	}
}