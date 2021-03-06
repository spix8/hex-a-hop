package com.github.jamescarter.hexahop.core.test.grid;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import playn.core.PlayN;
import playn.core.json.JsonParserException;
import playn.java.JavaPlatform;

import com.github.jamescarter.hexahop.core.level.Location;
import com.github.jamescarter.hexahop.core.player.Direction;
import com.github.jamescarter.hexahop.core.screen.LevelScreen;
import com.github.jamescarter.hexahop.core.tile.Collapsable2Tile;
import com.github.jamescarter.hexahop.core.tile.Tile;

public class LevelPlayTest {
	static {
		JavaPlatform.register();
	}

	private LevelScreen level2;
	private LevelScreen level3;
	private LevelScreen level4;
	private LevelScreen level5;
	private LevelScreen level6;

	@Before
	public void setUp() throws JsonParserException, Exception {
		level2 = new LevelScreen(new Location(0, 0), PlayN.assets().getTextSync("test/levels/test-002.json"));
		level3 = new LevelScreen(new Location(0, 0), PlayN.assets().getTextSync("test/levels/test-003.json"));
		level4 = new LevelScreen(new Location(0, 0), PlayN.assets().getTextSync("test/levels/test-004.json"));
		level5 = new LevelScreen(new Location(0, 0), PlayN.assets().getTextSync("test/levels/test-005.json"));
		level6 = new LevelScreen(new Location(0, 0), PlayN.assets().getTextSync("test/levels/test-006.json"));
	}

	@Test
	public void testCollapsable2Undo() {
		Tile statusTile = level2.getTileGrid().statusTileAt(new Location(4, 5));

		assertEquals(new Location(0, 5), level2.player());

		// step onto all the collapsable2 tiles once
		level2.move(Direction.SOUTH_EAST);
		level2.move(Direction.NORTH_EAST);
		level2.move(Direction.NORTH_EAST);
		level2.move(Direction.SOUTH_EAST);
		level2.move(Direction.SOUTH);

		// step back onto the previous collapsable2 tile
		level2.move(Direction.NORTH);

		assertEquals(new Location(4, 5), level2.player());

		assertFalse(statusTile.isWall());

		assertTrue(level2.undo());

		assertEquals(new Location(4, 6), level2.player());

		// validate that the tile didn't turn into a wall
		assertFalse(statusTile.isWall());
	}

	@Test
	public void testCollapsable2Undo2() {
		Tile[] tileList = {
			level3.getTileGrid().statusTileAt(new Location(2, 2)),
			level3.getTileGrid().statusTileAt(new Location(2, 3)),
			level3.getTileGrid().statusTileAt(new Location(3, 1)),
			level3.getTileGrid().statusTileAt(new Location(3, 3)),
			level3.getTileGrid().statusTileAt(new Location(4, 2)),
			level3.getTileGrid().statusTileAt(new Location(4, 3))
		};

		level3.move(Direction.NORTH_EAST);

		for (Tile tile : tileList) {
			assertTrue(tile.isWall());
		}

		level3.move(Direction.SOUTH_WEST);

		for (Tile tile : tileList) {
			assertFalse(tile.isWall());
		}

		// undo the last move that converted the walls to normal tiles
		level3.undo();
		level3.finishAnimation();

		for (Tile tile : tileList) {
			assertTrue(tile.isWall());
		}

		// undo the first move and confirm the gird has the original state when it was loaded
		level3.undo();
		level3.finishAnimation();

		for (Tile tile : tileList) {
			assertTrue(tile.isWall());
		}
	}

	@Test
	public void testCollapsable2Undo3() {
		Collapsable2Tile tile2 = (Collapsable2Tile) level2.getTileGrid().statusTileAt(new Location(1, 5));

		level2.move(Direction.SOUTH_EAST);

		assertFalse(tile2.isBreakable());

		level2.move(Direction.NORTH_WEST);

		assertTrue(tile2.isBreakable());

		level2.move(Direction.SOUTH_EAST);

		assertTrue(tile2.isBreakable());

		level2.undo();

		assertTrue(tile2.isBreakable());

		level2.move(Direction.SOUTH_EAST);

		assertTrue(tile2.isBreakable());

		level2.undo();
		level2.undo();

		level2.finishAnimation();

		assertFalse(tile2.isBreakable());
	}

	@Test
	public void testGunTile() {
		level4.move(Direction.NORTH_EAST);
		level4.move(Direction.NORTH_EAST);

		level4.finishAnimation();

		assertFalse(level4.getTileGrid().statusTileAt(new Location(3, 1)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(3, 0)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(3, 2)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(4, 1)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(4, 2)).isActive());
	}

	@Test
	public void testGunTileUndoExplodedStandingOn() {
		level4.move(Direction.NORTH_EAST);
		level4.move(Direction.NORTH_EAST);
		level4.move(Direction.SOUTH_EAST);

		level4.finishAnimation();

		assertFalse(level4.getTileGrid().statusTileAt(new Location(3, 2)).isActive());
		assertFalse(level4.getTileGrid().statusTileAt(new Location(4, 2)).isActive());
		assertFalse(level4.getTileGrid().statusTileAt(new Location(4, 3)).isActive());

		assertTrue(level4.undo());

		assertTrue(level4.getTileGrid().statusTileAt(new Location(3, 2)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(4, 2)).isActive());
		assertTrue(level4.getTileGrid().statusTileAt(new Location(4, 3)).isActive());
	}

	@Test
	public void testGunTileUndoAfterSteppingOff() {
		level4.move(Direction.NORTH_EAST);
		level4.move(Direction.NORTH_EAST);
		level4.move(Direction.NORTH);

		Tile destroyedTile = level4.getTileGrid().statusTileAt(new Location(3, 1));
		Tile[] gunTiles = {
			level4.getTileGrid().statusTileAt(new Location(2, 1)),
			level4.getTileGrid().statusTileAt(new Location(2, 2)),
			level4.getTileGrid().statusTileAt(new Location(3, 0)),
			level4.getTileGrid().statusTileAt(new Location(3, 2)),
			level4.getTileGrid().statusTileAt(new Location(4, 1)),
			level4.getTileGrid().statusTileAt(new Location(4, 2)),
			level4.getTileGrid().statusTileAt(new Location(4, 3))
		};

		assertFalse(destroyedTile.isActive());

		for (Tile tile : gunTiles) {
			assertTrue(tile.isActive());
		}

		level4.undo();
		level4.finishAnimation();

		for (Tile tile : gunTiles) {
			assertTrue(tile.isActive());
		}

		level4.undo();
		level4.finishAnimation();

		assertTrue(destroyedTile.isActive());

		for (Tile tile : gunTiles) {
			assertTrue(tile.isActive());
		}
	}

	@Test
	public void testTrampoline() {
		level5.move(Direction.SOUTH_EAST);
		level5.move(Direction.SOUTH_EAST);

		level5.finishAnimation();

		assertEquals(new Location(4, 2), level5.player());
	}

	@Test
	public void testUndoAfterDrown() {
		testTrampoline();

		level5.move(Direction.NORTH_WEST);
		level5.move(Direction.NORTH_WEST);

		level5.finishAnimation();

		Tile statusTile = level5.getTileGrid().statusTileAt(new Location(0, 0));

		assertEquals(statusTile.location(), level5.player());

		assertFalse(statusTile.isActive());

		level5.undo();

		assertFalse(statusTile.isActive());
	}

	@Test
	public void testUndoAfterWallToggleFromCollapsable2() {
		Tile statusTile = level6.getTileGrid().statusTileAt(new Location(1, 0));

		level6.move(Direction.SOUTH_WEST);
		level6.move(Direction.NORTH_EAST);

		assertTrue(statusTile.isWall());

		level6.move(Direction.NORTH);

		assertFalse(statusTile.isWall());

		level6.undo();
		level6.finishAnimation();

		assertTrue(statusTile.isWall());
	}
}
