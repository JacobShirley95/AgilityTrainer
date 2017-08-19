package jaccob.agilitytrainer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.LocalPath;
import org.powerbot.script.rt4.Player;

import jaccob.agilitytrainer.Course.Obstacle;

@Manifest(name="AgilityTrainer", description="description", properties="")
public class AgilityTrainer extends PollingScript<ClientContext> implements PaintListener{
	static final int COURSE_ID = 1;
	static final int MARKS_OF_GRACE_ID = 11849;
	static CourseLoader COURSE_LOADER;
	
	static {
		try {
			COURSE_LOADER = new CourseLoader("resources/courses.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	int laps = 0;
	int marksOfGracePickedUp = 0;
	int startXP = -1;
	AgilityCourseManager runner;
	
	final boolean waitTillVisible(GameObject object) {
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return object.inViewport();
			}
		}, 50, 100);
	}
	
	final boolean moving(Player myPlayer) {
		Tile lastPos = myPlayer.tile();
		
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !myPlayer.tile().equals(lastPos);
			}
		}, 50, 40);
	}
	
	final Tile arrayToTile(int[] arr) {
		return new Tile(arr[0], arr[1], arr[2]);
	}
	
	final Area getArea(int[][] area) {
		Tile[] ts = new Tile[area.length];
		for (int i = 0; i < area.length; i++) {
			ts[i] = arrayToTile(area[i]);
			
		}
		return new Area(ts[0], ts[1]);
	}
	
	final boolean walkToObstacle(Player myPlayer, Obstacle ob, GameObject target) {
		if (ob.path != null && ob.path.length > 0) {
			Tile[] randomPath = new Tile[ob.path.length];
			
			for (int i = 0; i < ob.path.length; i++) {
				randomPath[i] = ob.path[i].getRandomTile();
			}
			
			int c = 0;
			while (!target.inViewport() && c < randomPath.length) {
				Tile t = randomPath[c];
				
				if (t.matrix(ctx).interact("Walk Here") || ctx.movement.step(t)) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return target.inViewport() || ctx.movement.distance(ctx.movement.destination()) < 5;
						}
					});
				}
				
				c++;
			}
			
			return target.inViewport();
		} else
			return true;
	}
	
	final boolean clickObstacle(Player myPlayer, Obstacle ob) {
		GameObject target = getObstacleObject(ob);
		
		if (!walkToObstacle(myPlayer, ob, target))
			return false;
		
		if (!waitTillVisible(target))
			return false;
		
		String action = target.actions()[0];
		
		for (int tries = 0; tries < 10; tries++) {
			if (ob.rightClick) {
				if (target.interact(false, action)) {
					break;
				}
			} else if (target.interact(action))
				break;	
		}
		
		ctx.camera.angle(getRandomAngle(ob.yaws));
		
		return waitObstacle(myPlayer, ob);
	}
	
	final boolean waitObstacle(Player myPlayer, Obstacle ob) {
		GameObject next = getObstacleObject(runner.peekNext());

		boolean followObject = Math.random() > 0.9;
		long t = getRuntime() + ob.time;
		
		int xp = ctx.skills.experience(Constants.SKILLS_AGILITY);
		while (!ctx.controller.isStopping() && getRuntime() <= t) {
			if (ctx.skills.experience(Constants.SKILLS_AGILITY) > xp) {
				return true;
			}
			
			if (followObject) {
				ctx.input.move(next.centerPoint());
			}
		}
		
		return false;
	}
	
	final int randomRange(int min, int max) {
		return (int)(min + (Math.random() * (max - min)));
	}
	
	final int randomRange(int[] minAndMax) {
		return (int)(minAndMax[0] + (Math.random() * (minAndMax[1] - minAndMax[0])));
	}
	
	final GameObject getObstacleObject(Obstacle ob) {
		GameObject target = ctx.objects.select().id(ob.id).poll();
		target.bounds(ob.bounds);
		
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
	
	final int countMarksOfGrace() {
		return ctx.inventory.select().id(MARKS_OF_GRACE_ID).count(true);
	}
	
	final boolean takeMarksOfGrace() {
		BasicQuery<GroundItem> target = ctx.groundItems.select().id(MARKS_OF_GRACE_ID).nearest().viewable().select(reachable());
		if (target.size() == 0)
			return false;
		
		GroundItem t = target.peek();
		
		int c = countMarksOfGrace();
		
		for (int i = 0; i < 5; i++) {
			if (t.interact("Take", t.name())) {
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return countMarksOfGrace() > c;
					}
				})) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	final boolean relocate() {
		GameObject first = getObstacleObject(runner.current());
		Area startArea = runner.getCourse().startArea;
				
		LocalPath path = ctx.movement.findPath(startArea.getRandomTile());
		while (!ctx.controller.isStopping() && !first.inViewport() && path.traverse()) {
			ctx.chat.clickContinue();
			
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.movement.distance(ctx.movement.destination()) < 3;
				}
			}, 10, 6000);
		}
		
		return first.inViewport();
	}
	
	final int getRandomAngle(int[] yaws) {
		int r = randomRange(yaws);
		if (r < 0)
			r += 360;
		return r;
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
	
	private int getXPPerHour() {
		if (startXP == -1) {
			startXP = ctx.skills.experience(Constants.SKILLS_AGILITY);
		}
		
		long runTime = getRuntime();
		int currentExp = ctx.skills.experience(Constants.SKILLS_AGILITY);
		int expGain = currentExp - startXP;
		int expPh = (int) (3600000d / (long) runTime * (double) (expGain));
		
		return expPh;
	}
	
	public static String formatInterval(final long interval, boolean millisecs )
	{
	    final long hr = TimeUnit.MILLISECONDS.toHours(interval);
	    final long min = TimeUnit.MILLISECONDS.toMinutes(interval) %60;
	    final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) %60;
	    final long ms = TimeUnit.MILLISECONDS.toMillis(interval) %1000;
	    if( millisecs ) {
	        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	    } else {
	        return String.format("%02d:%02d:%02d", hr, min, sec );
	    }
	}
	
	@Override
	public void repaint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.GREEN);
		g2.drawString("Time running: " + formatInterval(getRuntime(), false), 10, 30);
		g2.drawString("XP Per Hour: " + getXPPerHour(), 10, 60);
		g2.drawString("Laps done: " + laps, 10, 90);
		g2.drawString("Marks of grace: " + marksOfGracePickedUp, 10, 60);
	}
	
	@Override
	public void start() {
		runner = new AgilityCourseManager(COURSE_LOADER.getCourse(COURSE_ID));
		ctx.camera.pitch(randomRange(runner.getCourse().pitches));
	}
	
	@Override
	public void poll() {
		Player myPlayer = ctx.players.local();
		if (ctx.game.floor() == 0) {
			if (!relocate())
				return;
			
			if (ctx.controller.isStopping())
				return;
		}

		while (!ctx.controller.isStopping() && !runner.atEnd()) {
			Obstacle current = runner.current();
			for (int tries = 0; tries < 4; tries++) {
				if (clickObstacle(myPlayer, current)) {
					Condition.sleep(current.delay);
					
					if (takeMarksOfGrace())
						marksOfGracePickedUp++;
					
					System.out.println("next");
					
					break;
				}
				
				System.out.println("Retrying " + current.title);
				
				if (ctx.controller.isStopping() || ctx.game.floor() == 0) {
					break;
				}

				Obstacle pastOb = runner.peekPrev();
				
				if (!getObstacleObject(pastOb).inViewport())
					ctx.camera.angle(getRandomAngle(pastOb.yaws));
			}
			
			if (ctx.game.floor() == 0) {
				runner.reset();
				break;
			}
			
			runner.next();
		}
		
		if (runner.atStart())
			laps++;
		
		Condition.sleep(200);
		runner.reset();
	}
}
