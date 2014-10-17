/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Aliaksei Boole
 */
public class ImgUtil
{
    private static Log logger = LogFactory.getLog(ImgUtil.class);

    /**
     * Compare to Images.
     *
     * @param imgUrl1
     * @param imgUrl2
     * @return difference between the two pictures Percentage
     */
    public static double getPercentDiff(String imgUrl1, String imgUrl2)
    {
        BufferedImage img1 = null;
        BufferedImage img2 = null;
        try
        {
            URL url1 = new URL(imgUrl1);
            URL url2 = new URL(imgUrl2);
            img1 = ImageIO.read(url1);
            img2 = ImageIO.read(url2);
        }
        catch (IOException e)
        {
            return 0;
        }
        int width1 = img1.getWidth(null);
        int width2 = img2.getWidth(null);
        int height1 = img1.getHeight(null);
        int height2 = img2.getHeight(null);
        if ((width1 != width2) || (height1 != height2))
        {
            return 0;
        }
        long diff = 0;
        for (int i = 0; i < height1; i++)
        {
            for (int j = 0; j < width1; j++)
            {
                try
                {
                    int rgb1 = img1.getRGB(i, j);
                    int rgb2 = img2.getRGB(i, j);
                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >> 8) & 0xff;
                    int b1 = (rgb1) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    int b2 = (rgb2) & 0xff;
                    diff += Math.abs(r1 - r2);
                    diff += Math.abs(g1 - g2);
                    diff += Math.abs(b1 - b2);
                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.error("Can't get Compare RGB", e);
                    }
                }
            }
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        return p * 100.0;
    }

    /**
     * Create in temp directory temporary  Image.
     * @return
     */
    public static File createTemporaryImg()
    {
        File jpgFile = null;
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawString("TEST", 20, 30);
        try
        {
            jpgFile = File.createTempFile("test", ".jpg");
            ImageIO.write(image, "jpg", jpgFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return jpgFile;
    }


}
