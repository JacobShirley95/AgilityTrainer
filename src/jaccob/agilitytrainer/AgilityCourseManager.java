package jaccob.agilitytrainer;

import jaccob.agilitytrainer.Course.Obstacle;

public class AgilityCourseManager {
	private Course course;
	private int currentOb;

	public AgilityCourseManager(Course course) {
		this.course = course;
	}
	
	public Course getCourse() {
		return course;
	}
	
	public Obstacle current() {
		return course.obstacles[currentOb];
	}
	
	public Obstacle peekNext() {
		return course.obstacles[(currentOb + 1) % course.obstacles.length];
	}
	
	public void reset() {
		currentOb = 0;
	}
	
	public Obstacle peekPrev() {
		int len = course.obstacles.length;
		
		int id = currentOb - 1;
		if (id < 0)
			id = len + id;
		
		return course.obstacles[id];
	}
	
	public boolean atStart() {
		return currentOb == 0;
	}
	
	public boolean atEnd() {
		return currentOb == course.obstacles.length;
	}
	
	public Obstacle next() {
		currentOb = (currentOb + 1) % course.obstacles.length;
		return current();
	}
	
	public Obstacle prev() {
		int len = course.obstacles.length;
		
		currentOb -= 1;
		if (currentOb < 0)
			currentOb += len;
		
		return current();
	}
}
