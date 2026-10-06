import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class usofondo extends JPanel {

    private Image fondo;

    public usofondo() {
        try {
            fondo = ImageIO.read(getClass().getResource("/fondo3.png"));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (fondo != null) {
            g2.drawImage(
                fondo,
                0,
                0,
                getWidth(),
                getHeight(),
                this
            );
        }
    }
}