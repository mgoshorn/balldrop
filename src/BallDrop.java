import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Created by Mitchell Goshorn on 6/9/2017.
 * Project simulates a ball falling, bouncing, and rolling to a stop.
 */
public class BallDrop extends JPanel {

    /**
     * Constants
     * BASE_COLOR - Sets the origin color that will be the most 'well lit' part of the ball after the gradient is applied.
     * SHADOWED_COLOR - Sets the darkest color for the ball possible for the gradient lighting to reach.
     * IS_AFFECTED_BY_GRAVITY - sets whether ball has gravity applied to its velocity
     * SURFACE_FRICTION - A multiplier applied to to tangential deltas on collision
     * ELASTICITY - A multiplier applied upon collision to the reversed delta.
     * LIGHT_OFFSET_MULTIPLIER - Sets the range within the ball in which the center of the gradient light can move relative to
     * the window size.
     * LIGHT_OFFSET - Sets a number to be subtracted after the multiplier which allows the light to travel to a
     * value less than the center of the ball
     * BALL_DIAMETER - Sets balls diameter
     * INITIAL_X_POSITION - Sets starting x coordinate for the ball
     * INITIAL_Y_POSITION - Sets starting y coordinate for the ball
     * TIME_STEP - Timing of updating/repainting in milliseconds.
     */
    private static final Color BASE_COLOR = Color.RED;
    private static final Color SHADOWED_COLOR = new Color(50, 0, 0, 255);
    private static final boolean IS_AFFECTED_BY_GRAVITY = true;
    private static final double SURFACE_FRICTION = 0.99;
    private static final double ELASTICITY = 0.5;
    private static final double LIGHT_OFFSET_MULTIPLIER = 0.4;
    private static final double LIGHT_OFFSET = -(LIGHT_OFFSET_MULTIPLIER/2);
    private static final double BALL_DIAMETER = 300;
    private static final double INITIAL_X_POSITION = 100;
    private static final double INITIAL_Y_POSITION = 100;
    private static final int TIME_STEP = 1000/60;


    /**
     * ball - Ellipse2D that will hold information on the ball
     * dx, dy - ball velocities
     */
    private static Ellipse2D ball;
    private static double dx = 12, dy = dx;

    /**
     * Setup window, display
     */
    private BallDrop() {
        JFrame app = new JFrame();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.add(this);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setUndecorated(true);
        app.setVisible(true);
        this.setBackground(Color.BLACK);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        /*
        Getting setting up ellipse center, colors, dist, and radius to create the radial gradient
        The center of the gradient is offset from the center of the ball relative to the balls position within the window
        to simulate the ball moving relative to an unmoving light source. The range from which the light can move is adjusted
        using LIGHT_OFFSET_MULTIPLIER
         */
        Point2D center = new Point2D.Double(ball.getCenterX() - ball.getWidth() * ((ball.getCenterX() / this.getWidth() * LIGHT_OFFSET_MULTIPLIER) + LIGHT_OFFSET),
                ball.getCenterY() - ball.getWidth() * ((ball.getCenterX() / this.getWidth() * LIGHT_OFFSET_MULTIPLIER) + LIGHT_OFFSET));
        Color[] colors = {BASE_COLOR, SHADOWED_COLOR};
        float[] dist = { 0.0f, 1.0f};
        float radius = (float)ball.getWidth()/2;

        RadialGradientPaint rg = new RadialGradientPaint(center, radius, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        g2d.setPaint(rg);
        g2d.fill(ball);
    }

    /**
     * Updates ball position using deltas, checks for and resolves collisions with the window edges, and applies friction/gravity
     */
    private void update() {
        //update ball position
        ball = new Ellipse2D.Double(ball.getX() + dx, ball.getY() + dy, ball.getWidth(), ball.getHeight());
        //Check for horizontal collision with window edges

        if(ball.getX() < 0 || ball.getMaxX() > this.getWidth()) {

            //Undo horizontal position if collision detected
            ball = new Ellipse2D.Double(ball.getX() - dx, ball.getY(), ball.getWidth(), ball.getHeight());

            //Reverse x-delta multiplied by elasticity
            dx = (-dx * ELASTICITY);
        }

        //Check for vertical collision
        if(ball.getY() < 0 || ball.getMaxY() > this.getHeight()) {

            //Undo vertical position if collision detected
            ball = new Ellipse2D.Double(ball.getX(), ball.getY() - dy, ball.getWidth(), ball.getHeight());

            //Reverse y-delta multiplied by the elasticity
            dy = (-dy * ELASTICITY);

            //Apply surface friction to ball
            dx = dx * SURFACE_FRICTION;
        }

        //Checks if gravity is to be applied, and calls method to apply it
        if(IS_AFFECTED_BY_GRAVITY) {
            addGravity();
        }
    }

    /**
     * Applies gravity to y-delta
     */
    private void addGravity() {
        dy += 9.6 / TIME_STEP;
    }

    public static void main(String[] args) {

        //Initializes display
        BallDrop display = new BallDrop();

        //Initializes ball to initial positions
        ball = new Ellipse2D.Double(INITIAL_X_POSITION, INITIAL_Y_POSITION, BALL_DIAMETER, BALL_DIAMETER);

        //Sets up loop for updating/repainting
        ActionListener looper = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.update();
                display.repaint();
            }
        };

        //Initializes, starts Timer
        Timer timer = new Timer(TIME_STEP, looper);
        timer.start();
    }
}
