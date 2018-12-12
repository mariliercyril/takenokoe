package com.raccoon.takenoko.tool;

public final class Constants {

	public static final int TIMEOUT = 200;

	private Constants() {}  // Prevent this class to be constructed

	private static final PropertiesFileReader PF_READER = PropertiesFileReader.getInstance();

	private static final String FILE_NAME = "takeyesntko";

	public static final int MAX_AMOUNT_OF_OBJECTIVES = 1;
	public static final int NUMBER_OF_TILES_TO_DRAW = PF_READER.getIntProperty(FILE_NAME, "number.of.tiles.to.draw", 3);

	public static final int MAX_BAMBOO_SIZE = PF_READER.getIntProperty(FILE_NAME, "max.bamboo.size", 4);
	public static final int USUAL_BAMBOO_GROWTH = PF_READER.getIntProperty(FILE_NAME, "usual.bamboo.growth", 1);

	public static final float NUMBER_OF_GAMES_FOR_STATS = PF_READER.getFloatProperty(FILE_NAME, "number.of.games.for.stats", 1000);

	public static final int MAX_SCORE = 64;

	public static final int NUMBER_OF_2_GREEN_PANDA_OBJECTIVE = 5;
    public static final int NUMBER_OF_2_PINK_PANDA_OBJECTIVE = 3;
    public static final int NUMBER_OF_2_YELLOW_PANDA_OBJECTIVE = 4;
    public static final int NUMBER_OF_3_CHUNKS_PANDA_OBJECTIVE = 3;


}
