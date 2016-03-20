package app.onedayofwar.System;

import android.opengl.GLSurfaceView;
import android.util.Log;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.TextFont;

/**
 * Created by Slava on 29.03.2015.
 */
public class GLView extends GLSurfaceView
{
    private MainActivity activity;
    private GLRenderer renderer;
    private int screenWidth;
    private int screenHeight;

    public GLView(MainActivity activity, int width, int height)
    {
        super(activity);
        this.activity = activity;
        this.screenWidth = width;
        this.screenHeight = height;
        setEGLContextClientVersion(2);
        renderer = new GLRenderer(this);
        setRenderer(renderer);
        setOnTouchListener(renderer);
    }

    public void LoadAssets(Graphics graphics)
    {
        Assets.arialFont = new TextFont(graphics.LoadTexture("fonts/arial.png"), "fonts/arial.xml", new XMLParser(activity.getAssets()));
        Assets.space = graphics.LoadTexture("campaign/space/space.jpg");
        Assets.planet = graphics.LoadTexture("campaign/space/planet.png");
        Assets.player = graphics.LoadTexture("campaign/space/player.png");
        Assets.btnFinishInstallation = graphics.LoadTexture("button/installation_finish.png");
        Assets.btnInstall = graphics.LoadTexture("button/install.png");
        Assets.btnRegion = graphics.LoadTexture("shipmenu.png");

        Assets.btnStartGame = graphics.LoadTexture("button/startGameBtn.png");
        Assets.btnSingleGame = graphics.LoadTexture("button/singleGameBtn.png");
        Assets.btnBluetoothGame = graphics.LoadTexture("button/bluetoothBtn.png");
        Assets.btnCampaing = graphics.LoadTexture("button/campaignBtn.png");
        Assets.btnQuickBattle = graphics.LoadTexture("button/quickBattleBtn.png");
        Assets.btnBack = graphics.LoadTexture("button/backBtn.png");
        Assets.btnNewGame = graphics.LoadTexture("button/newGame.png");
        Assets.btnLoadGame = graphics.LoadTexture("button/loadGame.png");

        Assets.robotIcon = graphics.LoadTexture("unit/icon/robot_icon.png");
        Assets.robotImage = graphics.LoadTexture("unit/image/robot.png");
        Assets.robotStroke = graphics.LoadTexture("unit/stroke/robot_stroke.png");
        Assets.ifvImage = graphics.LoadTexture("unit/image/ifv.png");
        Assets.ifvIcon = graphics.LoadTexture("unit/icon/ifv_icon.png");
        Assets.ifvStroke = graphics.LoadTexture("unit/stroke/ifv_stroke.png");
        Assets.engineerImage = graphics.LoadTexture("unit/image/engineer.png");
        Assets.engineerIcon = graphics.LoadTexture("unit/icon/rocket_icon.png");
        Assets.engineerStroke = graphics.LoadTexture("unit/stroke/engineer_stroke.png");
        Assets.tankImage = graphics.LoadTexture("unit/image/tank.png");
        Assets.tankIcon = graphics.LoadTexture("unit/icon/tank_icon.png");
        Assets.tankStroke = graphics.LoadTexture("unit/stroke/tank_stroke.png");
        Assets.turretImage = graphics.LoadTexture("unit/image/turret.png");
        Assets.turretIcon = graphics.LoadTexture("unit/icon/turret_icon.png");
        Assets.turretStroke = graphics.LoadTexture("unit/stroke/turret_stroke.png");
        Assets.sonderImage = graphics.LoadTexture("unit/image/sonder.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.akiraImage = graphics.LoadTexture("unit/image/akira.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.battleshipImage = graphics.LoadTexture("unit/image/battleship.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.bioshipImage = graphics.LoadTexture("unit/image/bioship.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.birdOfPreyImage = graphics.LoadTexture("unit/image/birdofprey.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.defaintImage = graphics.LoadTexture("unit/image/defaint.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");
        Assets.r2d2Image =  graphics.LoadTexture("unit/image/r2d2.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/sonder_icon.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");


        //Assets.grid = graphics.LoadTexture("field/grid/normal_green_5x5.png");
        //Assets.gridIso = graphics.LoadTexture("field/grid/iso_5x5.png");
        //Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        //Assets.isoGridCoeff = (int)((screenHeight * (1 - 2 * 0.4f)) / 30) * 30 / (double)Assets.gridIso.getHeight();

        Assets.grid = graphics.LoadTexture("field/grid/normal_green.png");
        Assets.gridIso = graphics.LoadTexture("field/grid/iso.png");
        Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.15f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        Assets.isoGridCoeff = Assets.gridCoeff;

        Assets.signFire = graphics.LoadTexture("field/mark/fire.png");
        Assets.signMiss = graphics.LoadTexture("field/mark/miss_green.png");
        Assets.signMissIso = graphics.LoadTexture("field/mark/miss_iso.png");
        Assets.signHit = graphics.LoadTexture("field/mark/hit_green.png");
        Assets.signFlag = graphics.LoadTexture("field/mark/flag.png");
        Assets.signError = graphics.LoadTexture("field/mark/error.png");
        Assets.signGlare = graphics.LoadTexture("field/mark/glare_green.png");

        Assets.btnCancel = graphics.LoadTexture("button/cancel.png");
        Assets.btnInstall = graphics.LoadTexture("button/install.png");
        Assets.btnFinishInstallation = graphics.LoadTexture("button/installation_finish.png");
        Assets.btnShoot = graphics.LoadTexture("button/shoot.png");
        Assets.btnTurn = graphics.LoadTexture("button/turn.png");
        Assets.btnPanelClose = graphics.LoadTexture("button/panel_close.png");
        Assets.btnFlag = graphics.LoadTexture("button/flag.png");

        Assets.groundBackground = graphics.LoadTexture("desert.jpg");
        Assets.spaceBackground = graphics.LoadTexture("spacebackground.jpg");
        Assets.monitor = graphics.LoadTexture("monitor.png");

        Assets.bullet = graphics.LoadTexture("unit/bullet/bullet.png");
        Assets.miniRocket = graphics.LoadTexture("unit/bullet/miniRocket.png");

        Assets.explosion = graphics.LoadTexture("animation/land_explosion.png");
        Assets.airExplosion = graphics.LoadTexture("animation/air_explosion.png");
        Assets.fire = graphics.LoadTexture("animation/fire2.png");

        Assets.spaceCoeff = 1.0d * screenHeight / Assets.space.getHeight();
        Assets.btnCoeff = screenHeight * 0.17f / Assets.btnFinishInstallation.getHeight();
        Assets.monitorHeightCoeff = (double)screenHeight / 1080;
        Assets.monitorWidthCoeff = (double)screenWidth / 1920;
        Assets.iconCoeff = ((screenHeight - 70) / 6d) / Assets.sonderIcon.getHeight();
        Assets.bgHeightCoeff = screenHeight *1f/ Assets.groundBackground.getHeight();
        Assets.bgWidthCoeff = screenWidth *1f/ Assets.groundBackground.getWidth();

        renderer.changeScreen(new MainView(this));
    }

    public void gotoMainMenu()
    {
        renderer.GoMenu();
    }

    public void changeScreen(ScreenView screen)
    {
        renderer.changeScreen(screen);
    }

    public void goBack()
    {
        renderer.GoBack();
    }

    public MainActivity getActivity()
    {
        return activity;
    }

    public void StartCampaign(DBController dbController, boolean isNewGame)
    {
        changeScreen(new GameView(this, dbController, isNewGame));
        activity.gameState = MainActivity.GameState.CAMPAIGN;
    }

    public void StartBattle(Planet planet, char type, boolean isYourTurn)
    {

        BattleView battleView = new BattleView(this, planet, type, isYourTurn);
        if(type == 'b')
        {
            battleView.btController = activity.getBtController();
        }
        Log.i("BT", "Start battle");
        changeScreen(battleView);
        activity.gameState = MainActivity.GameState.BATTLE;
    }

    public void moveCamera(float dx, float dy)
    {
        renderer.moveCamera(dx, dy);
    }

    public float getCameraX()
    {
        return renderer.getCameraX();
    }

    public float getCameraY()
    {
        return renderer.getCameraY();
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }
}
