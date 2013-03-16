package ca.polymtl.inf8405.sevenwonders.model;

public class Card {
	private String name_;
	public Card(String name){
		name_ = name;
	}
	
	public String getName(){
		return name_;
	}
	
	public void setName(String name){
		name_ = name;
	}
}
