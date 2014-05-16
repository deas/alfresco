package org.alfresco.share.util;

import java.util.*;

/**
 * @author Aliaksei Boole
 */
public class RandomUtil
{
    private static final Random RANDOM = new Random();

    public static String getRandomString(int length)
    {
        char from[] = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            result.append(from[RANDOM.nextInt((from.length - 1))]);
        }
        return result.toString();
    }

    public static List<String> getRandomListString(int arrayLength, int stringsLength)
    {
        List<String> randomStrings = new ArrayList<String>();
        for (int i = 0; i < arrayLength; i++)
        {
            randomStrings.add(getRandomString(stringsLength));
        }
        return randomStrings;
    }

    public static int getInt(int bound)
    {
        return RANDOM.nextInt(bound);
    }


}
