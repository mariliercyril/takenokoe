package com.raccoon.takenoko.player;

import com.raccoon.takenoko.game.Color;
import com.raccoon.takenoko.game.Game;
import com.raccoon.takenoko.game.Tile;
import com.raccoon.takenoko.game.objective.Objective;
import com.raccoon.takenoko.Takeyesntko;
import com.raccoon.takenoko.game.objective.panda.TwoBambooChunksPandaObjective;
import com.raccoon.takenoko.tool.Constants;
import com.raccoon.takenoko.tool.ForbiddenActionException;

import java.awt.Point;
import java.util.*;

/**
 * Class representig the player taking part in the game. To be extended by a bot to
 * actually perform a move when it's its turn to play.
 * Will provide all the attributes and methods common to all players.
 */
public abstract class Player {

    private int score;
    private int id;
    private List<Objective> objectives;
    private static int counter = 0;
    private HashMap<Color, Integer> stomach;

    public Player() {
        score = 0;
        counter++;
        id = counter;
        objectives = new ArrayList<>();
        stomach = new HashMap<>();
        stomach.put(Color.GREEN, 0);
        stomach.put(Color.YELLOW, 0);
        stomach.put(Color.PINK, 0);
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void addObjective(Objective objective) {
        this.objectives.add(objective);
    }

    public HashMap<Color, Integer> getStomach() {
        return stomach;
    }

    /**
     * This method will be the one called by the game to give their turn to the players/
     * It will be calling the methods for the players to play their turn.
     * <p>
     * DESIGN PATTERN : TEMPLATE METHOD
     *
     * @param game the game in which the player is playing
     */
    public final void play(Game game) throws ForbiddenActionException {
        // 1st step : ask bot to plan actions
        Action[] plannedActions = planActions(game);

        // check if the actions are compatible (exactly 2 costly actions)
        int validityCheck = 0;
        for (int i = 0; i < plannedActions.length; validityCheck += plannedActions[i++].getCost());
        if (validityCheck != 2) {
            throw new ForbiddenActionException("Player tried to play an incorrect number of actions.");
        }
        Takeyesntko.print("Choosen actions : " + Arrays.toString(plannedActions));

        // step 2 : execute all actions
        for (Action a : plannedActions) {
            execute(a, game);
        }

        // step 3 : count points
        Takeyesntko.print("Player has played. Current score : " + getScore());
    }

    /**
     * BOT CAN'T ACCESS THIS METHOD
     * Used to enforce a honest behavior
     *
     * @param a    action to play
     * @param game current game
     */
    private void execute(Action a, Game game) throws ForbiddenActionException {
        Takeyesntko.print("PLAYING " + a);


        switch (a) {
            case PUT_DOWN_TILE:
                // refactorable : chooseTile can return a tile with the chosen position in it.
                Tile t = this.chooseTile(game);
                Point choice = this.whereToPutDownTile(game, t);
                t.setPosition(choice);
                game.putDownTile(t);
                break;
            case MOVE_GARDENER:
                List<Point> gardenerAccessible = game.getBoard().getAccessiblePositions(game.getGardener().getPosition());
                Point whereToMoveGardener = whereToMoveGardener(gardenerAccessible);
                // check that point is in available points array
                if (!gardenerAccessible.contains(whereToMoveGardener)) {
                    throw new ForbiddenActionException("Player tried to put the gardener in a non accessible position.");
                }
                game.getGardener().move(game.getBoard(), whereToMoveGardener);
                break;
            case VALID_OBJECTIVE:
                Objective objective = this.chooseObjectiveToValidate();
                if (objective != null) {
                    Takeyesntko.print("Player has completed an objective ! 1 point to the player !" + objective);
                    this.objectives.remove(objective);
                    this.score += objective.getScore();
                }
                break;
            case DRAW_OBJECTIVE:
                if(objectives.size() > Constants.MAX_AMOUNT_OF_OBJECTIVES) {    // We check if we are allowed to add an objective
                    throw new ForbiddenActionException("Player tried to draw an objective with a full hand already");
                }
                objectives.add(game.drawObjective());
                break;
            case MOVE_PANDA: // Works the same way as MOVE_GARDENER except it's a panda
                List<Point> pandaAccessible = game.getBoard().getAccessiblePositions(game.getPanda().getPosition());
                Point whereToMovePanda = whereToMovePanda(pandaAccessible);
                if (!pandaAccessible.contains(whereToMovePanda)) {
                    throw new ForbiddenActionException("Player tried to put the panda in a non accessible position.");
                }
                boolean destinationHadBamboo = game.getBoard().get(whereToMovePanda).getBambooSize() > 0; // Checks if there is bamboo on the destination tile
                game.getPanda().move(game.getBoard(), whereToMovePanda);

                if (destinationHadBamboo) {
                    eatBamboo(game.getBoard().get(game.getPanda().getPosition()).getColor()); // The panda eats a piece of bamboo on the tile where it lands
                }
                for (Objective pandaObjective : objectives) {
                    if(pandaObjective instanceof TwoBambooChunksPandaObjective) {
                        pandaObjective.checkIfCompleted(this);
                    }
                }
                break;
            default:
                Takeyesntko.print(a + " UNSUPPORTED");
        }
    }

    protected abstract Action[] planActions(Game game);

    protected abstract Point whereToPutDownTile(Game game, Tile t);

    protected abstract Tile chooseTile(Game game);

    public static void reinitCounter() {
        counter = 0;
    }

    protected abstract Point whereToMoveGardener(List<Point> available);

    protected abstract Point whereToMovePanda(List<Point> available);

    protected void eatBamboo(Color color) {
        if(Objects.nonNull(color)){
            stomach.put(color, stomach.get(color) + 1);
            Takeyesntko.print(String.format("Player has eaten a %s bamboo ! He now has %d %s bamboo(s) in his stomach", color, stomach.get(color), color));
        }
    }

    protected abstract Objective chooseObjectiveToValidate();
}
