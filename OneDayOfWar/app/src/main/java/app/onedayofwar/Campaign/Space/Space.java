package app.onedayofwar.Campaign.Space;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Campaign.CharacterControl.AI;
import app.onedayofwar.Campaign.CharacterControl.Player;
import app.onedayofwar.Campaign.CharacterControl.TechMSG;
import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Campaign.System.PlanetView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;
import app.onedayofwar.UI.Button;
import app.onedayofwar.UI.Panel;

/**
 * Created by Slava on 24.02.2015.
 */
public class Space
{
    private Vector2 touchPos;
    private Vector2 lastTouch;
    private Vector2 tmp2;
    private Vector2 toMove;
    private Vector2 tmp;
    private Vector2 infoTextPos;

    private XMLParser xmlParser;
    private PlanetController planetController;
    private Activity activity;
    private GameView gameView;

    private Sprite background;
    private Sprite btnRegion;

    private Button moveBtn;
    private Button nextStep;
    private Button startBattleBtn;
    private Panel infoPanel;

    private Player player;
    private AI AI;

    private int width;
    private int height;
    private int color;
    private int selectedPlanet;
    private int pointsToMove;
    private float spaceVelocityCoeff;


    public Space(Activity activity, GameView gameView)
    {
        this.activity = activity;
        this.gameView = gameView;
    }

    public void Initialize(Graphics graphics)            //Тут сам сравни по ошибкам, которые выскочат что нового есть, еще нужно в GameView добавить всякие коэфициенты кнопок и т.д
    {
        background = new Sprite(Assets.space);
        btnRegion = new Sprite(Assets.btnRegion);

        height = 2000;
        width = 2000;

        background.Scale(2 * (float) Assets.bgWidthCoeff, 2 * (float) Assets.bgHeightCoeff);
        background.setPosition(getScreenWidth() / 2 + Assets.btnRegion.getWidth() / 2, getScreenHeight() / 2);
        btnRegion.setPosition(0, Assets.btnRegion.getHeight()/2);

        touchPos = new Vector2();
        lastTouch = new Vector2();

        pointsToMove = 20;

        if(gameView.IsNewGame())
        {
            gameView.getDBController().Delete();
            xmlParser = new XMLParser(activity.getAssets());
            planetController = new PlanetController(this);
            planetController.LoadPlanets(graphics, xmlParser);
            player = new Player(this, pointsToMove);
            AI = new AI(this, pointsToMove, player);
        }
        else
        {
            gameView.getDBController().LoadWorld(gameView.getGlView());
            planetController = new PlanetController(this);
            planetController.LoadPlanets(graphics, gameView.getDBController());
            player = new Player(this, pointsToMove);
            gameView.getDBController().LoadCharacter(true, player);
            AI = new AI(this, pointsToMove, player);
            gameView.getDBController().LoadCharacter(false, AI);
        }

        color = Color.RED;
        toMove = new Vector2();
        infoTextPos = new Vector2();
        toMove.SetFalse();

        moveBtn = new Button(Assets.btnInstall,(int)(Assets.btnInstall.getWidth()*Assets.btnCoeff/2),(int)(Assets.btnInstall.getHeight() * Assets.btnCoeff/2),false);
        moveBtn.Scale(Assets.btnCoeff);

        infoPanel = new Panel(width/2, height/8, width, height/4, Panel.Type.UP);

        nextStep = new Button(Assets.btnTurn,(int)(Assets.btnTurn.getWidth()*Assets.btnCoeff/2), (int)(250 + Assets.btnTurn.getHeight()*Assets.btnCoeff/2), false);
        nextStep.Scale(Assets.btnCoeff);

        startBattleBtn = new Button(Assets.btnShoot, (int)(getScreenWidth() - Assets.btnShoot.getWidth()*Assets.btnCoeff/2), 0, false);
        startBattleBtn.Scale(Assets.btnCoeff);

        TechMSG.isAttack = false;
        TechMSG.playerMove = true;
        TechMSG.selectedPlanet = -1;
        TechMSG.infoAttack = "На вас напали, лететь на автопилоте?";
        TechMSG.isAILand = false;
        TechMSG.isPlayerLand = false;
        TechMSG.isFirstPlanetConquered = false;
        TechMSG.isReadyForUpdate = false;
        TechMSG.isFinishedWay = true;
        TechMSG.isNeedMove = true;
        TechMSG.isAttacked = false;
        TechMSG.attackedPlanet = -1;

        spaceVelocityCoeff = Assets.space.getHeight()*1.0f/(width*2);

        tmp = new Vector2();
        tmp2 = new Vector2();
        infoTextPos.SetValue(infoPanel.matrix[12], infoPanel.matrix[13]);
        startBattleBtn.getMatrix()[13] = infoPanel.matrix[13];
        infoPanel.Move();
    }

    public void GotoPlanet()
    {
        gameView.getGlView().changeScreen(new PlanetView(gameView.getGlView(), this, planetController.getPlanet(selectedPlanet)));
    }

    public Player getPlayer()
    {
        return player;
    }

    public AI getAI(){return AI;}

    public void onTouch(MotionEvent event)
    {
        lastTouch.SetValue((int) (event.getX() - touchPos.x) * 1.5f, (int) (event.getY() - touchPos.y) * 1.5f);
        touchPos.SetValue((int) event.getX(), (int) event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                tmp2.SetValue(tmp.x - gameView.getCameraX(), tmp.y - gameView.getCameraY());
                planetController.SelectPlanet(tmp2);
                if (planetController.isPlanetSelected())
                {
                    toMove.SetValue(planetController.getSelectedPlanet().getMatrix()[12], planetController.getSelectedPlanet().getMatrix()[13]);
                    selectedPlanet = planetController.getSelectedPlanetNum();
                    for(int i = 0; i < AI.getConqueredPlanets().size(); i++)
                    {
                        if(selectedPlanet == AI.getConqueredPlanets().get(i))
                        {
                            TechMSG.attackedPlanet = selectedPlanet;
                            TechMSG.isAttacked = true;
                            AI.calculateLevel();
                            break;
                        }
                    }
                    planetController.doSelectedPlanetFalse();
                    gameView.currentCamera.SetValue(GetCameraX(), GetCameraY());
                }
                else
                    toMove.SetFalse();

                ButtonsUpdate();

                CheckButtons();
                break;

            case MotionEvent.ACTION_MOVE:
                if(touchPos.x > Assets.btnRegion.getWidth()/2)
                    gameView.MoveCamera(lastTouch);
                break;
            case MotionEvent.ACTION_UP:
                ResetButtons();
                break;
        }
        tmp.SetValue(touchPos);
    }


    public void Update(float eTime)
    {
        planetController.UpdatePlanets();
        player.Update(eTime);
        AI.Update(eTime);
        infoTextPos.SetValue(infoPanel.matrix[12], infoPanel.matrix[13]);
        infoPanel.Update(eTime);
        startBattleBtn.getMatrix()[13] = infoPanel.matrix[13];
    }



    public void Draw(Graphics graphics)
    {
        graphics.DrawParallaxSprite(background, spaceVelocityCoeff);
        planetController.DrawPlanets(graphics);
        player.Draw(graphics);
        AI.Draw(graphics);
        graphics.DrawStaticSprite(btnRegion);
        moveBtn.Draw(graphics);
        nextStep.Draw(graphics);
        AI.infoPlanetsDraw(graphics);
        if (infoPanel.isClose || !infoPanel.isStop)
            infoPanel.Draw(graphics);
        graphics.DrawText(TechMSG.infoAttack, Assets.arialFont, 0,infoTextPos.y, getScreenWidth() - startBattleBtn.width, Color.WHITE, 24);
        startBattleBtn.Draw(graphics);

        //graphics.DrawText("AI....", Assets.arialFont, gameView.getGlView().getScreenWidth()/2, gameView.getGlView().getScreenHeight()/2, 0, Color.RED, 100);
    }

    public void Attacked()
    {

    }



    public void NextTurn()
    {
        AI.InverseMyStep();
        player.setPointsToMove(pointsToMove);
        getPlanetController().NextTurn();
        Save();
        TechMSG.playerMove = true;
        PrepareToPlayerStep();
    }

    public void startBattle()
    {
        infoPanel.Move();
    }

    public void CheckButtons()
    {
        if(moveBtn.IsClicked())
        {
            Log.i("READY", ""+TechMSG.isFinishedWay);
            if(TechMSG.isFinishedWay)
            {
                if(tmp.x > Assets.btnRegion.getWidth()/2)
                {
                    player.followToTap(tmp, toMove, (int)gameView.getGlView().getCameraX(), (int)gameView.getGlView().getCameraY());
                    Log.i("TMP", "x " + tmp.x + " y " + tmp.y);
                    Log.i("TOMOVE", ""+toMove.IsFalse());
                    TechMSG.isNeedMove = true;
                    TechMSG.isFinishedWay = false;
                }
            }
            else
            {
                TechMSG.isNeedMove = true;
            }
        }

        else if(nextStep.IsClicked())
        {
            if(TechMSG.playerMove && AI.getPointsToMove() == 0)
            {
                AI.setPointsToMove(pointsToMove);
                TechMSG.playerMove = false;
                TechMSG.isNeedMove = false;
                PrepareToAIStep();
                player.InverseMyStep();
            }
        }

        else if(startBattleBtn.IsClicked())
        {
            selectedPlanet = TechMSG.selectedPlanet;
            GotoPlanet();
        }
    }

    public void ButtonsUpdate()
    {
        moveBtn.Update(touchPos);
        startBattleBtn.Update(touchPos);
        nextStep.Update(touchPos);
    }


    public void ResetButtons()
    {
        moveBtn.Reset();
        startBattleBtn.Reset();
        nextStep.Reset();
    }

    public void PrepareToAIStep()
    {
        moveBtn.Lock();
        nextStep.Lock();
    }

    public void PrepareToPlayerStep()
    {
        moveBtn.Unlock();
        nextStep.Unlock();
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getScreenHeight()
    {
        return gameView.getGlView().getScreenHeight();
    }

    public int getScreenWidth()
    {
        return gameView.getGlView().getScreenWidth();
    }

    public float GetCameraX(){return gameView.getCameraX();}

    public float GetCameraY(){return gameView.getCameraY();}

    public PlanetController getPlanetController(){return planetController;}

    public void Save()
    {
        gameView.getDBController().SaveWorld(this);
        gameView.getDBController().SavePlanets(planetController.getPlanets());
        gameView.getDBController().SaveCharacter(true, player);
        gameView.getDBController().SaveCharacter(false, AI);
    }

}
