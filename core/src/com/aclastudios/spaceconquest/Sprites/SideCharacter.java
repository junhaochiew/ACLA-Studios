package com.aclastudios.spaceconquest.Sprites;

import com.aclastudios.spaceconquest.Screens.PlayScreen;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.aclastudios.spaceconquest.Weapons.FireBall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

//This is the side character class where all the features are implemented
public class SideCharacter extends Sprite{
    public final String[] area = {"Team1Spawn","Team2Spawn"};
    private float xSpeedPercent, ySpeedPercent,lastXPercent, lastYPercent;
    private float x, y;
    private float angle;
    private int enemyID;

    private int actualFireCount;
    private int fireCount;
    private int actualIFCount=0;
    private int IFCount=0;
    private float rateOfFire;
    private float cooldown;
    private Array<FireBall> fireballs;

    //asset-related field
    public World world;
    private PlayScreen screen;
    TiledMap map;
    public Body b2body;
    protected Fixture fixture;
    private TextureRegion character;

    //side character info
    float spawnX;
    float spawnY;
    private boolean setToDestroy;
    private boolean destroyed;
    private enum State { STANDING, RUNNING };
    private State currentState;
    private State previousState;
    private Animation running;
    private float stateTimer;
    private float radius = 11/ SpaceConquest.PPM;
    private float characterSize =  25/ SpaceConquest.PPM;

    public SideCharacter(World world, PlayScreen screen, int ID, String spriteName){
        super(screen.getAtlas().findRegion(spriteName));
        this.screen = screen;
        this.world = world;
        this.enemyID = ID;
        map =screen.getMap();
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), getRegionX() + i * 200, getRegionY(), 200, 200));
        }
        running =new Animation(0.2f, frames);
        defineCharacter();
        character = new TextureRegion(getTexture(), getRegionX() + 200, getRegionY(), 200, 200);
        setBounds(0, 0, characterSize, characterSize);
        setRegion(character);
        fireballs = new Array<FireBall>();

        rateOfFire = screen.getRateOfFire();
        fireCount = 0;
        actualFireCount = 0;
        cooldown =0;

        setToDestroy = false;
        destroyed = false;
        lastXPercent=1;
        lastYPercent=0;
        xSpeedPercent=0;
        ySpeedPercent=0;
        x=0;
        y=0;
        angle=0;
        setOriginCenter();
        defineCharacter();

    }

    //this method is called at the start of the game and after the player died
    public void defineCharacter(){
        BodyDef bdef = new BodyDef();
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().matches(area[enemyID/(screen.getNumOfPlayers()/2)])) {
                Array<RectangleMapObject> mo = layer.getObjects().getByType(RectangleMapObject.class);
                Rectangle rect = mo.get(enemyID%(screen.getNumOfPlayers()/2)).getRectangle();
                spawnX = rect.getX()*SpaceConquest.MAP_SCALE;
                spawnY = rect.getY()*SpaceConquest.MAP_SCALE;
                bdef.position.set(spawnX/ SpaceConquest.PPM, spawnY/ SpaceConquest.PPM); //temp set position

            }
        }
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        //Collision Bit
        fdef.filter.categoryBits = SpaceConquest.CHARACTER_BIT; //what category is this fixture
        fdef.filter.maskBits = SpaceConquest.OBSTACLE_BIT
                | SpaceConquest.IRON_BIT
                |SpaceConquest.STATION_BIT
                |SpaceConquest.MAIN_CHARACTER_BIT
                |SpaceConquest.FRIENDLY_FIREBALL_BIT; //What can the character collide with?

        //Body
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

//        fixture = b2body.createFixture(fdef);
    }

    @Override
    public void draw(Batch batch) {
        if(!this.isDestroyed())
             super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

    //this method is called by playscreen at every frame
    public void update(float dt) {
        cooldown += dt;
        if (setToDestroy ) {
            System.out.println("destroying");
            world.destroyBody(b2body);
            destroyed = true;
            setToDestroy = false;
        }
        if(destroyed){
                destroyed = false;
                defineCharacter();
        }else {
            b2body.setTransform(this.x, this.y,angle);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(dt));
        }
        if((cooldown>rateOfFire)&&((fireCount<actualFireCount)||(IFCount<actualIFCount))){
            fire(IFCount<actualIFCount);
            cooldown=0;
        }
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
    }
    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case RUNNING:
                region = running.getKeyFrame(stateTimer, true);
                break;
            default:
                region = character;
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if (b2body.getLinearVelocity().x > 5 || b2body.getLinearVelocity().x < -5 || b2body.getLinearVelocity().y > 5 || b2body.getLinearVelocity().y < -5) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void dead(){
        setToDestroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    //this method is called whenever the play screen receive a message from the network
    //if there is a mismatch of actual fire count and the fire count send over the other player, this character will call fire
    public void updateEnemy(float x,float y, float angle,float radius,float xPercent,float yPercent,int actualFireCount, float xSpeed,float ySpeed, int IFCount){
        this.x=x;
        this.y=y;
        b2body.setLinearVelocity(xSpeed,ySpeed);
        this.angle = angle;
        this.actualFireCount = actualFireCount;
        this.actualIFCount = IFCount;
        xSpeedPercent = xPercent;
        ySpeedPercent = yPercent;
        if(xPercent!=0||yPercent!=0){
            lastYPercent=yPercent;
            lastXPercent = xPercent;
        }
        if(radius!=this.radius) {
            System.out.println("********old radius = " + this.radius + "new radius = "+ radius +"*************");
            setScale((radius<this.radius)?1:radius/this.radius);
        }
        this.radius = radius;
        Array<Fixture> fix = b2body.getFixtureList();
        Shape shape = fix.get(0).getShape();
        shape.setRadius(radius);
        setRotation(angle);
    }

    //fires a fireball at the current direction
    public float[] fire(boolean imbaOrNot){
        System.out.println("user "+enemyID+" is firing");
        if(imbaOrNot)
            IFCount++;
        else
            fireCount+=1;
        float[] s = {b2body.getPosition().x,b2body.getPosition().y};
        FireBall f = new FireBall(screen, s[0], s[1], lastXPercent, lastYPercent,radius,true,enemyID,imbaOrNot);
        fireballs.add(f);
        return s;
    }
}
