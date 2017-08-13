package jaccob.agilitytrainer;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

@Manifest(name="AgilityTrainer", description="description", properties="")
public class AgilityTrainer extends PollingScript<ClientContext> {
	static final int ROUGH_WALL_ID = 10073;
	static final int[] ROUGH_WALL_BOUNDS = {8, 28, -200, -20, 30, 98};
	static final int[] ROUGH_WALL_YAWS = {51, 86};
	
	static final int TIGHT_ROPE_1_ID = 10074;
	static final int[] TIGHT_ROPE_1_BOUNDS = {20, 56, -32, 0, 8, 128};
	static final int[] TIGHT_ROPE_1_YAWS = {51, 86};
	
	static final int TIGHT_ROPE_2_ID = 10075;
	static final int[] TIGHT_ROPE_2_BOUNDS = {8, 128, -32, 0, 20, 56};
	static final int[] TIGHT_ROPE_2_YAWS = {169, 182};
	
	static final int NARROW_WALL_ID = 10077;
	static final int[] NARROW_WALL_BOUNDS = {-32, 32, -20, 0, -32, 32};
	static final int[] NARROW_WALL_YAWS = {51, 86};
	
	static final int WALL_ID = 10084;
	static final int[] WALL_BOUNDS = {-32, 32, 144, 0, -56, -76};
	static final int[] WALL_YAWS = {270, 289};
	
	static final int GAP_ID = 10085;
	static final int[] GAP_BOUNDS = {-116, 32, -16, 0, -28, 56};
	static final int[] GAP_YAWS = {333, 360};
	
	static final int CRATE_ID = 10086;
	static final int[] CRATE_BOUNDS = {-140, 8, -28, 0, -92, 32};
	static final int[] CRATE_YAWS = {51, 86};
	
	static final Obstacle[] OBSTACLES = {new Obstacle(ROUGH_WALL_ID, ROUGH_WALL_BOUNDS, ROUGH_WALL_YAWS, new Tile(3102, 3279, 3)),
										 new Obstacle(TIGHT_ROPE_1_ID, TIGHT_ROPE_1_BOUNDS, TIGHT_ROPE_1_YAWS, new Tile(3090, 3276, 3)),
										 new Obstacle(TIGHT_ROPE_2_ID, TIGHT_ROPE_2_BOUNDS, TIGHT_ROPE_2_YAWS, new Tile(3092, 3266, 3)),
										 new Obstacle(TIGHT_ROPE_2_ID, TIGHT_ROPE_2_BOUNDS, TIGHT_ROPE_2_YAWS, new Tile(3092, 3266, 3)),
										 new Obstacle(TIGHT_ROPE_2_ID, TIGHT_ROPE_2_BOUNDS, TIGHT_ROPE_2_YAWS, new Tile(3092, 3266, 3)),
										 new Obstacle(TIGHT_ROPE_2_ID, TIGHT_ROPE_2_BOUNDS, TIGHT_ROPE_2_YAWS, new Tile(3092, 3266, 3))};
	
	@Override
	public void start() {
		Obstacle o = OBSTACLES[2];
		ctx.objects.select();
		
		BasicQuery<GameObject> query = ctx.objects.id(o.id);
		
		GameObject target = query.peek();
		target.bounds(o.bounds);
	}
	
	final boolean doObstacle(Obstacle ob) {
		
		
		return false;
	}
	
	@Override
	public void poll() {
		//if level 0 then failed
	}

	static class Obstacle {
		public int id;
		public int[] bounds;
		public int[] yaws;
		public Tile end;
		
		public Obstacle(int id, int[] bounds, int[] yaws, Tile end) {
			this.id = id;
			this.bounds = bounds;
			this.yaws = yaws;
			this.end = end;
		}
	}
}
