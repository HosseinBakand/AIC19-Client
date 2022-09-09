package client;

import client.model.*;
import sun.misc.Queue;

import java.time.chrono.HijrahEra;
import java.util.Random;

public class AI
{
    int distance[][]=new int[31][31];
    Queue<Cell> q=new Queue<>();
    private Random random = new Random();
    boolean[] firstPlan=new boolean[4];
//    Cell[] next=new Cell[4];
    int maxJump=Integer.MIN_VALUE;

    public void preProcess(World world)
    {
        System.out.println("pre process started");
        for(int i=0;i<31;i++){
            for(int j=0;j<31;j++){
                distance[i][j]=Integer.MAX_VALUE;
            }
        }
        Cell[] stars=world.getMap().getMyRespawnZone();
        Cell cell[]=world.getMap().getObjectiveZone();
        Cell nearest1,nearest2,nearest3,nearest4;
        int min1=Integer.MAX_VALUE,min2=Integer.MAX_VALUE,min3=Integer.MAX_VALUE,min4=Integer.MAX_VALUE;

        for(int i=0;i<cell.length;i++){
            q.enqueue(cell[i]);
            distance[cell[i].getRow()][cell[i].getColumn()]=0;
            if(world.manhattanDistance(cell[i],stars[0])<min1){
                nearest1=cell[i];
                min1=world.manhattanDistance(cell[i],stars[0]);
            }
            if(world.manhattanDistance(cell[i],stars[1])<min2){
                nearest2=cell[i];
                min2=world.manhattanDistance(cell[i],stars[1]);
            }
            if(world.manhattanDistance(cell[i],stars[2])<min3){
                nearest3=cell[i];
                min3=world.manhattanDistance(cell[i],stars[2]);
            }
            if(world.manhattanDistance(cell[i],stars[3])<min4){
                nearest4=cell[i];
                min4=world.manhattanDistance(cell[i],stars[3]);
            }

        }
        try {
            calculateDis(world);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        for(int i=0;i<31;i++){
//            for(int j=0;j<31;j++){
//                System.out.print(distance[i][j]+"\t");
//            }
//            System.out.println();
//        }

        Cell now=cell[0];

        while(distance[now.getRow()][now.getRow()]!=0){
            for(int i= -4 ; i<=4 ; i++){
                for(int j = -4 ; j<=4 ; j++) {
                    if (world.getMap().isInMap(now.getRow() + i, now.getColumn() + j)) {
                        if (world.manhattanDistance(now.getRow(), now.getColumn(), now.getRow() + i, now.getColumn() + j) > 4)
                            continue;

                        int disDiff = distance[now.getRow()][now.getColumn()] - distance[now.getRow() + i][now.getColumn() + j];
                        int manDiff = world.manhattanDistance(now.getRow(), now.getColumn(), now.getRow() + i, now.getColumn() + j);
                        if (disDiff > manDiff && disDiff > maxJump) {
                            maxJump = disDiff;
                        }
                    }
                }
            }






            int x=now.getRow();
            int y=now.getColumn();
            int manNow=distance[x][y];
            int row=0;
            int column=0;
            int min=4;
            if(distance[x][y+1]<manNow){
                now=world.getMap().getCell(x,y+1);
            }
            if(distance[x][y-1]<manNow){
                now=world.getMap().getCell(x,y-1);
            }
            if(distance[x+1][y]<manNow){
                now=world.getMap().getCell(x+1,y);
            }
            if(distance[x-1][y]<manNow){
                now=world.getMap().getCell(x-1,y);
            }
        }




    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");
//        world.pickHero(HeroName.values()[world.getCurrentTurn()]);
        world.pickHero(HeroName.BLASTER);
    }

    public void moveTurn(World world)
    {


        System.out.println("move started "+ world.getCurrentTurn());
        Hero[] heroes = world.getMyHeroes();
//        for(int i=0;i<4;i++){
//            next[i]=heroes[i].getCurrentCell();
//        }

        for (int i=0;i<4;i++) {


            if(heroes[i].getCurrentCell().isInObjectiveZone())firstPlan[i]=true;
            if(heroes[i].getCurrentCell().getColumn()==-1){
                firstPlan[i]=false;
                continue;
            }
//            if(world.manhattanDistance(distance[heroes[i].getCurrentCell().getRow()][heroes[i].getCurrentCell().getColumn()]>2)
            if (!firstPlan[i]) {
                firstPlan(world,heroes[i]);

            }else{
                secondPlan(world,heroes[i],i);
            }
        }
    }

//    public void actionTurn(World world){
//        Hero[] heroes = world.getMyHeroes();
//        Cell [][] cells = new Cell [4][3];
////        Hero[] enemy =world.getOppHeroes();
////        Map map = world.getMap();
//        for(Hero hero : heroes){
//            for(Ability ab : hero.getAbilities()){
//                if(hero.getName() == HeroName.BLASTER && ab.getName() == AbilityName.BLASTER_ATTACK){
//                    cells[0][0] = checkAttackPlace(hero , world , AbilityName.BLASTER_ATTACK);
//                }
//                else if (hero.getName() == HeroName.BLASTER && ab.getName() == AbilityName.BLASTER_BOMB){
//                    cells[0][2] = checkAttackPlace(hero , world , AbilityName.BLASTER_ATTACK);
//                }
//                else if (hero.getName() == HeroName.GUARDIAN && ab.getName() == AbilityName.GUARDIAN_ATTACK ){
//                    cells[1][0] = checkAttackPlace(hero , world , AbilityName.GUARDIAN_ATTACK);
//                }
//                else if(ab.getName() == AbilityName.HEALER_ATTACK && hero.getName() == HeroName.HEALER){
//                    cells[2][0] = checkAttackPlace(hero , world , AbilityName.HEALER_ATTACK);
//                }
//                else if (ab.getName() == AbilityName.SENTRY_ATTACK && hero.getName() == HeroName.SENTRY){
//                    cells[3][0] = checkAttackPlace(hero , world , AbilityName.SENTRY_ATTACK);
//                }
//
//            }
//        }
//    }

    public void actionTurn(World world){
        System.out.println("action started");

        Hero[] heroes=world.getMyHeroes();
        Hero[] enemy = world.getOppHeroes();
        Map map=world.getMap();
        int OppHp[]=new int[4];
        for(int i=0;i<4;i++){
            OppHp[i]=enemy[i].getCurrentHP();
        }
        for(Hero hero:world.getMyHeroes()){
            int maxHere=Integer.MIN_VALUE;
            Cell targetJump=null;
            for(int i= -4 ; i<=4 ; i++){
                for(int j = -4 ; j<=4 ; j++) {
                    if (world.getMap().isInMap(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j)) {
                        int disDiff = distance[hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()] - distance[hero.getCurrentCell().getRow() + i][hero.getCurrentCell().getColumn() + j];
                        if (world.manhattanDistance(hero.getCurrentCell().getRow(), hero.getCurrentCell().getColumn(), hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j) > 4)
                            continue;
                        if (disDiff > maxHere) {
                            targetJump = world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + i);
                            maxHere = disDiff;
                        }
                    }
                }
            }
            if(maxHere>=maxJump-4){
                world.castAbility(hero,AbilityName.BLASTER_DODGE,targetJump);
                continue;
            }
                if (hero.getCurrentCell().getColumn() == -1) continue;
                int row = random.nextInt(map.getRowNum());
                int column = random.nextInt(map.getColumnNum());

                Ability[] abilities = hero.getAbilities();

                int max = Integer.MIN_VALUE;
                Ability best = null;
                Cell target = null;

                for (Ability ab : abilities) {
                    if (ab.getRemCooldown() != 0) continue;
                    if (ab.getName() == AbilityName.values()[0] || ab.getName() == AbilityName.values()[2] || ab.getName() == AbilityName.values()[3] || ab.getName() == AbilityName.values()[5] || ab.getName() == AbilityName.values()[6] || ab.getName() == AbilityName.values()[9]) {
                        //============  hamle ==============
                        int miniMax = 0;
                        Cell bestCell;
                        for (int i = -ab.getRange(); i <= ab.getRange(); i++) {
                            for (int j = -ab.getRange(); j <= ab.getRange(); j++) {
                                int heur = 0;
                                Cell thisCell = world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j);
                                int dis = world.manhattanDistance(thisCell, hero.getCurrentCell());
                                //System.out.println("dis"+dis+" range"+);
                                if (dis <= ab.getRange()) {
                                    Hero[] targets = world.getAbilityTargets(ab.getName(), hero.getCurrentCell(), thisCell);
//                                System.out.println(thisCell.getRow()+" "+thisCell.getColumn());
//                                for (Hero h:targets){
//                                    System.out.println("1 "+h.getName());
//                                }
//                                    for (Hero h : targets) {
//                                    System.out.println("2 "+h.getName());
                                        heur += attackHeur(world, ab, targets, thisCell,OppHp);

                                }

                                if (heur > max) {
                                    max = heur;
                                    best = ab;
                                    target = thisCell;
                                }
                            }
                        }
//                    for(Hero ene : enemy){
//                        int heur=0;
//                        if(ene.getCurrentCell().getColumn()!=-1){
//                            int dis=world.manhattanDistance(hero.getCurrentCell(),ene.getCurrentCell());
//                            if(dis<= ab.getRange()){
//                                if(ab.isLobbing()){
//                                    heur+=attackHeur(world,ab,enemy,ene.getCurrentCell());
//                                }else if(world.isInVision(hero.getCurrentCell(),ene.getCurrentCell())){
//                                        heur+=attackHeur(world,ab,enemy,ene.getCurrentCell());
//
//                                }
//                            }
//                        }
//                        if(heur>max){
//                            max=heur;
//                            best=ab;
//                            target=ene.getCurrentCell();
//                        }
//                    }

                    } else if (ab.getName() == AbilityName.values()[1] || ab.getName() == AbilityName.values()[4] || ab.getName() == AbilityName.values()[7] || ab.getName() == AbilityName.values()[10]) {
                        //======= goriz ===========
//                        int minHP = 0;
//                        int heur = 0;
//
//
//                        for (Hero ene : enemy) {
//                            int maxThisHero = 0;
//                            if (ene.getCurrentCell().getColumn() != -1) {
//                                for (Ability enemyAbility : ene.getAbilities()) {
//                                    //System.out.println("ability choose "+ ene.getName());
//                                    if (enemyAbility.getName() == AbilityName.values()[0] || enemyAbility.getName() == AbilityName.values()[2] || enemyAbility.getName() == AbilityName.values()[3] || enemyAbility.getName() == AbilityName.values()[5] || enemyAbility.getName() == AbilityName.values()[6] || enemyAbility.getName() == AbilityName.values()[9]) {
//                                        if (maxThisHero < enemyAbility.getPower() && world.manhattanDistance(hero.getCurrentCell(), ene.getCurrentCell()) <= enemyAbility.getRange()) {
//                                            if (enemyAbility.isLobbing()) {
//                                                maxThisHero = enemyAbility.getPower();
//                                            } else if (world.isInVision(ene.getCurrentCell(), hero.getCurrentCell())) {
//                                                maxThisHero = enemyAbility.getPower();
//                                            }
//
//                                        }
//                                    }
//                                }
//                                minHP += maxThisHero;
//                                //System.out.println("max This HP :"+maxThisHero);
//                            }
//                        }
//                        Cell bestCell = null;
////                    System.out.println("minHp: "+ minHP);
//                        if (hero.getCurrentHP() < minHP + 10) {
//                            heur += 0;
//                            int miniMax = 0;
//                            for (int i = -ab.getRange(); i <= ab.getRange(); i++) {
//                                for (int j = -ab.getRange(); j <= ab.getRange(); j++) {
//                                    int dis = world.manhattanDistance(world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j), hero.getCurrentCell());
//                                    if (dis <= ab.getRange()) {
//                                        int miniHeur = 0;
//
//                                        for (Hero a : heroes) {
//                                            if (a.getName() == HeroName.HEALER) {
//                                                if (world.manhattanDistance(a.getCurrentCell(), world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j)) <= 4) {
//                                                    miniHeur += 4;
//                                                }
//                                            }else{
//                                                if(world.manhattanDistance(a.getCurrentCell(), world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j)) > 5 ) {
//                                                    miniHeur+=7;
//                                                }
//
//                                            }
//                                        }
//
//                                        if (world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j).isInObjectiveZone()) {
//                                            miniHeur += 10;
//                                        }
//                                        if(world.manhattanDistance(hero.getCurrentCell(), world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j)) > 2 ){
//                                            miniHeur+=5;
//                                        }
//                                        for (Hero ene : enemy) {
//                                            if (world.manhattanDistance(ene.getCurrentCell(), world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j)) > 5 ) {
//                                                miniHeur += 2;
//
//                                            }
//                                        }
//                                        if (miniMax < miniHeur) {
//                                            miniMax = miniHeur;
//                                            bestCell = world.getMap().getCell(hero.getCurrentCell().getRow() + i, hero.getCurrentCell().getColumn() + j);
//                                        }
//                                        //naghesssssssssssssssssssssssssssssss
//                                        //entekhab behtarin makan
//                                    }
//                                }
//                            }
//                            heur += miniMax;
//
//
//                        }
//
//                        if (heur > max) {
//                            max = heur;
//                            best = ab;
//                            target = bestCell;
//                        }


                    } else if (ab.getName() == AbilityName.values()[8] || ab.getName() == AbilityName.values()[11]) {
                        // =========== defa ========

                        for (Hero hero2 : heroes) {
                            int heur = 0;

                            if (/*!hero2.isDodge &&*/ world.manhattanDistance(hero.getCurrentCell(), hero2.getCurrentCell()) <= ab.getRange() && hero.getCurrentHP() < 0.5 * hero.getMaxHP()) {
                                heur += 110;
                                for (Hero ene : enemy) {
                                    if (world.isInVision(ene.getCurrentCell(), hero2.getCurrentCell())) {
                                        heur++;
                                    }
                                }
                                heur += (hero2.getMaxHP() - hero2.getCurrentHP()) / 10;

                            }
                            if (heur > max) {
                                max = heur;
                                best = ab;
                                target = hero2.getCurrentCell();
                            }
                        }
                    }


                }
//            if(best.getName() == AbilityName.values()[1] || best.getName() == AbilityName.values()[4] || best.getName() == AbilityName.values()[7] || best.getName() == AbilityName.values()[10])
//            {
//                hero.isDodge=true;
//            }
                if (target != null && max > 0) {
                    //System.out.println("shoot on"+target.getRow()+" "+target.getColumn());
                    world.castAbility(hero, best, target);
                    Hero[] targets=world.getAbilityTargets(best,hero.getCurrentCell(),target);
                    Hero[] ht=world.getOppHeroes();
                    for(int j=0;j<4;j++){
                        for(Hero ht2:targets){
                            if(ht[j]==ht2){
                                OppHp[j]-=best.getPower();
                            }
                        }
                    }
                }

//        for(Hero h:heroes){
//            h.isDodge=false;
//        }
        }

    }

    private void calculateDis(World world) throws InterruptedException {
        while(!q.isEmpty()){
            Cell c=q.dequeue();
            int x=c.getRow();
            int y=c.getColumn();
            if(distance[x][y+1]==Integer.MAX_VALUE && !world.getMap().getCell(x,y+1).isWall()){
                distance[x][y+1]=distance[x][y]+1;
                q.enqueue(world.getMap().getCell(x,y+1));
            }
            if(distance[x][y-1]==Integer.MAX_VALUE && !world.getMap().getCell(x,y-1).isWall()){
                distance[x][y-1]=distance[x][y]+1;
                q.enqueue(world.getMap().getCell(x,y-1));
            }
            if(distance[x+1][y]==Integer.MAX_VALUE && !world.getMap().getCell(x+1,y).isWall()){
                distance[x+1][y]=distance[x][y]+1;
                q.enqueue(world.getMap().getCell(x+1,y));
            }
            if(distance[x-1][y]==Integer.MAX_VALUE && !world.getMap().getCell(x-1,y).isWall()){
                distance[x-1][y]=distance[x][y]+1;
                q.enqueue(world.getMap().getCell(x-1,y));
            }
        }
    }

//    private AbilityPack moveBest(World world){
//    }

    private void firstPlan(World world, Hero hero){
        int maxHere=Integer.MIN_VALUE;
        for(int i= -4 ; i<=4 ; i++){
            for(int j = -4 ; j<=4 ; j++){
                if(world.getMap().isInMap(hero.getCurrentCell().getRow()+i, hero.getCurrentCell().getColumn()+j)) {
                    int disDiff = distance[hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()] - distance[hero.getCurrentCell().getRow() + i][hero.getCurrentCell().getColumn() + j];

                    if (disDiff > maxHere) {
                        maxHere = disDiff;
                    }
                }
            }
        }
        if(maxHere>=maxJump-4){
            return;
        }


        Cell c=hero.getCurrentCell();
        int x=c.getRow();
        int y=c.getColumn();
        int now=distance[x][y];
        int row=0;
        int column=0;
        int min=4;
        if(distance[x][y+1]<=now && world.getMyHero(x,y+1)==null){
            now=distance[x][y+1];
            min=3;
            row=x;
            column=y+1;
        }
        if(distance[x][y-1]<=now  && world.getMyHero(x,y-1)==null){
            now=distance[x][y-1];
            min=2;
            row=x;
            column=y-1;
        }
        if(distance[x+1][y]<=now && world.getMyHero(x+1,y)==null){
            now=distance[x+1][y];
            min=1;
            row=x+1;
            column=y;
        }
        if(distance[x-1][y]<=now && world.getMyHero(x-1,y)==null){
            now=distance[x-1][y];
            min=0;
            row=x-1;
            column=y;
        }

        if(min!=4){
            if(world.getMyHero(row,column)!=null ){
                min=random.nextInt(4);
            }

            world.moveHero(hero, Direction.values()[min]);}
    }

    private void secondPlan(World world,Hero hero,int heroNum){
        if(hero.getName()==HeroName.HEALER){
            healerMove(world,hero);
        }else if(hero.getName()==HeroName.GUARDIAN){
            guardianMove(world,hero);
            //healerMove(world, hero);
        }else if(hero.getName()==HeroName.BLASTER){
            blasterMove(world,hero,heroNum);
        }else{
            sentryMove(world, hero);
        }
    }

    private void blasterMove(World world,Hero hero,int heroNum){
//        System.out.println("==========start===========");
//        for(int i=0;i<4;i++){
//            System.out.println(next[i].getRow()+" "+next[i].getColumn());
//        }
        Cell[] next=new Cell[4];
        int heur=0;
        int downHeur=0,upHeur=0,rightHeur=0,leftHeur=0,nowHeur=5;
        Map map=world.getMap();
        Cell CCell=hero.getCurrentCell();
        Cell down = map.getCell(CCell.getRow()+1,CCell.getColumn());
        Cell up = map.getCell(CCell.getRow()-1,CCell.getColumn());
        Cell right = map.getCell(CCell.getRow(),CCell.getColumn()+1);
        Cell left = map.getCell(CCell.getRow(),CCell.getColumn()-1);

        if(down.isWall() || world.getMyHero(down)!=null)
            downHeur = -1000;
        if(up.isWall() || world.getMyHero(up)!=null)
            upHeur = -1000;
        if(left.isWall() || world.getMyHero(left)!=null)
            leftHeur = -1000;
        if(right.isWall() || world.getMyHero(right)!=null)
            rightHeur = -1000;

        Hero[] opp=world.getOppHeroes();
        nowHeur-=2*distance[hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()];
        downHeur-=2*distance[down.getRow()][down.getColumn()];
        upHeur-=2*distance[up.getRow()][up.getColumn()];
        leftHeur-=2*distance[left.getRow()][left.getColumn()];
        rightHeur-=2*distance[right.getRow()][right.getColumn()];
        int numberEnemySee=0;
        for(Hero op : opp){//heuristic nesbat be hariff
            if(op.getCurrentCell().getColumn()==-1){
                numberEnemySee++;
                continue;}
            int currentDis =world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell());
            downHeur+=distanceFromOpp(world,down,op,hero);
            upHeur+=distanceFromOpp(world,up,op,hero);
            leftHeur+=distanceFromOpp(world,left,op,hero);
            rightHeur+=distanceFromOpp(world,right,op,hero);
            nowHeur+=distanceFromOpp(world,hero.getCurrentCell(),op,hero);

        }

        Hero [] heroes = world.getMyHeroes();


        int i=-1;
        for(Hero h : heroes){
            i++;
            if(h==hero)continue;
            if(h.getCurrentCell().getColumn()==-1)continue;
            if(next[i]==null){
                downHeur+=distanceFromFriends(world,down,h.getCurrentCell());
                upHeur+=distanceFromFriends(world,up,h.getCurrentCell());
                leftHeur+=distanceFromFriends(world,left,h.getCurrentCell());
                rightHeur+=distanceFromFriends(world,right,h.getCurrentCell());
                nowHeur+=distanceFromFriends(world,hero.getCurrentCell(),h.getCurrentCell());
            }else{
                downHeur+=distanceFromFriends(world,down,next[i]);
                upHeur+=distanceFromFriends(world,up,next[i]);
                leftHeur+=distanceFromFriends(world,left,next[i]);
                rightHeur+=distanceFromFriends(world,right,next[i]);
                nowHeur+=distanceFromFriends(world,hero.getCurrentCell(),next[i]);
            }

        }



        if(down.isInObjectiveZone()){
            downHeur+=10;
        }if(up.isInObjectiveZone()){
            upHeur+=10;
        }if(left.isInObjectiveZone()){
            leftHeur+=10;
        }if(right.isInObjectiveZone()){
            rightHeur+=10;
        }
        if(hero.getCurrentCell().isInObjectiveZone()){
            nowHeur+=10;
        }
        Direction direction=null;
        heur=nowHeur ;
        Cell thisIs=hero.getCurrentCell();
        if(rightHeur>heur){
            heur=rightHeur;
            direction=Direction.RIGHT;
            thisIs=right;
        }
        if(leftHeur>heur){
            heur=leftHeur;
            direction=Direction.LEFT;
            thisIs=left;
        }
        if(upHeur>heur){
            heur=upHeur;
            direction=Direction.UP;
            thisIs=up;
        }
        if(downHeur>heur){
            heur=downHeur;
            direction=Direction.DOWN;
            thisIs=down;
        }
        if(direction!=null ){
            next[heroNum]=thisIs;
            world.moveHero(hero,direction );
        }
//        System.out.println("==========after===========");
//        for(int i=0;i<4;i++){
//            System.out.println(next[i].getRow()+" "+next[i].getColumn());
//        }
//        System.out.println("==========end===========");
    }

    public int distanceFromFriends(World world,Cell direct,Cell other){
        int h=0;
        int dis=world.manhattanDistance(direct,other);
        if(dis>8){
            h+=35;
        }else if(dis>7){
            h+=34;
        }else if(dis>6){
            h+=33;
        }else if(dis>5){
            h+=32;
        }else if(dis>4){
            h+=30;
        }else if(dis>3){
            h+=24;
        }else if(dis>2){
            h+=17;
        }else if(dis>1){
            h+=9;
        }
        return h;
    }

    public int distanceFromOpp(World world,Cell direct,Hero other,Hero mine){
        int h=0;
        int dis=world.manhattanDistance(direct,other.getCurrentCell());
        if(mine.getAbility(AbilityName.BLASTER_BOMB).getRemCooldown()==0){
            if(dis<=5){
                h+=6;
            }else if(dis<=6){
                h+=5;
            }else if(dis<=7){
                h+=4;
            }else if(dis<=8){
                h+=3;
            }else if(dis<=9){
                h+=2;
            }else if(dis<=10){
                h+=1;
            }
        }else if(mine.getAbility(AbilityName.BLASTER_BOMB).getRemCooldown()==0){
            if(dis<=3){
                h+=6;
            }else if(dis<=4){
                h+=5;
            }else if(dis<=5){
                h+=4;
            }else if(dis<=6){
                h+=3;
            }else if(dis<=7){
                h+=2;
            }else if(dis<=8){
                h+=1;
            }else if(dis<=9){
                h+=0;
            }else if(dis<=10){
                h+=-1;
            }else if(dis<=11){
                h+=-2;
            }else if(dis<=12){
                h+=-3;
            }else if(dis<=13){
                h+=-4;
            }else if(dis<=14){
                h+=-5;
            }
        }
        return h;
    }


    private void sentryMove(World world,Hero hero){
        int heur=0;
        int downHeur=0,upHeur=0,rightHeur=0,leftHeur=0,nowHeur=0;
        Map map=world.getMap();
        Cell CCell=hero.getCurrentCell();
        Cell down = map.getCell(CCell.getRow()+1,CCell.getColumn());
        Cell up = map.getCell(CCell.getRow()-1,CCell.getColumn());
        Cell right = map.getCell(CCell.getRow(),CCell.getColumn()+1);
        Cell left = map.getCell(CCell.getRow(),CCell.getColumn()-1);

        if(down.isWall() || world.getMyHero(down)!=null)
            downHeur = -1000;
        if(up.isWall() || world.getMyHero(up)!=null)
            upHeur = -1000;
        if(left.isWall() || world.getMyHero(left)!=null)
            leftHeur = -1000;
        if(right.isWall() || world.getMyHero(right)!=null)
            rightHeur = -1000;

        Hero[] opp=world.getOppHeroes();
        for(Hero op : opp){//heuristic nesbat be hariff
            if(op.getCurrentCell().getColumn()==-1)continue;
            int currentDis =world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell());
            if ( op.getName() == HeroName.BLASTER){

                if(currentDis<=5){
                    nowHeur-=3;
                    if(currentDis>world.manhattanDistance(op.getCurrentCell(),down)){
                        downHeur+=3;
                    }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)){
                        upHeur+=3;
                    }if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)){
                        leftHeur+=3;
                    }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)){
                        rightHeur+=3;
                    }
                }

                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down) ^ world.manhattanDistance(op.getCurrentCell(),down)<=7){
                    downHeur+=2;
                    if(world.manhattanDistance(down,op.getCurrentCell())<=5){
                        downHeur-=3;
                    }
                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)^ world.manhattanDistance(op.getCurrentCell(),up)<=7){
                    upHeur += 2;
                    if(world.manhattanDistance(up,op.getCurrentCell())<=5){
                        upHeur-=3;
                    }

                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)^ world.manhattanDistance(op.getCurrentCell(),left)<=7){
                    leftHeur += 2;
                    if(world.manhattanDistance(left,op.getCurrentCell())<=5){
                        leftHeur-=3;
                    }

                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)^ world.manhattanDistance(op.getCurrentCell(),right)<=7){//////////should be edit
                    rightHeur += 2;
                    if(world.manhattanDistance(right,op.getCurrentCell())<=5){
                        rightHeur-=3;
                    }

                }

                if(world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur+=2;
                    nowHeur+=extrasentryHealth(world,hero.getCurrentCell(),hero,op);
                }if(world.isInVision(down,op.getCurrentCell())){
                    downHeur+=2;
                    downHeur+=extrasentryHealth(world,down,hero,op);
                }if(world.isInVision(up,op.getCurrentCell())){
                    upHeur+=2;
                    upHeur+=extrasentryHealth(world,up,hero,op);
                }if(world.isInVision(left,op.getCurrentCell())){
                    leftHeur+=2;
                    leftHeur+=extrasentryHealth(world,left,hero,op);
                }if(world.isInVision(right,op.getCurrentCell())){
                    rightHeur+=2;
                    rightHeur+=extrasentryHealth(world,right,hero,op);
                }

            }
            else if ( op.getName() == HeroName.GUARDIAN){
                if(world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell())<=5 && world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell())>1){
                    nowHeur+=2;
                    break;
                }
                if(currentDis<=2){
                    nowHeur-=4;
                    if(currentDis<world.manhattanDistance(op.getCurrentCell(),down) ){
                        downHeur+=5;
                    }if(currentDis<world.manhattanDistance(op.getCurrentCell(),up)){
                        upHeur+=5;
                    } if(currentDis<world.manhattanDistance(op.getCurrentCell(),left)){
                        leftHeur+=5;
                    }if(currentDis<world.manhattanDistance(op.getCurrentCell(),right)){
                        rightHeur+=5;
                    }
                    break;

                }
                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down) ^ world.manhattanDistance(op.getCurrentCell(),down)<=7){
                    downHeur+=2;


                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)^ world.manhattanDistance(op.getCurrentCell(),up)<=7){
                    upHeur+=2;


                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)^ world.manhattanDistance(op.getCurrentCell(),left)<=7){
                    leftHeur+=2;


                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)^ world.manhattanDistance(op.getCurrentCell(),right)<=7){
                    rightHeur+=2;
                }

                if(world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur+=2;
                    nowHeur+=extrasentryHealth(world,hero.getCurrentCell(),hero,op);
                }if(world.isInVision(down,op.getCurrentCell())){
                    downHeur+=2;
                    downHeur+=extrasentryHealth(world,down,hero,op);
                }if(world.isInVision(up,op.getCurrentCell())){
                    upHeur+=2;
                    upHeur+=extrasentryHealth(world,up,hero,op);
                }if(world.isInVision(left,op.getCurrentCell())){
                    leftHeur+=2;
                    leftHeur+=extrasentryHealth(world,left,hero,op);
                }if(world.isInVision(right,op.getCurrentCell())){
                    rightHeur+=2;
                    rightHeur+=extrasentryHealth(world,right,hero,op);
                }


            }else if ( op.getName() == HeroName.HEALER){
                if(world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur+=2;
                    nowHeur+=extrasentryHealth(world,hero.getCurrentCell(),hero,op);
                }if(world.isInVision(down,op.getCurrentCell())){
                    downHeur+=2;
                    downHeur+=extrasentryHealth(world,down,hero,op);
                }if(world.isInVision(up,op.getCurrentCell())){
                    upHeur+=2;
                    upHeur+=extrasentryHealth(world,up,hero,op);
                }if(world.isInVision(left,op.getCurrentCell())){
                    leftHeur+=2;
                    leftHeur+=extrasentryHealth(world,left,hero,op);
                }if(world.isInVision(right,op.getCurrentCell())){
                    rightHeur+=2;
                    rightHeur+=extrasentryHealth(world,right,hero,op);
                }
            }else { // sentry
                if(!world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur+=2;
                    //nowHeur+=extrasentryHealth(world,hero.getCurrentCell(),hero,op);
                }if(!world.isInVision(down,op.getCurrentCell())){
                    downHeur+=2;
                    //downHeur+=extrasentryHealth(world,down,hero,op);
                }if(!world.isInVision(up,op.getCurrentCell())){
                    upHeur+=2;
                    //upHeur+=extrasentryHealth(world,up,hero,op);
                }if(!world.isInVision(left,op.getCurrentCell())){
                    leftHeur+=2;
                    //leftHeur+=extrasentryHealth(world,left,hero,op);
                }if(!world.isInVision(right,op.getCurrentCell())){
                    rightHeur+=2;
                    //rightHeur+=extrasentryHealth(world,right,hero,op);
                }
                if(world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur-=1;
                    nowHeur+=extrasentryHealth(world,hero.getCurrentCell(),hero,op);
                }if(world.isInVision(down,op.getCurrentCell())){
                    downHeur-=1;
                    downHeur+=extrasentryHealth(world,down,hero,op);
                }if(world.isInVision(up,op.getCurrentCell())){
                    upHeur-=1;
                    upHeur+=extrasentryHealth(world,up,hero,op);
                }if(world.isInVision(left,op.getCurrentCell())){
                    leftHeur-=1;
                    leftHeur+=extrasentryHealth(world,left,hero,op);
                }if(world.isInVision(right,op.getCurrentCell())){
                    rightHeur-=1;
                    rightHeur+=extrasentryHealth(world,right,hero,op);
                }
            }
        }
        for(Hero khodi: world.getMyHeroes()){
            int currentDis=world.manhattanDistance(hero.getCurrentCell(),khodi.getCurrentCell());
            if(khodi.getName()==HeroName.HEALER){
                if(currentDis>world.manhattanDistance(khodi.getCurrentCell(),down) ^ world.manhattanDistance(khodi.getCurrentCell(),down)<=4){
                    downHeur+=2;
                }if(currentDis>world.manhattanDistance(khodi.getCurrentCell(),up)^ world.manhattanDistance(khodi.getCurrentCell(),up)<=4){
                    upHeur += 2;
                } if(currentDis>world.manhattanDistance(khodi.getCurrentCell(),left)^ world.manhattanDistance(khodi.getCurrentCell(),left)<=4){
                    leftHeur += 2;
                }if(currentDis>world.manhattanDistance(khodi.getCurrentCell(),right)^ world.manhattanDistance(khodi.getCurrentCell(),right)<=4){//////////should be edit
                    rightHeur += 2;
                }
            }
        }
        if(down.isInObjectiveZone()){
            downHeur+=10;
        }if(up.isInObjectiveZone()){
            upHeur+=10;
        }if(left.isInObjectiveZone()){
            leftHeur+=10;
        }if(right.isInObjectiveZone()){
            rightHeur+=10;
        }if(CCell.isInObjectiveZone()){
            nowHeur+=10;
        }
        Direction direction=null;
        heur=nowHeur*2;
//        if(hero.getCurrentCell().isInObjectiveZone()){
//            heur+=5;
//        }

        if(rightHeur>heur){
            heur=rightHeur;
            direction=Direction.RIGHT;
        }
        if(leftHeur>heur){
            heur=leftHeur;
            direction=Direction.LEFT;
        }
        if(upHeur>heur){
            heur=upHeur;
            direction=Direction.UP;
        }
        if(downHeur>heur){
            heur=downHeur;
            direction=Direction.DOWN;
        }
        if(direction!=null){
            world.moveHero(hero,direction );
        }

    }

    private void healerMove(World world,Hero current){
        Hero[] canSee=world.getOppHeroes();

        Map map = world.getMap();

        int downHeur=0,upHeur=0,leftHeur=0,rightHeur=0;
        int nowHeur=0;
        Cell CCell = current.getCurrentCell();
        Cell down = map.getCell(CCell.getRow()+1,CCell.getColumn());
        Cell up = map.getCell(CCell.getRow()-1,CCell.getColumn());
        Cell right = map.getCell(CCell.getRow(),CCell.getColumn()+1);
        Cell left = map.getCell(CCell.getRow(),CCell.getColumn()-1);
        if(down.isWall() || world.getMyHero(down)!=null)
            downHeur = -1000;
        if(up.isWall() || world.getMyHero(up)!=null)
            upHeur = -1000;
        if(left.isWall() || world.getMyHero(left)!=null)
            leftHeur = -1000;
        if(right.isWall() || world.getMyHero(right)!=null)
            rightHeur = -1000;


        for (Hero hero : world.getMyHeroes()){
            Cell heroCell = hero.getCurrentCell();
            if(world.manhattanDistance(heroCell,CCell)<=4){
                nowHeur+=3;
                if(hero.getCurrentHP()<100){
                    nowHeur+=5;
                }
            }

            if((world.manhattanDistance(heroCell, down) <= 3) ^ world.manhattanDistance(heroCell,CCell)>world.manhattanDistance(heroCell,down)){
                downHeur += 3;

                if(hero.getCurrentHP()<=100){
                    downHeur+=5;
                }
            }if((world.manhattanDistance(heroCell, up) <= 4)^ world.manhattanDistance(heroCell,CCell)>world.manhattanDistance(heroCell,up)){
                upHeur += 3;
                if(hero.getCurrentHP()<=100){
                    upHeur+=5;
                }
            }if((world.manhattanDistance(heroCell, right) <= 4)^ world.manhattanDistance(heroCell,CCell)>world.manhattanDistance(heroCell,left)){
                rightHeur += 3;
                if(hero.getCurrentHP()<=100){
                    rightHeur+=5;
                }
            }if((world.manhattanDistance(heroCell, left) <= 4)^ world.manhattanDistance(heroCell,CCell)>world.manhattanDistance(heroCell,right)){
                leftHeur += 3;
                if(hero.getCurrentHP()<=100){
                    leftHeur+=5;
                }
            }
        }

        if(down.isInObjectiveZone()){
            downHeur+=5;
        }if(up.isInObjectiveZone()){
            upHeur+=5;
        }if(left.isInObjectiveZone()){
            leftHeur+=5;
        }if(right.isInObjectiveZone()){
            rightHeur+=5;
        }if(CCell.isInObjectiveZone()){
            nowHeur+=5;
        }

        for(Hero opp : canSee){
            Cell oppCell = opp.getCurrentCell();
            if(opp.getName() == HeroName.SENTRY) {
                if (!world.isInVision(CCell, opp.getCurrentCell()) && world.isInVision(down,oppCell))
                    downHeur -= 2;
                if (!world.isInVision(CCell, opp.getCurrentCell()) && world.isInVision(up, oppCell))
                    upHeur -= 2;
                if (!world.isInVision(CCell, opp.getCurrentCell()) && world.isInVision(right, oppCell))
                    rightHeur -= 2;
                if (!world.isInVision(CCell, opp.getCurrentCell()) && world.isInVision(left, oppCell) )
                    leftHeur -= 2;
                if (world.isInVision(CCell, opp.getCurrentCell()) && !world.isInVision(down, oppCell))
                    downHeur += 2;
                if (world.isInVision(CCell, opp.getCurrentCell()) && !world.isInVision(up, oppCell))
                    upHeur += 2;
                if (world.isInVision(CCell, opp.getCurrentCell()) && !world.isInVision(right, oppCell))
                    rightHeur += 2;
                if (world.isInVision(CCell, opp.getCurrentCell()) && !world.isInVision(left, oppCell))
                    leftHeur += 2;
                if(!world.isInVision(CCell,oppCell)){
                    nowHeur+=2;
                }
            }
            if(opp.getName() == HeroName.BLASTER ) {
                if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,down) ^ world.manhattanDistance(CCell,oppCell)>6){
                    downHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,up) ^ world.manhattanDistance(CCell,oppCell)>6){
                    upHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,left) ^ world.manhattanDistance(CCell,oppCell)>6){
                    leftHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,right) ^ world.manhattanDistance(CCell,oppCell)>6){
                    rightHeur+=3;
                }
                if (world.manhattanDistance(oppCell, CCell) <= 5)
                    nowHeur -= 2;
            }
            if(opp.getName()== HeroName.GUARDIAN){
                if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,down) ^ world.manhattanDistance(CCell,oppCell)>2){
                    downHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,up) ^ world.manhattanDistance(CCell,oppCell)>2){
                    upHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,left) ^ world.manhattanDistance(CCell,oppCell)>2){
                    leftHeur+=3;
                }if(world.manhattanDistance(oppCell,CCell)<world.manhattanDistance(oppCell,right) ^ world.manhattanDistance(CCell,oppCell)>2){
                    rightHeur+=3;
                }
                if (world.manhattanDistance(oppCell, CCell) <= 2)
                    nowHeur -= 2;
            }
        }
        Direction direction=null;
//        heur=nowHeur*2 ;
//        if(hero.getCurrentCell().isInObjectiveZone()){
//            heur+=2;
//        }
        int heur=nowHeur*5/2;
        if(downHeur>heur){
            heur=downHeur;
            direction=Direction.DOWN;
        }
        if(upHeur>heur){
            heur=upHeur;
            direction=Direction.UP;
        }
        if(rightHeur>heur){
            heur=rightHeur;
            direction=Direction.RIGHT;
        }
        if(leftHeur>heur){
            heur=leftHeur;
            direction=Direction.LEFT;
        }
        if(direction!=null){
            world.moveHero(current,direction );
        }
    }

    private void guardianMove(World world,Hero hero){
        int heur=0;
        int downHeur=0,upHeur=0,rightHeur=0,leftHeur=0,nowHeur=0;
        Map map=world.getMap();
        Cell CCell=hero.getCurrentCell();
        Cell down = map.getCell(CCell.getRow()+1,CCell.getColumn());
        Cell up = map.getCell(CCell.getRow()-1,CCell.getColumn());
        Cell right = map.getCell(CCell.getRow(),CCell.getColumn()+1);
        Cell left = map.getCell(CCell.getRow(),CCell.getColumn()-1);

        if(down.isWall() || world.getMyHero(down)!=null)
            downHeur = -1000;
        if(up.isWall() || world.getMyHero(up)!=null)
            upHeur = -1000;
        if(left.isWall() || world.getMyHero(left)!=null)
            leftHeur = -1000;
        if(right.isWall() || world.getMyHero(right)!=null)
            rightHeur = -1000;

        Hero[] opp=world.getOppHeroes();
        for(Hero op : opp){//heuristic nesbat be hariff
            if(op.getCurrentCell().getColumn()==-1)continue;
            int currentDis =world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell());
            if ( op.getName() == HeroName.BLASTER){
                if((hero.getCurrentHP()< 20 && world.isInVision(hero.getCurrentCell(),op.getCurrentCell())) || ( hero.getCurrentHP()<40) ){
                    nowHeur=-6;
                }

                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down)){
                    downHeur+=2;
                    downHeur+=extraGuardianHealth(world,down,hero,op);
                    if((hero.getCurrentHP()< 20 && world.isInVision(down,op.getCurrentCell())) || (hero.getCurrentHP()<40) ){
                        downHeur=-6;
                    }
                    if(world.manhattanDistance(op.getCurrentCell(),down)<=1){
                        downHeur+=2;
                    }
                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)){
                    upHeur+=extraGuardianHealth(world,up,hero,op);
                    upHeur+=2;
                    if((hero.getCurrentHP()< 20 && world.isInVision(up,op.getCurrentCell())) || (hero.getCurrentHP()<40) ){
                        upHeur=-6;
                    }
                    if(world.manhattanDistance(op.getCurrentCell(),up)<=1){
                        upHeur+=2;
                    }
                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)){
                    leftHeur+=extraGuardianHealth(world,left,hero,op);
                    leftHeur+=2;
                    if((hero.getCurrentHP()< 20 && world.isInVision(left,op.getCurrentCell())) || (hero.getCurrentHP()<40) ){
                        leftHeur=-6;
                    }
                    if(world.manhattanDistance(op.getCurrentCell(),left)<=1){
                        leftHeur+=2;
                    }
                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)){
                    rightHeur+=2;
                    rightHeur+=extraGuardianHealth(world,right,hero,op);
                    if((hero.getCurrentHP()< 20 && world.isInVision(right,op.getCurrentCell())) || (hero.getCurrentHP()<40) ){
                        rightHeur=-6;
                    }
                    if(world.manhattanDistance(op.getCurrentCell(),right)<=1){
                        rightHeur+=2;
                    }
                }

            }else if ( op.getName() == HeroName.GUARDIAN){
                if(hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0 && world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell())<=1 ){
                    nowHeur+=4;
                    if(hero.getCurrentHP()>op.getCurrentHP()){
                        nowHeur+=3;
                    }
                    break;
                }
                if(world.manhattanDistance(op.getCurrentCell(),hero.getCurrentCell())<=1){
                    nowHeur-=1;
                    if(hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()!=0){
                        nowHeur-=2;
                    }
                    if(hero.getCurrentHP()<op.getCurrentHP()){
                        nowHeur-=3;
                        if(currentDis<world.manhattanDistance(op.getCurrentCell(),down) ){
                            downHeur+=2;
                        }if(currentDis<world.manhattanDistance(op.getCurrentCell(),up)){
                            upHeur+=2;
                        } if(currentDis<world.manhattanDistance(op.getCurrentCell(),left)){
                            leftHeur+=2;
                        }if(currentDis<world.manhattanDistance(op.getCurrentCell(),right)){
                            rightHeur+=2;
                        }
                    }
                    break;
                }

                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down) ){
                    downHeur+=2;
                    downHeur+=extraGuardianHealth(world,right,hero,op);
                    if(hero.getCurrentHP()>op.getCurrentHP()){
                        downHeur+=1;
                    }

                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)){
                    upHeur+=2;
                    upHeur+=extraGuardianHealth(world,right,hero,op);
                    if(hero.getCurrentHP()>op.getCurrentHP()){
                        upHeur+=1;
                    }
                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)){
                    leftHeur+=2;
                    leftHeur+=extraGuardianHealth(world,right,hero,op);
                    if(hero.getCurrentHP()>op.getCurrentHP()){
                        leftHeur+=1;
                    }
                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)){
                    rightHeur+=2;
                    rightHeur+=extraGuardianHealth(world,right,hero,op);
                    if(hero.getCurrentHP()>op.getCurrentHP()){
                        rightHeur+=1;
                    }
                }


            }else if ( op.getName() == HeroName.HEALER){
                if(currentDis<=1){
                    nowHeur+=5;
                }
                if(hero.getCurrentHP()<=20){
                    nowHeur-=9;
                    if(currentDis<world.manhattanDistance(op.getCurrentCell(),down) ){
                        downHeur+=2;
                    }if(currentDis<world.manhattanDistance(op.getCurrentCell(),up)){
                        upHeur+=2;
                    } if(currentDis<world.manhattanDistance(op.getCurrentCell(),left)){
                        leftHeur+=2;
                    }if(currentDis<world.manhattanDistance(op.getCurrentCell(),right)){
                        rightHeur+=2;
                    }
                    break;
                }

                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down)){
                    downHeur+=3;
                    downHeur+=extraGuardianHealth(world,down,hero,op);
                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)){
                    upHeur+=3;
                    upHeur+=extraGuardianHealth(world,down,hero,op);

                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)){
                    leftHeur+=3;
                    leftHeur+=extraGuardianHealth(world,down,hero,op);

                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)){
                    rightHeur+=3;
                    rightHeur+=extraGuardianHealth(world,down,hero,op);

                }


            }else { // sentry

                if(!world.isInVision(hero.getCurrentCell(),op.getCurrentCell())){
                    nowHeur+=3;
                    if(hero.getCurrentHP()<=50){
                        nowHeur+=4;
                    }
                }if(!world.isInVision(down,op.getCurrentCell())){
                    downHeur+=3;
                    if(hero.getCurrentHP()<=50){
                        downHeur+=4;
                    }
                }if(!world.isInVision(up,op.getCurrentCell())){
                    upHeur+=3;
                    if(hero.getCurrentHP()<=50){
                        upHeur+=4;
                    }
                }if(!world.isInVision(left,op.getCurrentCell())){
                    leftHeur+=3;
                    if(hero.getCurrentHP()<=50){
                        leftHeur+=4;
                    }
                }if(!world.isInVision(right,op.getCurrentCell())){
                    rightHeur+=3;
                    if(hero.getCurrentHP()<=50){
                        rightHeur+=4;

                    }
                }

                if(currentDis>world.manhattanDistance(op.getCurrentCell(),down) ){
                    if(hero.getCurrentHP()>60 && (hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0 )){
                        downHeur+=2;
                    }
                    downHeur+=extraGuardianHealth(world,down,hero,op);

                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),up)){

                    if(hero.getCurrentHP()>60 && (hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0 )){
                        upHeur+=2;
                    }
                    upHeur+=extraGuardianHealth(world,down,hero,op);

                } if(currentDis>world.manhattanDistance(op.getCurrentCell(),left)){

                    if(hero.getCurrentHP()>60 && (hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0)){
                        leftHeur+=2;
                    }
                    leftHeur+=extraGuardianHealth(world,down,hero,op);

                }if(currentDis>world.manhattanDistance(op.getCurrentCell(),right)){
                    if(hero.getCurrentHP()>60 && (hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0)){
                        rightHeur+=2;
                    }
                    rightHeur+=extraGuardianHealth(world,down,hero,op);

                }
            }

        }
        for ( Hero otherHero : world.getMyHeroes()){
            int currentDis=world.manhattanDistance(otherHero.getCurrentCell(),hero.getCurrentCell());
            if(otherHero==hero){
                break;
            }else if(otherHero.getName()==HeroName.BLASTER){
                if(world.manhattanDistance(otherHero.getCurrentCell(),hero.getCurrentCell())<=4){
                    nowHeur+=3;
                    if(otherHero.getCurrentHP()<50){
                        nowHeur+=3;
                    }
                }if(world.manhattanDistance(otherHero.getCurrentCell(),down)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),down)){
                    downHeur+=3;
                    if(otherHero.getCurrentHP()<50){
                        downHeur+=3;
                    }
                }if(world.manhattanDistance(otherHero.getCurrentCell(),up)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),up)){
                    upHeur+=3;
                    if(otherHero.getCurrentHP()<50){
                        upHeur+=3;
                    }
                }if(world.manhattanDistance(otherHero.getCurrentCell(),left)<=4^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),left)){
                    leftHeur+=3;
                    if(otherHero.getCurrentHP()<50){
                        leftHeur+=3;
                    }
                }if(world.manhattanDistance(otherHero.getCurrentCell(),right)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),down)){
                    rightHeur+=3;
                    if(otherHero.getCurrentHP()<50){
                        rightHeur+=3;
                    }
                }
            }else{//sentry
                if(currentDis<=9 && otherHero.getCurrentHP()<=50){
                    if(world.manhattanDistance(otherHero.getCurrentCell(),hero.getCurrentCell())<=4){
                        nowHeur+=5;
                    }if(world.manhattanDistance(otherHero.getCurrentCell(),down)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),down)){
                        downHeur+=3;
                        if(otherHero.getCurrentHP()<50){
                            downHeur+=3;
                        }
                    }if(world.manhattanDistance(otherHero.getCurrentCell(),up)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),up)){
                        upHeur+=3;

                    }if(world.manhattanDistance(otherHero.getCurrentCell(),left)<=4^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),left)){
                        leftHeur+=3;

                    }if(world.manhattanDistance(otherHero.getCurrentCell(),right)<=4 ^ currentDis>world.manhattanDistance(otherHero.getCurrentCell(),down)){
                        rightHeur+=3;

                    }
                }
            }
        }
        if(down.isInObjectiveZone()){
            downHeur+=5;
        }if(up.isInObjectiveZone()){
            upHeur+=5;
        }if(left.isInObjectiveZone()){
            leftHeur+=5;
        }if(right.isInObjectiveZone()){
            rightHeur+=5;
        }
        Direction direction=null;
        heur=nowHeur*3 ;
        if(hero.getCurrentCell().isInObjectiveZone()){
            heur+=5;
        }
        if(rightHeur>heur){
            heur=rightHeur;
            direction=Direction.RIGHT;
        }
        if(leftHeur>heur){
            heur=leftHeur;
            direction=Direction.LEFT;
        }
        if(upHeur>heur){
            heur=upHeur;
            direction=Direction.UP;
        }
        if(downHeur>heur){
            heur=downHeur;
            direction=Direction.DOWN;
        }
        if(direction!=null){
            world.moveHero(hero,direction );
        }
    }

    int attackHeur(World world,Ability ab , Hero[] enemy, Cell eneCell ,int[] hp){
        int heur=0;
        int i=-1;
        for(Hero eneNew:enemy){
            i++;
            if(eneNew.getCurrentCell().getColumn()!=-1 && world.manhattanDistance(eneCell,eneNew.getCurrentCell())<= ab.getAreaOfEffect()){
                if(hp[i]>0 && hp[i]<=ab.getPower()){
                    heur+=100 + eneNew.getHeroConstantsRespawnTime();
                }else if(hp[i]>0){
                    heur+=(400 - hp[i] + ab.getPower())*70/400+eneNew.getHeroConstantsRespawnTime();
                }
            }
        }
        return heur;

    }

    public int heurOneAction(World world,Hero hero,Ability ability,Hero target){
        return 0;
    }



    private int extraBlasterHealth(World world,Cell direction,Hero hero, Hero op){
        int h=0;
        if(hero.getAbility(AbilityName.BLASTER_BOMB).getRemCooldown()==0){
            if(op.getCurrentHP()<=40){
                h+=5;
            }else if(op.getCurrentHP()<=80){
                h+=4;
            }else if(op.getCurrentHP()<=40){
                h+=3;
            }else {
                h+=2;
            }
        }
        if(hero.getAbility(AbilityName.BLASTER_ATTACK).getRemCooldown()==0){
            if(op.getCurrentHP()<=20){
                h+=5;
            }else if(op.getCurrentHP()<=40){
                h+=4;
            }else if(op.getCurrentHP()<=60){
                h+=3;
            }else if(op.getCurrentHP()<=80){
                h+=2;
            }else if(op.getCurrentHP()<=100){
                h+=1;
            }
        }
        return h;
    }

    private int extraGuardianHealth(World world,Cell direction,Hero hero, Hero op){
        int h=0;
        if(hero.getAbility(AbilityName.GUARDIAN_ATTACK).getRemCooldown()==0){
            if(op.getCurrentHP()<=40){
                h+=5;
            }else if(op.getCurrentHP()<=80){
                h+=4;
            }else if(op.getCurrentHP()<=40){
                h+=3;
            }else {
                h+=2;
            }
        }

        return h;
    }

    private int extrasentryHealth(World world,Cell direction,Hero hero, Hero op){
        int h=0;
        if(hero.getAbility(AbilityName.SENTRY_RAY).getRemCooldown()==0){
            if(op.getCurrentHP()<=40){
                h+=5;
            }else if(op.getCurrentHP()<=80){
                h+=4;
            }else if(op.getCurrentHP()<=40){
                h+=3;
            }else {
                h+=2;
            }
        }
        if(hero.getAbility(AbilityName.SENTRY_ATTACK).getRemCooldown()==0 && world.manhattanDistance(direction,op.getCurrentCell())<=7){
            if(op.getCurrentHP()<=40){
                h+=5;
            }else if(op.getCurrentHP()<=80){
                h+=4;
            }else if(op.getCurrentHP()<=40){
                h+=3;
            }else {
                h+=2;
            }
        }

        return h;
    }

    private Cell checkAttackPlace(Hero hero , World world , AbilityName abilityName){
        int m1 , m2, m3, m4;
        int minX = +100  , maxX=-100 , minY=+100 , maxY=-100;
        int range;
        minX = hero.getCurrentCell().getRow() - hero.getAbility(abilityName).getRange();
        minY = hero.getCurrentCell().getColumn() - hero.getAbility(abilityName).getRange();
        maxX = hero.getCurrentCell().getRow() + hero.getAbility(abilityName).getRange();
        maxY = hero.getCurrentCell().getColumn() + hero.getAbility(abilityName).getRange();
        range = hero.getAbility(abilityName).getRange();
        Map map = world.getMap();
        Hero [] h = null ;
        int mm = 0;
        Cell  cells ; // cells to attack for blaster , sentry , healer ,
        Cell retCell = null;
        for(int i= minX ; i<=maxX ; i++){
            for(int j = minY ; j<maxY ; j++){
                int tedad = 0;
                cells = world.getMap().getCell(i, j);
                h= world.getAbilityTargets(abilityName, hero.getCurrentCell(), cells);
                if(h.length > mm && world.manhattanDistance(hero.getCurrentCell(), cells)<=range){
                    mm = h.length;
                    retCell = cells;
                }

            }
        }
        return  retCell;

    }

}


