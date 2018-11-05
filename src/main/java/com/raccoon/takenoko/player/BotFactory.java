package com.raccoon.takenoko.player;

import org.springframework.beans.factory.FactoryBean;

public class BotFactory implements FactoryBean<Player> {

    private static int counter = 0;

    /*
    Instead of re initialize a counter we could think of a Player constructor asking an Id as a parameter.
    The factory would be in charge of giving them their number.
     */

    @Override
    public Player getObject() {
        if (counter++ % 2 == 0) {
            return new RandomBot();
        }
        return new BamBot();
    }

    @Override
    public Class<?> getObjectType() {
        return Player.class;
    }


}