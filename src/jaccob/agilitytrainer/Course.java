package jaccob.agilitytrainer;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class Course {
	public String title;
	public int[] pitches;
	public Area startArea;
	public Obstacle[] obstacles;
	
	public void fromData(CourseData data) {
		title = data.title;
		startArea = CourseLoader.getArea(data.startArea);
		pitches = data.pitches;
		obstacles = new Obstacle[data.obstacles.length];
		
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i] = new Obstacle();
			obstacles[i].id = data.obstacles[i].id;
			obstacles[i].title = data.obstacles[i].title;
			obstacles[i].rightClick = data.obstacles[i].rightClick;
			obstacles[i].bounds = data.obstacles[i].bounds;
			obstacles[i].yaws = data.obstacles[i].yaws;
			obstacles[i].time = data.obstacles[i].time;
			obstacles[i].endTile = CourseLoader.arrayToTile(data.obstacles[i].endTile);
			obstacles[i].finishRadius = data.obstacles[i].finishRadius;
			obstacles[i].delay = data.obstacles[i].delay;
			
			if (data.obstacles[i].path != null) {
				obstacles[i].path = new Area[data.obstacles[i].path.length];
				
				for (int j = 0; j < obstacles[i].path.length; j++) {
					obstacles[i].path[j] = CourseLoader.getArea(data.obstacles[i].path[j]);
				}
			}
		}
	}
	
	public class Obstacle {
		public String title;
		public int id;
		public boolean rightClick;
		public int[] bounds;
		public int[] yaws;
		public int time;
		public Tile endTile;
		public int finishRadius;
		public int delay = 0;
		public Area[] path;
	}
}
