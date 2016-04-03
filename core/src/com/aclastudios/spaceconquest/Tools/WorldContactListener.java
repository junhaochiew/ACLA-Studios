package com.aclastudios.spaceconquest.Tools;

import com.aclastudios.spaceconquest.Scenes.Hud;
import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Sprites.MainCharacter;
import com.aclastudios.spaceconquest.Sprites.Resource.GunPowder;
import com.aclastudios.spaceconquest.Sprites.Resource.Iron;
import com.aclastudios.spaceconquest.Sprites.Resource.Oil;
import com.aclastudios.spaceconquest.Weapons.FireBall;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    private PlayScreen screen;
    private SpaceConquest game;
    public WorldContactListener(PlayScreen screen,SpaceConquest game) {
        this.screen=screen;
        this.game=game;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        int[] forHud;
        switch (cDef){
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.IRON_BIT:
                screen.increaseCharWeight(1);
                if(fixA.getFilterData().categoryBits == SpaceConquest.IRON_BIT) {
                    ((Iron) fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addIron_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                }
                else {
                    ((Iron) fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addIron_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                }
                //Hud.addScore(1);
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.GUNPOWDER_BIT:
                screen.increaseCharWeight(1);
                if(fixA.getFilterData().categoryBits == SpaceConquest.GUNPOWDER_BIT){
                    ((GunPowder)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addGun_powder_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                }
                else{
                    ((GunPowder)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addGun_powder_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                //Hud.addScore(1);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT | SpaceConquest.OIL_BIT:
                screen.increaseCharWeight(1);
                if(fixA.getFilterData().categoryBits == SpaceConquest.OIL_BIT){
                    ((Oil)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
                    ((MainCharacter) fixB.getUserData()).addOil_count();
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                }
                else{
                    ((Oil)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
                    ((MainCharacter) fixA.getUserData()).addOil_count();
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                //Hud.addScore(1);
                break;
            case SpaceConquest.MAIN_CHARACTER_BIT |SpaceConquest.STATION_BIT:
                int score =screen.depositResource();
                float[] gadget;
                if(fixA.getFilterData().categoryBits == SpaceConquest.MAIN_CHARACTER_BIT){
                    forHud = ((MainCharacter) fixA.getUserData()).getKnapsackInfo();
                    gadget = ((MainCharacter) fixA.getUserData()).getGadgetInfo();
                }else{
                    forHud = ((MainCharacter) fixB.getUserData()).getKnapsackInfo();
                    gadget = ((MainCharacter) fixB.getUserData()).getGadgetInfo();
                }
                Hud.updateknapscore(forHud[0],forHud[1],forHud[2],forHud[3]);
                Hud.updateGadget((int)gadget[0],gadget[1]);
                //uncomment this
//                if (game.multiplayerSessionInfo.mId_num!=0) {
//                    game.playServices.MessagetoServer("Serverpoints:" + game.multiplayerSessionInfo.mId_num + ":" + score);
//                } else {
                  //  screen.addscore("0",score);
//                }
                break;
//            case SpaceConquest.OBJECT_BIT| SpaceConquest.IRON_BIT:
//                if(fixA.getFilterData().categoryBits == SpaceConquest.IRON_BIT)
//                    ((Iron)fixA.getUserData()).use((MainCharacter) fixB.getUserData());
//                else
//                    ((Iron)fixB.getUserData()).use((MainCharacter) fixA.getUserData());
//                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.OBSTACLE_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.CHARACTER_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                }
                else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                }
                break;
            case SpaceConquest.FIREBALL_BIT | SpaceConquest.MAIN_CHARACTER_BIT:
                if(fixA.getFilterData().categoryBits == SpaceConquest.FIREBALL_BIT) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                    ((MainCharacter) fixB.getUserData()).reduceHP();
                }
                else {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                    ((MainCharacter) fixA.getUserData()).reduceHP();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
