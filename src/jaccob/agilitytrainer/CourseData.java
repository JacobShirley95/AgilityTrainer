package jaccob.agilitytrainer;


public class CourseData {
	public String title;
	public int[][] startArea;
	public ObstacleData[] obstacles;
	
	public class ObstacleData {
		public String title;
		public int id;
		public boolean rightClick;
		public int[] bounds;
		public int[] yaws;
		public int time;
		public int[] endTile;
		public int finishRadius;
		public int delay = 0;
		public int[][][] path;
	}
	
	static class Status {
		public int courseId = 0;
		public int obstacleId = 0;
		
		@Override
		public String toString() {
			return "CourseID: " + courseId + ", ObstacleID: " + obstacleId;
		}
	}
}
