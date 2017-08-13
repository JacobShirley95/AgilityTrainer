package jaccob.agilitytrainer;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

@Manifest(name="AgilityTrainer", description="description", properties="")
public class AgilityTrainer extends PollingScript<ClientContext> {
	static final int ROUGH_WALL_ID = 10073;
	static final int[] ROUGH_WALL_BOUNDS = {8, 28, -200, -20, 30, 98};
	static final int[] ROUGH_WALL_YAWS = {51, 86};
	
	static final int TIGHT_ROPE_ID = 3625;
	static final int[] TIGHT_ROPE_BOUNDS = {20, 56, -32, 0, 8, 128};
	static final int[] TIGHT_ROPE_YAWS_1 = {51, 86};
	static final int[] TIGHT_ROPE_YAWS_2 = {169, 182};
	
	static final Obstacle[] OBSTACLES = {new Obstacle(ROUGH_WALL_ID, false, ROUGH_WALL_BOUNDS, ROUGH_WALL_YAWS),
										 getTightRope(TIGHT_ROPE_YAWS_1, 0),
										 getTightRope(TIGHT_ROPE_YAWS_1, 2)};
	
	@Override
	public void start() {
		Obstacle o = OBSTACLES[2];
		ctx.objects.select();
		BasicQuery<GameObject> query = null;
		if (o.mesh)
			query = ctx.objects.select(byMeshIds(o.id));
		else
			query = ctx.objects.id(o.id);
		
		GameObject target = query.peek();
		target.bounds(o.bounds);
		System.out.println(target);
		target.hover();
	}
	
	private Filter<GameObject> byMeshIds(int... ids) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject go) {
				return Arrays.equals(go.meshIds(), ids);
			}
		};
	}
	
	static Obstacle getTightRope(int[] yaws, int orientation) {
		Obstacle o = new Obstacle(TIGHT_ROPE_ID, true, TIGHT_ROPE_BOUNDS, yaws);
		o.orientation = (byte) orientation;
		return o;
	}
	
	@Override
	public void poll() {
		// TODO Auto-generated method stub
		
	}

	static class Obstacle {
		public int id;
		public boolean mesh;
		public int[] bounds;
		public int[] yaws;
		public byte orientation;
		
		public Obstacle(int id, boolean mesh, int[] bounds, int[] yaws) {
			this.id = id;
			this.mesh = mesh;
			this.bounds = bounds;
			this.yaws = yaws;
		}
	}
}
