package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;



public class MainCharacter extends Sprite {
    private float xSpeed,ySpeed;
    public World world;
    public Body b2body;
    protected Fixture fixture;
    private TextureRegion character;
    private int charWeight;
    private int charScore;
    public MainCharacter(World world,PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
        defineCharacter();
        character = new TextureRegion(getTexture(),0,0,16,16);
        setBounds(0,0,16,16);
        setRegion(character);
    }

    public void defineCharacter(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(150,150); //temp set position
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8);
        xSpeed = 0;
        ySpeed = 0;
        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                | SpaceConquest.IRON_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.OBJECT_BIT; //What can the character collide with?

        //Body
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
//        fixture = b2body.createFixture(fdef);
    }
    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
        System.out.println("My weight is " + charWeight);
    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public float getySpeed() {
        return ySpeed;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }


    public Integer getCharWeight() {
        return charWeight;
    }

    public int getCharScore() {
        return charScore;
    }

    public void addCharWeight(int charWeight) {
        this.charWeight += charWeight;
    }

    public void setCharWeight(int w){
        this.charWeight=w;
    }
}
