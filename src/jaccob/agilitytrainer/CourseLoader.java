package jaccob.agilitytrainer;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class CourseLoader {
	private Course[] courses;
	
	public CourseLoader(String file) throws FileNotFoundException {
		Gson obstacleJson = new Gson();
		JsonReader reader = new JsonReader(new FileReader("resources/courses.json"));
		CourseData[] data = obstacleJson.fromJson(reader, CourseData[].class);
		courses = new Course[data.length];
		
		for (int i = 0; i < courses.length; i++) {
			courses[i] = new Course();
			courses[i].fromData(data[i]);
		}
	}
	
	public Course[] getCourses() {
		return courses;
	}
	
	public Course getCourse(int id) {
		return courses[id];
	}
	
	public static final Tile arrayToTile(int[] arr) {
		return new Tile(arr[0], arr[1], arr[2]);
	}
	
	public static final Area getArea(int[][] area) {
		Tile[] ts = new Tile[area.length];
		for (int i = 0; i < area.length; i++) {
			ts[i] = arrayToTile(area[i]);
		}
		return new Area(ts[0], ts[1]);
	}
}
