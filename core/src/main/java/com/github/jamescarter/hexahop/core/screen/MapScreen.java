package com.github.jamescarter.hexahop.core.screen;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.Color;
import playn.core.ImageLayer;
import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.Surface;
import playn.core.SurfaceLayer;

import com.github.jamescarter.hexahop.core.callback.LevelLoadCallback;
import com.github.jamescarter.hexahop.core.grid.GridLoader;
import com.github.jamescarter.hexahop.core.grid.MapTileGrid;
import com.github.jamescarter.hexahop.core.grid.TileGrid;
import com.github.jamescarter.hexahop.core.json.GridStateJson;
import com.github.jamescarter.hexahop.core.level.Location;
import com.github.jamescarter.hexahop.core.tile.MapStatusTile;
import com.github.jamescarter.hexahop.core.tile.Tile;

public class MapScreen extends GridLoader {
	private final ImageLayer bgLayer = graphics().createImageLayer(assets().getImage("images/map.png"));
	private final MapTileGrid mapTileGrid = new MapTileGrid();

	public MapScreen(Location completedLevelLocation, boolean completedLevelPar, String mapJsonString) {
		GridStateJson<Integer> mapJson = new GridStateJson<Integer>(
			Integer.class,
			mapTileGrid,
			anim,
			PlayN.json().parse(
				mapJsonString
			),
			GridStateJson.STORAGE_KEY_MAP
		);

		if (!mapJson.hasStatus()) {
			Location start = mapJson.start();

			mapTileGrid.setStatusTileAt(start, new MapStatusTile(start));
		}

		if (completedLevelLocation != null) {
			mapTileGrid.unlockConnected(completedLevelLocation, completedLevelPar);

			GridStateJson.store(mapTileGrid, GridStateJson.STORAGE_KEY_MAP);
		}
	}

	@Override
	public void wasAdded() {
		getGridLayer().setTranslation(
			170,
			30
		);

		SurfaceLayer lineLayer = graphics().createSurfaceLayer(1000, 480);
		Surface surface = lineLayer.surface();
		surface.setFillColor(Color.rgb(39, 23, 107));

		boolean addLineLayer = false;

		// draw lines between connections
		for (int row=0; row<getTileGrid().rows(); row++) {
			List<Tile> tileList = getTileGrid().rowTileList(row);

			for (int col=0; col<tileList.size(); col++) {
				if (tileList.get(col) != null) {
					for (Location toLocation : mapTileGrid.statusConnectedTo(new Location(col, row))) {
						addLineLayer = true;

						surface.drawLine(
							getColPosition(col, 32),
							getRowPosition(row, col, 38),
							getColPosition(toLocation.col(), 32),
							getRowPosition(toLocation.row(), toLocation.col(), 38),
							2
						);
					}
				}
			}
		}

		if (addLineLayer) {
			add(lineLayer);
		}

		super.wasAdded();

		bgLayer.addListener(new Pointer.Adapter() {
			@Override
			public void onPointerStart(Pointer.Event event) {
				Location location = getGridLocation(event.x(), event.y());

				// Make sure the level is activated before allowing the user to load it
				if (mapTileGrid.statusTileAt(location) != null) {
					assets().getText("levels/" + getLevelName(mapTileGrid.baseAt(location)) + ".json", new LevelLoadCallback(location));
				}
			}
		});
	}

	@Override
	public void wasShown() {
		PlayN.keyboard().setListener(new Keyboard.Adapter() {
			@Override
			public void onKeyDown(Event event) {
				switch(event.key()) {
					case ESCAPE:
					case BACK:
						screens.replace(new TitleScreen());
					break;
					default:
				}
			}
		});
	}

	private String getLevelName(int levelId) {
		StringBuilder sb = new StringBuilder();

		if (levelId < 10) {
			sb.append("00");
		} else if (levelId < 100) {
			sb.append("0");
		}

		sb.append(levelId);

		return sb.toString();
	}

	@Override
	public TileGrid<Integer> getTileGrid() {
		return mapTileGrid;
	}

	@Override
	public Layer getBackgroundLayer() {
		return bgLayer;
	}
}
