/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.ui.AlgoExplorerTopComponent;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AlgDescriptionImage {
        private static final int STAR_WIDTH = 16;
        private static final int STAR_HEIGHT = 16;
        private static final int STAR_MAX = 5;
        private static final int TEXT_GAP = 5;
        private static final int LINE_GAP = 4;
        private static final int Y_BEGIN = 10;
        private static final int IMAGE_RIGHT_MARIN = 10;
        private final Image greenIcon;
        private final Image grayIcon;
        private Graphics g;
        private final String qualityStr;
        private final String speedStr;
        private int textMaxSize;
        private final AlgorithmFactory factory;

        public AlgDescriptionImage(AlgorithmFactory factory) {
            this.factory = factory;
            greenIcon = ImageUtilities.loadImage("org/bapedis/core/resources/yellow.png");
            grayIcon = ImageUtilities.loadImage("org/bapedis/core/resources/grey.png");
            qualityStr = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.tooltip.quality");
            speedStr = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.tooltip.speed");
        }

        public void paint(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawString(qualityStr, 0, STAR_HEIGHT + Y_BEGIN - 2);
            paintStarPanel(g, textMaxSize + TEXT_GAP, Y_BEGIN, STAR_MAX, factory.getQualityRank());
            g.drawString(speedStr, 0, STAR_HEIGHT * 2 + LINE_GAP + Y_BEGIN - 2);
            paintStarPanel(g, textMaxSize + TEXT_GAP, STAR_HEIGHT + LINE_GAP + Y_BEGIN, STAR_MAX, factory.getSpeedRank());
        }

        public Image getImage() {
            //Image size
            BufferedImage im = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            textMaxSize = 0;
            textMaxSize = Math.max(im.getGraphics().getFontMetrics().stringWidth(qualityStr), textMaxSize);
            textMaxSize = Math.max(im.getGraphics().getFontMetrics().stringWidth(speedStr), textMaxSize);
            int imageWidth = STAR_MAX * STAR_WIDTH + TEXT_GAP + textMaxSize + IMAGE_RIGHT_MARIN;

            //Paint
            BufferedImage img = new BufferedImage(imageWidth, 100, BufferedImage.TYPE_INT_ARGB);
            this.g = img.getGraphics();
            paint(g);
            return img;
        }

        public void paintStarPanel(Graphics g, int x, int y, int max, int value) {
            for (int i = 0; i < max; i++) {
                if (i < value) {
                    g.drawImage(greenIcon, x + i * 16, y, null);
                } else {
                    g.drawImage(grayIcon, x + i * 16, y, null);
                }
            }
        }
    
}
