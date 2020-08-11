package TetrisBeta;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import jdk.nashorn.internal.ir.Block;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class GameTetris extends JFrame
{

    final String TITLE_OF_PROGRAM = "Tetris"; //Название программы
    final int BLOCK_SIZE = 25; //Размер блока
    final int ARC_RADIUS = 6; //Степень закруглённости фигур
    final int FIELD_WIDTH = 10; //Ширина игрового поля (в блоках)
    final int FIELD_HEIGHT = 20; //Высота игрового поля (в блоках)
    final int START_LOCATION = 180; //Определяет положение левого верхнего угла нашего окна
    final int FIELD_DX = 7; //Корректива по оси OX
    final int FIELD_DY = 26; //Корректива по оси OY
    final int LEFT = 37; //Скан кода клавиш ↓↓↓
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    int SHOW_DELAY = 150; //Начальная задержка анимации (скорость падения фигуры)
    final int[][][] SHAPES = { //В данном массиве хранятся фигуры тетриса
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f0f0}}, // I
            {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}}, // O
            {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}}, // J
            {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f000}}, // S
            {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a000}}, // L
            {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}}, // T
            {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}}; // Z
    final int[] SCORES = {100, 300, 700, 1500}; //Начисляемые очки (зависит от одновременно сбитых блоков)
    int gameScore = 0; //Очки игры
    int [][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH]; //Размер окна в блоках
    JFrame frame; //Объект основного окна
    Canvas canvasPanel = new Canvas(); //Панель, на которой будем всё рисовать
    Random random = new Random(); //Рандом для выпадения фигур
    Figure figure = new Figure();
    boolean gameOver = false; //определяет окончание игры
    final int[][] GAME_OVER_MSG = {
            {0,1,1,0,0,0,1,1,0,0,0,1,0,1,0,0,0,1,1,0},
            {1,0,0,0,0,1,0,0,1,0,1,0,1,0,1,0,1,0,0,1},
            {1,0,1,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1},
            {1,0,0,1,0,1,0,0,1,0,1,0,1,0,1,0,1,0,0,0},
            {0,1,1,0,0,1,0,0,1,0,1,0,1,0,1,0,0,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,1,0,0,1,0,0,1,0,0,1,1,0,0,1,1,1,0,0},
            {1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0},
            {1,0,0,1,0,1,0,1,0,0,1,1,1,1,0,1,1,1,0,0},
            {1,0,0,1,0,1,1,0,0,0,1,0,0,0,0,1,0,0,1,0},
            {0,1,1,0,0,1,0,0,0,0,0,1,1,0,0,1,0,0,1,0}}; //Надпись GAME OVER


    public static void main(String[] args)
    {
        new GameTetris().go(); //вызывает программу (начало игры)
    }

    void go() //Игровая логика и создания окна
    {
        setTitle(TITLE_OF_PROGRAM); //Создание окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Позволяет закрывать окно на крестик
        //размеры окна и стартовая позиция (координаты левого верхнего угла)
        setBounds(START_LOCATION, START_LOCATION, FIELD_WIDTH * BLOCK_SIZE + FIELD_DX, FIELD_HEIGHT * BLOCK_SIZE + FIELD_DY);
        setResizable(false); //Окно неменяемого размера
        canvasPanel.setBackground(Color.black); //Задний фон - чёрный

        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e) //Метод, передающий нажатия кнопок
            {
                if (!gameOver)
                {
                    if (e.getKeyCode() == DOWN) figure.drop(); //Стрелка вниз = уронить фигуру
                    if (e.getKeyCode() == UP) figure.rotate(); //Стрелка вверх = повернуть фигуру
                    if (e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) figure.move(e.getKeyCode()); //Движение фигуры по горизонтали
                }
                canvasPanel.repaint(); //Если игра не закончилась, рисуем следующий кадр
            }
        });
        getContentPane().add(BorderLayout.CENTER, canvasPanel); //Добавляет компоненты в окно
        setVisible(true); //Окно видимое

        Arrays.fill(mine[FIELD_HEIGHT], 1); //Здесь определяется дно нашего игрового поля

        while (!gameOver) //главный игровой цикл
        {
            try { Thread.sleep(SHOW_DELAY); } catch (Exception e)  //Задержка
            { e.printStackTrace(); }
            canvasPanel.repaint(); //Перерисовка окна
            if (figure.isTouchGround()) //Коснулась ли фигура земли или блоков
                {
                    figure.leaveOnTheGround(); //Оставляем фигуру на земле/блоках
                    checkFilling(); //проверка заполнения строк
                    figure = new Figure(); //создаём новую фигуру
                    gameOver = figure.isCrossGround(); //Не закончилась ли игра (стаканчик заполнен)
                }
            else figure.stepDown(); //фигура падает на ещё один шаг
        }
    }

    void checkFilling() // Проверяет заполнилась ли строка
    {
        int row = FIELD_HEIGHT - 1; //Игровое дно
        int countFillRows = 0;
        while (row > 0)
        {
            int filled = 1;
            for (int col = 0; col < FIELD_WIDTH; col++)
                filled *= Integer.signum(mine[row][col]);
            if (filled > 0)
            {
                countFillRows++;
                if (SHOW_DELAY > 200)
                SHOW_DELAY -= 20; //Ускорение падения
                for (int i = row; i > 0; i--) System.arraycopy(mine[i-1], 0, mine[i], 0, FIELD_WIDTH);
            } else
                row--;
        }
        if (countFillRows > 0) { //Согласно количество убранных строк начисляет очки
            gameScore += SCORES[countFillRows - 1];
            setTitle(TITLE_OF_PROGRAM + " : " + gameScore);
        }
    }

    class Figure //Действия с фигурами
    {
        private ArrayList<Block> figure = new ArrayList<Block>();
        private int[][] shape = new int[4][4]; //Массив, содержащий 1 фигуру
        private int type, size, color; //Тип фигуры, размер и цвет
        private int x = 3, y = 0; //Стартовые координаты

        Figure() //Конструктор
        {
            type = random.nextInt(SHAPES.length); //Вызываем одну из шести фигурок (рандомно)
            size = SHAPES[type][4][0]; //Определяем форму
            color = SHAPES[type][4][1]; //Лпределяем цвет
            if (size == 4) y = -1;
            for (int i = 0; i < size; i++)
                //Копирование фигуры из общего массива в массив shape
                System.arraycopy(SHAPES[type][i],0,shape[i], 0, SHAPES[type][i].length);
            createFromShape();
        }

        void createFromShape()
        {
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    if (shape[y][x] == 1) figure.add(new Block(x+ this.x, y + this.y));
        }

        boolean isTouchGround() //Коснулась ли фигура земли или блоков
        {
            //Проверка каждого блока, не коснулся ли он дна
            for (Block block : figure)
                if (mine[block.getY() + 1][block.getX()] > 0) return true;
            return false;
        }

        boolean isCrossGround() //Не закончилась ли игра (стаканчик заполнен)
        {
            for (Block block : figure) if (mine[block.getY()][block.getX()] > 0) return true;
            return false;
        }

        void leaveOnTheGround() //Оставляем фигуру на земле/блоках
        {
            for (Block block : figure) mine[block.getY()][block.getX()] = color;

        }

        void stepDown() //Фигура падает на ещё один шаг
        {
            //Отдельно по блокам устанавлиаем новую координату Y (предыдущая +1)
            for (Block block : figure) block.setY(block.getY() + 1);
            y++;
        }

        boolean isTouchWall(int direction) //Проверка касания стены фигурой
        {
            for (Block block : figure)
            {
                if (direction == LEFT && (block.getX() == 0 || mine[block.getY()][block.getX() - 1] > 0)) return true;
                if (direction == RIGHT && (block.getX() == FIELD_WIDTH - 1 || mine[block.getY()][block.getX() + 1] > 0)) return true;
            }
            return false;
        }

        void move(int direction) //Перемещение фигуры
        {
            if (!isTouchWall(direction))
            {
                int dx = direction - 38;
                for (Block block : figure) block.setX(block.getX() + dx);
                x += dx;
            }
        }

        void drop() //Моментальное падение фигурки
        {
            while(!isTouchGround()) stepDown();
        }

        boolean isWrongPosition() //Проверка возможен ли поворот фигуры
        {
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    if (shape[y][x] == 1)
                    {
                        if (y + this.y < 0) return true; //Проверка прикосновения со стенами
                        if (x + this.x < 0 || x + this.x > FIELD_WIDTH - 1) return true; //Проверка прикосновения со стенами
                        if (mine[y + this.y][x + this.x] > 0) return true; //Проверка прикосновения с другой фигурой
                    }
            return false;
        }

        void rotate() //Поворачивание фигуры
        {
            for (int i = 0; i < size / 2; i++)
                for (int j = i; j < size - 1 - i; j++)
                {
                    int tmp = shape[size - 1 - j][i];
                    shape[size - 1 - j][i] = shape[size - 1 - i][size - 1 - j];
                    shape[size - 1 - i][size - 1 - j] = shape[j][size - 1 - i];
                    shape[j][size - 1 - i] = shape[i][j];
                    shape[i][j] = tmp;
                }
            if (!isWrongPosition()) //Если фигура в положении, в котором не может повернуться
            {
                figure.clear();
                createFromShape();
            }
        }

        void paint(Graphics g) //Проходит по массиву блоков в фигуре, и каждый такой объект рисует (по блокам)
        {
            for (Block block : figure) block.paint(g, color);
        }

    }

    class Block //Класс, для работы с блоками
    {
        int x, y;
        public Block(int x, int y)  //Конструктор
        {
            setX(x);
            setY(y);
        }

        void setX(int x) {this.x = x; }
        void setY(int y) {this.y = y; }

        int getX() {return x; }
        int getY() {return y; }

        void paint(Graphics g, int color) //Рисующий метод
        {
            g.setColor(new Color(color)); //Устанавливаем цвет
            //Форма нашего прямоугольника
            g.drawRoundRect(x*BLOCK_SIZE+1, y*BLOCK_SIZE+1, BLOCK_SIZE-2,BLOCK_SIZE-2,ARC_RADIUS,ARC_RADIUS);

        }
    }

    public class Canvas extends JPanel //Здесь будем рисовать
    {
        @Override //Инструкция, что будем перекрывать метод paint (переопределение, перекрытие)
        public void paint(Graphics g)
        {
            super.paint(g); //Данная конструкция вызывает метод paint (всё, что было нарисовано до этого)
            for (int x  = 0; x < FIELD_WIDTH; x++)
                for (int y = 0; y < FIELD_HEIGHT; y++) //Прорисовка фигур, лежащих на земле
                    if (mine[y][x] > 0) {
                        g.setColor(new Color(mine[y][x]));
                        g.fill3DRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 1, BLOCK_SIZE - 1, true);
                    }
            if (gameOver) //Конец игры, выводится надпись GAME OVER
            {
                g.setColor(Color.white);
                for (int y = 0; y < GAME_OVER_MSG.length; y++)
                    for (int x = 0; x < GAME_OVER_MSG[y].length; x++)
                        if (GAME_OVER_MSG[y][x] == 1) g.fill3DRect(x*11+10, y*11+160, 10,10,true);
            }
            else
                figure.paint(g);
        }
    }
}