package jaccob.agilitytrainer;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game.Crosshair;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.LocalPath;
import org.powerbot.script.rt4.Player;
import org.powerbot.script.rt4.TilePath;

@Manifest(name="AgilityTrainer", description="description", properties="")
public class AgilityTrainer extends PollingScript<ClientContext> {
	static final int ROUGH_WALL_ID = 10073;
	static final int[] ROUGH_WALL_BOUNDS = {8, 28, -200, -20, 30, 98};
	static final int[] ROUGH_WALL_YAWS = {51, 86};
	static final int ROUGH_WALL_TIME = 5000;
	static final Tile ROUGH_WALL_END = new Tile(3102, 3279, 3);
	
	static final int TIGHT_ROPE_1_ID = 10074;
	static final int[] TIGHT_ROPE_1_BOUNDS = {20, 56, -32, 0, 8, 128};
	static final int[] TIGHT_ROPE_1_YAWS = {169, 182};
	static final int TIGHT_ROPE_1_TIME = 10000;
	static final Tile TIGHT_ROPE_1_END = new Tile(3090, 3277, 3);
	
	static final int TIGHT_ROPE_2_ID = 10075;
	static final int[] TIGHT_ROPE_2_BOUNDS = {8, 128, -32, 0, 20, 56};
	static final int[] TIGHT_ROPE_2_YAWS = {169, 182};
	static final int TIGHT_ROPE_2_TIME = 10000;
	static final Tile TIGHT_ROPE_2_END = new Tile(3092, 3267, 3);
	
	static final int NARROW_WALL_ID = 10077;
	static final int[] NARROW_WALL_BOUNDS = {-32, 32, -20, 0, -32, 32};
	static final int[] NARROW_WALL_YAWS = {217, 256};
	static final int NARROW_WALL_TIME = 10000;
	static final Tile NARROW_WALL_END = new Tile(3088, 3261, 3);
	
	static final int WALL_ID = 10084;
	static final int[] WALL_BOUNDS = {-32, 32, 144, 0, -56, -76};
	static final int[] WALL_YAWS = {300, 320};
	static final int WALL_TIME = 10000;
	static final Tile WALL_END = new Tile(3088, 3255, 3);
	
	static final int GAP_ID = 10085;
	static final int[] GAP_BOUNDS = {-116, 32, -16, 0, -28, 56};
	static final int[] GAP_YAWS = {0, 17};
	static final int GAP_TIME = 10000;
	static final Tile GAP_END = new Tile(3096, 3256, 3);
	
	static final int CRATE_ID = 10086;
	static final int[] CRATE_BOUNDS = {-140, 8, -28, 0, -92, 32};
	static final int[] CRATE_YAWS = {74, 113};
	static final int CRATE_TIME = 10000;
	static final Tile CRATE_END = new Tile(3103, 3261, 0);
	static final Area[] CRATE_PATH = {new Area(new Tile(3098, 3258, 3), new Tile(3100, 3260, 3))};
	
	static final Obstacle[] OBSTACLES = {new Obstacle(ROUGH_WALL_ID, ROUGH_WALL_BOUNDS, ROUGH_WALL_YAWS, ROUGH_WALL_TIME, ROUGH_WALL_END),
										 new Obstacle(TIGHT_ROPE_1_ID, TIGHT_ROPE_1_BOUNDS, TIGHT_ROPE_1_YAWS, TIGHT_ROPE_1_TIME, TIGHT_ROPE_1_END),
										 new Obstacle(TIGHT_ROPE_2_ID, TIGHT_ROPE_2_BOUNDS, TIGHT_ROPE_2_YAWS, TIGHT_ROPE_2_TIME, TIGHT_ROPE_2_END),
										 new Obstacle(NARROW_WALL_ID, NARROW_WALL_BOUNDS, NARROW_WALL_YAWS, NARROW_WALL_TIME, NARROW_WALL_END),
										 new Obstacle(WALL_ID, WALL_BOUNDS, WALL_YAWS, WALL_TIME, WALL_END),
										 new Obstacle(GAP_ID, GAP_BOUNDS, GAP_YAWS, GAP_TIME, GAP_END),
										 new Obstacle(CRATE_ID, CRATE_BOUNDS, CRATE_YAWS, CRATE_TIME, CRATE_END)};
	
	static final Area START_AREA = new Area(new Tile(3103, 3277, 0), new Tile(3105, 3280, 0));
	
	static final int MARKS_OF_GRACE_ID = 11849;
	
	int current = 0;
	
	@Override
	public void start() {
		ctx.camera.pitch(randomRange(60, 80));
	}
	
	final boolean waitTillVisible(GameObject object) {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return object.inViewport();
			}
		}, 50, 100);
	}
	
	final boolean walkToObstacle(Player myPlayer, Obstacle ob, GameObject target) {
		if (ob.path != null && ob.path.length > 0) {
			Tile[] randomPath = new Tile[ob.path.length];
			
			for (int i = 0; i < randomPath.length; i++) 
				randomPath[i] = ob.path[i].getRandomTile();
			
			int c = 0;
			while (!target.inViewport() && c < randomPath.length) {
				Tile t = randomPath[c];
				
				if (t.matrix(ctx).interact("Walk Here")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.movement.distance(ctx.movement.destination()) < 3;
						}
					});
				}
				
				c++;
			}
			
			return target.inViewport();
		} else
			return true;
	}
	
	final boolean clickObstacle(Player myPlayer, Obstacle ob, int current) {
		GameObject target = getObstacleObject(ob);
		
		if (!walkToObstacle(myPlayer, ob, target))
			return false;
		
		if (!waitTillVisible(target))
			return false;
		
		for (int tries = 0; tries < 10; tries++)
			if (target.interact(target.actions()[0]))
				break;
		
		ctx.camera.angle(randomRange(ob.yaws));
		
		return waitObstacle(myPlayer, ob, current);
	}
	
	final boolean waitObstacle(Player myPlayer, Obstacle ob, int index) {
		GameObject next = getObstacleObject(OBSTACLES[(index + 1) % OBSTACLES.length]);
		String[] actions = next.actions();
		
		boolean followObject = Math.random() > 0.6;
		long t = getRuntime() + ob.time;
		
		while (!ctx.controller.isStopping() && getRuntime() <= t) {
			if (myPlayer.tile().equals(ob.end))
				return true;
			
			String[] items = ctx.menu.items();
			if (followObject) {// && items.length > 0 && actions.length > 0 && !items[0].contains(actions[0]))
				//if (Math.random() > 0.9)
					//next.hover();
				ctx.input.move(next.centerPoint());
			}
			
			//Condition.sleep(100 + (int)(Math.random() * 67));
		}
		
		return false;
	}
	
	final int randomRange(int min, int max) {
		return (int)(min + (Math.random() * (max - min)));
	}
	
	final int randomRange(int[] minAndMax) {
		return (int)(minAndMax[0] + (Math.random() * (minAndMax[1] - minAndMax[0])));
	}
	
	final boolean isFinished(Player myPlayer) {
		return myPlayer.tile().equals(OBSTACLES[OBSTACLES.length - 1].end);
	}
	
	final GameObject getObstacleObject(Obstacle ob) {
		if (ob.gameObject != null)
			return ob.gameObject;
		
		GameObject target = ctx.objects.select().id(ob.id).poll();
		target.bounds(ob.bounds);
		//ob.setGameObject(target);
		
		return target;
	}
	
	final Filter<GroundItem> reachable() {
		return new Filter<GroundItem>() {
			@Override
			public boolean accept(GroundItem gi) {
				return gi.tile().matrix(ctx).reachable();
			}
		};
	}
	
	final boolean takeMarksOfGrace() {
		BasicQuery<GroundItem> target = ctx.groundItems.select().id(MARKS_OF_GRACE_ID).nearest().viewable().select(reachable());
		if (target.size() == 0)
			return false;
		
		GroundItem t = target.peek();
		
		int c = ctx.inventory.select().id(11849).count();
		
		t.interact("Take", t.name());
		
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ctx.inventory.select().id(MARKS_OF_GRACE_ID).count(true) > c;
			}
		});
	}
	
	@Override
	public void poll() {
		Player myPlayer = ctx.players.local();
		if (ctx.game.floor() == 0) {
			GameObject first = getObstacleObject(OBSTACLES[0]);
			
			LocalPath path = ctx.movement.findPath(START_AREA.getRandomTile());
			while (!ctx.controller.isStopping() && !first.inViewport() && path.traverse()) {
				ctx.chat.clickContinue();
				
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.movement.distance(ctx.movement.destination()) < 3;
					}
				}, 10, 6000);
			}
			
			if (ctx.controller.isStopping())
				return;
		}

		while (!ctx.controller.isStopping() && current < OBSTACLES.length) {
			for (int tries = 0; tries < 4; tries++) {
				if (clickObstacle(myPlayer, OBSTACLES[current], current)) {
					Condition.sleep(230);
					
					takeMarksOfGrace();
					current++;
					
					System.out.println("next " + current);
					
					break;
				}
				System.out.println("failll " + current);
				
				if (ctx.controller.isStopping() || ctx.game.floor() == 0) {
					current = 0;
					break;
				}
				
				int past = current - 1;
				if (past < 0)
					past += OBSTACLES.length;
				
				Obstacle pastOb = OBSTACLES[past];
				ctx.camera.angle(randomRange(pastOb.yaws));
			}
			
			if (ctx.game.floor() == 0) {
				current = 0;
				break;
			}
		}
		Condition.sleep(200);
		current = 0;
		
		//if level 0 then failed
	}
	
	final boolean hoverSmart(GameObject go) {
		if (!ctx.menu.items()[0].contains(go.actions()[0])) {
			return go.hover();
		}
		return false;
	}
	
	final boolean isAnimating(Player myPlayer) {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return myPlayer.animation() != -1;
			}
		}, 30);
	}

	static class Obstacle {
		public int id;
		public int[] bounds;
		public int[] yaws;
		public int time;
		public Tile end;
		public Area[] path;
		private GameObject gameObject;
		
		public Obstacle(int id, int[] bounds, int[] yaws, int time, Tile end, Area... path) {
			this.id = id;
			this.bounds = bounds;
			this.yaws = yaws;
			this.time = time;
			this.end = end;
			this.path = path;
		}
		
		public void setGameObject(GameObject obj) {
			this.gameObject = obj;
		}
		
		public GameObject getGameObject() {
			return gameObject;
		}
	}
}
